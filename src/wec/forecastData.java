/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wec;

/**
 *
 * @author Nuno
 */
public class forecastData {
    private int level;
    private String info;
    private boolean notReady=false;
    
    public forecastData(int value,String category){
        this.level=value;
        this.info=category;
    }
    
    public forecastData(int value,String category,boolean readyVal){
        this.level=value;
        this.info=category;
        this.notReady=readyVal;
    }
    
    public synchronized void setLevel(int value){
        this.level=value;
    }
    
    public synchronized void setBlank(){
        this.level=0;
        this.info="NA";
        this.notReady=false;
    }
    
    public synchronized void setInfo(String newCat){
        this.info=newCat;
    }
    
    public synchronized void setRefuse(boolean refVal){
        this.notReady=refVal;
    }
    
    
    public synchronized int getLevel(){
        return this.level;
    }
    
    public synchronized String getInfo(){
        return this.info;
    }
    
    public synchronized boolean getRefuseVal(){
        return this.notReady;
    }
    
    
    
}
