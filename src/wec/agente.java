/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wec;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import java.util.Calendar;
import javax.swing.SwingUtilities;

/**
 * agente: agent iniciador ciclico; central estacao participante
 * @author Nuno
 */
public class agente extends Agent {

    private long intervalo = 4000;  // 25000
    private String nome = "C";
    private janela jan;
    private forecastData previsao;
    
    private final String type="forecast";
    private final String service="seawave";
    
    private AID centralAID;

    public agente() {

    }
    
    public AID searchForService(String type,String serviceName){
        
        
        DFAgentDescription dfa= new DFAgentDescription();
        ServiceDescription serv= new ServiceDescription();
        
        serv.setType(type);
        serv.setName(serviceName);
        
        dfa.addServices(serv);
        
        try {
            DFAgentDescription[] agentList= DFService.search(this, dfa);
            
            // by default returns the first AID int the list
            
            System.out.println(getAID().getLocalName() +" diz >>>>> Encontrei "+agentList.length);
            
            if (agentList.length>0) {
                return agentList[0].getName();
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void setup() {
        
//        // publish services on yellow pages
       
       addBehaviour(new TickerBehaviour(this, 4000) {
           @Override
           protected void onTick() {
                centralAID=searchForService(type, service);
                
                if(getTickCount()>1){
                    
                    done();
                }
          }
        });
        
        
        
        previsao=new forecastData(0, "");

        Runnable run = new Runnable() {
            @Override
            public void run() {
                jan = new janela("Dashboard of agent " + getLocalName(),previsao);
                jan.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(run);

        addBehaviour(new TickerBehaviour(this, intervalo) {

            @Override
            protected void onTick() {
                System.out.println("check if any update has been launched. want new data");
                
                if(centralAID!=null){

                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                
                // find which Agent is capable of providing a seawave forecast
                
                
                
                msg.addReceiver(new AID(centralAID.getLocalName(), AID.ISLOCALNAME));
                
                msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                msg.setContent("hr tomorrow");

                addBehaviour(new Iniciador(myAgent, msg, jan,previsao));
                
                }else{
                    System.err.println("Cannot find suitable central Agent!");
                }

            }
        });
    }

    private static class Iniciador extends AchieveREInitiator {

        private janela mygui;
        private forecastData prev;

        public Iniciador(Agent a, ACLMessage msg, janela gui,forecastData fData) {
            super(a, msg);
            this.mygui = gui;
            this.prev=fData;
        }

        // trata a resposta do participante, i.e, o que responde
        @Override
        protected void handleAgree(ACLMessage agree) {
            System.out.println(agree.getSender().getName() + " forecast concorda e vai enviar dados, aguarde...");
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            System.out.println(refuse.getSender().getName() + " estacao forecast nao tem dados disponiveis!");
            
            
            
            prev.setBlank();
            
            Calendar now = Calendar.getInstance();
            int hora = now.get(Calendar.HOUR_OF_DAY);
            int minutos = now.get(Calendar.MINUTE);
            int segundos = now.get(Calendar.SECOND);

            String timestamp = hora + ":" + minutos + ":" + segundos;
            

            Runnable run = new Runnable() {
                @Override
                public void run() {

                   mygui.setError(timestamp+": Falha! Pedido recusado pela central");
                   mygui.setCurrentSetup(String.valueOf(prev.getLevel()));
                    
                }
            };
            SwingUtilities.invokeLater(run);
        }

        @Override
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            System.out.println("pedido de forecast n foi entendido pelo destinatario " + notUnderstood.getSender().getName());
            
            prev.setBlank();
            
            Calendar now = Calendar.getInstance();
            int hora = now.get(Calendar.HOUR_OF_DAY);
            int minutos = now.get(Calendar.MINUTE);
            int segundos = now.get(Calendar.SECOND);

            String timestamp = hora + ":" + minutos + ":" + segundos;
            

            Runnable run = new Runnable() {
                @Override
                public void run() {

                   mygui.setError(timestamp+": Falha! Pedido não satisfeito pela central");
                   mygui.setCurrentSetup(String.valueOf(prev.getLevel()));
                    
                }
            };
            SwingUtilities.invokeLater(run);
            
            
            
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            System.out.println("Forecast recebido de " + inform.getSender().getName() + " ! Dados: " + inform.getContent());

            Calendar now = Calendar.getInstance();
            int hora = now.get(Calendar.HOUR_OF_DAY);
            int minutos = now.get(Calendar.MINUTE);
            int segundos = now.get(Calendar.SECOND);

            String timestamp = hora + ":" + minutos + ":" + segundos;
            String config = inform.getContent();

            Runnable run = new Runnable() {
                @Override
                public void run() {

                    mygui.setCurrentSetup(config);
                    mygui.setLastUpdate(timestamp);
                    mygui.setAddress(myAgent.getName());
                    
                    prev.setLevel(Integer.parseInt(config));
                    prev.setInfo("HR");
                    
                    
                }
            };
            SwingUtilities.invokeLater(run);

        }

    }
}
