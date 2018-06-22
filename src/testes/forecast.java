/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

/**
 *
 * @author Nuno
 */
public class forecast extends Agent {
    private final String foreToday="chuvoso";
    private final String foreTom="solarengo";
    

    @Override
    protected void setup() {
        System.out.println(getLocalName()+": waiting for forecast data demand...");
        
        MessageTemplate protocolo = MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        MessageTemplate performativa = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        MessageTemplate filtroTotal = MessageTemplate.and(protocolo, performativa);
        
        addBehaviour(new Participante(this,filtroTotal));
    }

    private class Participante extends AchieveREResponder {

        public Participante(Agent a, MessageTemplate mt) {
            super(a,mt);
        }
        
        // metodo que aguarda mensagem REQUEST

        @Override
        protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
            System.out.println("Central de forecast "+getLocalName()+ " recebemos pedido de "+request.getSender().getName());
            
            String pedido=request.getContent();
            String[] elementos = pedido.split(" ");
            
            System.out.println("tamanho "+elementos.length);
            if (elementos!=null && elementos.length==3) {
                
               String dia=elementos[2];
               
                
                if(dia.equalsIgnoreCase("today")|| dia.equalsIgnoreCase("tomorrow")){
                    System.out.println("pedido de forecast aceite");
                    ACLMessage agree = request.createReply();
                    agree.setPerformative(ACLMessage.AGREE);
                   
                    return agree;
                }else{
                    System.out.println("pedido de forecast nao aceite, confirme especificacao");
                    throw new RefuseException("nao ha dados para esse dia");
                }
                
                
                
                
            }else{
                throw new NotUnderstoodException("o pedido de forecast nao foi entendido");
            }
            
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            
            // vamos enviar a previsao ao solicitante
            String pedido=request.getContent();
            String[] elementos = pedido.split(" ");
            String dia = elementos[2];
            
            if(dia.equalsIgnoreCase("today")){
                ACLMessage inform = request.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                inform.setContent(foreToday);
                return inform;
                
            }else if(dia.equalsIgnoreCase("tomorrow")){
                ACLMessage inform = request.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                inform.setContent(foreTom);
                return inform;
            }else{
                throw new FailureException("nao foi possivel satisfazer o pedido");
            }
            
            
        }
        
        
        

       
    
    
    
    }
}
