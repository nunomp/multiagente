/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wec;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import javax.swing.SwingUtilities;

/**
 *
 * @author Nuno
 */
public class daemon extends Thread {

    private int port;
    private ServerSocket listener;
    private janela launchingGUI;
    private final String mensagem="bolacha";
    private forecastData envelope;
    private final String serviceOK="Service running @ ";
    private final String serviceNok="Service stopped";

    public daemon(janela gui,int port,forecastData data) {
        this.port = port;
        this.launchingGUI=gui;
        this.envelope=data;

    }

    @Override
    public void run() {
        System.out.println("Server Running thread " + Thread.currentThread().toString());
        System.out.println("Servidor escutando porta " + port);

        try {
            while (!interrupted()) {
                System.err.println("interrompido?   "+interrupted());

                listener = new ServerSocket(port);
                setStatusGUI(serviceOK +port);

                try {
                    while (true) {
                        Socket socket = listener.accept();

                        try {
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println(envelope.getLevel());
                        } catch (Exception e) {
                        } finally {
                            socket.close();
                        }
                    }
                } catch (Exception e) {
                } finally {
                    listener.close();
                    setStatusGUI(serviceNok);
                }

            }
        } catch (Exception e) {
            System.out.println("interrompido");
        }
        System.out.println("Acabei");
    }

    public void terminate() {

        try {
            this.listener.close();
            
            setStatusGUI(serviceNok);

        } catch (Exception e) {
            System.err.println("fui fechado "+e.getMessage());
        }
    }
    
    public void setStatusGUI(String status){
        
        Runnable run = new Runnable() {
            @Override
            public void run() {
                launchingGUI.setWorkerStatus(status);
                
               
                
                if (status.equals(serviceNok)) {
                    launchingGUI.setServiceOK(false);
                }else{
                    launchingGUI.setServiceOK(true);
                }
                
                
                
            }
        };
        SwingUtilities.invokeLater(run);
    }
    
    

}
