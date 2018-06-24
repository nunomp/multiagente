/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Nuno
 */
public class backWorker extends Thread {
    private int contador=0;

    public backWorker() {
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {    
                contador++;
                System.out.println("Contador "+contador);
                TimeUnit.SECONDS.sleep(2);
            }
        } catch (InterruptedException e) {
            System.err.println("fui interrompido "+e.getMessage());
        }
        System.out.println("Acabei");
    }
    
    
}
