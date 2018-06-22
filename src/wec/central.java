/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wec;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import javax.swing.SwingUtilities;

/**
 *
 * @author Nuno
 */
public class central extends Agent {

    private int hrtoday = 10;
    private int hrtomorrow = 95;
    private janelaCentral gui;
    private forecastData previsao;
    
   
    
    public void register(){
        // register provided services on yellow pages, also called DF
        
        DFAgentDescription dfd=new DFAgentDescription();
        dfd.setName(getAID());
        
        // create a service
        ServiceDescription sd = new ServiceDescription();
        sd.setType("forecast");
        sd.setName("seawave");
        dfd.addServices(sd);
        
      ServiceDescription sd2 = new ServiceDescription();
        sd2.setType("forecast");
        sd2.setName("temperature");
        dfd.addServices(sd2);
        
        
        // register on DF
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setup() {

        register();

        // inicia pacote de previsao que sera distribuido aos agentes
        previsao = new forecastData(31, "hr");  // default

        // ********************
        MessageTemplate protocolo = MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        MessageTemplate performativa = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        MessageTemplate filtroTotal = MessageTemplate.and(protocolo, performativa);

        Runnable runnable = new Runnable() {
            public void run() {
                gui = new janelaCentral("Dashboard of central agent " + getLocalName(), previsao);
                gui.setConfig(String.valueOf(previsao.getLevel()));
                gui.setAddress(getName());
                gui.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(runnable);

        System.out.println(getLocalName() + ": waiting for forecast data demand...");

        ACLMessage msg = receive(filtroTotal);

        // falta if msg null????
        addBehaviour(new AchieveREResponder(this, filtroTotal) {
            @Override
            protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
                System.out.println("Central de forecast " + getLocalName() + " recebemos pedido de " + request.getSender().getName());

                String pedido = request.getContent();    // recebe mensagem do tipo "hr tomorrow", exemplo apenas
                String[] elementos = pedido.split(" ");
                String dia = elementos[1];

                if (!previsao.getRefuseVal()) {

                    System.out.println("Central bloqueada: " + previsao.getRefuseVal());

                    if (elementos.length == 2 && (dia.equalsIgnoreCase("today") || dia.equalsIgnoreCase("tomorrow"))) {
                        System.out.println("pedido de forecast aceite");

                        ACLMessage agree = request.createReply();
                        agree.setPerformative(ACLMessage.AGREE);

                        return agree;

                    } else {
                        throw new NotUnderstoodException("Pedido nao entendido ou falha na central");
                    }

                } else {
                    System.out.println("pedido de forecast nao aceite, confirme especificacao");
                    throw new RefuseException("Pedido recusado pela central");
                }

            }

                 @Override
                        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {

                            // vamos enviar a previsao ao solicitante
                            String pedido = request.getContent();
                            String[] elementos = pedido.split(" ");
                            String dia = elementos[1];
                            
                            // para apagar, a informaçao a enviar e a que esta no envelope, if else so para teste

                            if (dia.equalsIgnoreCase("today")) {
                                ACLMessage inform = request.createReply();
                                inform.setPerformative(ACLMessage.INFORM);
                                inform.setContent(Integer.toString(hrtoday));
                                return inform;

                            } else if (dia.equalsIgnoreCase("tomorrow")) {
                                ACLMessage inform = request.createReply();
                                inform.setPerformative(ACLMessage.INFORM);
                                //inform.setContent(Integer.toString(hrtomorrow));
                                inform.setContent(Integer.toString(previsao.getLevel()));
                                return inform;
                            } else {
                                throw new FailureException("nao foi possivel satisfazer o pedido");
                            }
                        }

                    });

                  
                    
                

            
        

    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            System.err.println(e.getMessage());
        }
    }
    
    

}
