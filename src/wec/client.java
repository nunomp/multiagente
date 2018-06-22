/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wec;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import static java.lang.Thread.interrupted;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/**
 *
 * @author Nuno
 */
public class client extends Thread {

    private final int port = 9090;
    private final String host = "localhost";
    private forecastData envelope;
    private janelaCentral mygui;

    private Socket Sockclient;

    public client(janelaCentral gui,forecastData fd) {
        envelope=fd;
        mygui=gui;
    }

    @Override
    public void run() {
        System.out.println("cliente Running thread " + Thread.currentThread().toString());
        System.out.println("cliente escutando porta " + port);

        try {
            while (!interrupted()) {
                System.err.println("            cliente interrompido?   " + interrupted());

                //setStatusGUI(serviceOK +port);
                

                try {
                    while (true) {
                        
                        TimeUnit.SECONDS.sleep(2);
                        
                        try {
                            Sockclient = new Socket(host, port);
                            BufferedReader input = new BufferedReader(new InputStreamReader(Sockclient.getInputStream()));
                            String answer = input.readLine();
                            System.out.println("******** ***** ******* Cliente recebeu msg: " + answer + " ********");

                            // *******************************
                            
                            envelope.setLevel(Integer.parseInt(answer));
                            updateReceivedData(answer);
                            
                            // ****************************
                            
                        } catch (java.net.ConnectException erroc) {
                            System.err.println("Connection refused. Server may be down! ");
                        } finally {

                        }
                    }
                } catch (Exception e) {
                } finally {

                    //setStatusGUI(serviceNok);
                }

            }
        } catch (Exception e) {
            System.out.println("interrompido");
        }
        System.out.println("Acabei");
    }

    public void terminate() {

        try {
            

            //setStatusGUI(serviceNok);
        } catch (Exception e) {
            System.err.println("fui fechado " + e.getMessage());
        }

    }
    
    public void updateReceivedData(String txt){
        // updates gui with latest data received from MATLAB instance, or other app that uses same TCPIP socket
        
        Runnable run = new Runnable() {
            @Override
            public void run() {
                mygui.setReceivedData(txt);
            }
        };
        
        SwingUtilities.invokeLater(run);
        
    }

}
