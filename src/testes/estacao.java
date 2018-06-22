/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 *
 * @author Nuno
 */
public class estacao extends Agent {
    private long periodo=20000; //ms

    @Override
    protected void setup() {
        
        Object[] args=getArguments();
        String texto=(String) args[0];
        
         
         
         
         // cria behaviour que de 40 em 40 segundos pede novos dados humidade
         
        if (args.length>0 && args!=null) {
            System.out.println("station is requesting new forecast data...");
            
            // cria msg com varios destinatarios, se necessario
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            for (int i = 0; i < args.length; i++) {
                msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
            }
            
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            msg.setContent("hr today");
            
            addBehaviour(new Iniciador(this,msg){
                
                
                
                
                
            });
            
        }else{
            System.out.println("erro no argumento");
        }
        
        
        
        
    }

    private static class Iniciador extends AchieveREInitiator {

        public Iniciador(Agent a, ACLMessage msg) {
            super(a,msg);
            
            
        }
        
        
         @Override
        protected void handleAgree(ACLMessage agree) {
            System.out.println(agree.getSender().getName()+ " forecast concorda e vai enviar dados, aguarde...");
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            System.out.println(refuse.getSender().getName()+" estacao forecast nao tem dados disponiveis!");
        }

        @Override
        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            System.out.println("pedido de forecast n foi entendido pelo destinatario "+notUnderstood.getSender().getName());
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            System.out.println("Forecast recebido de "+inform.getSender().getName()+" ! Dados: "+inform.getContent());
            
            
            
        }
        
        
    }
    
    
    
}
