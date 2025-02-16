package hilos;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;

import GUI.PrintConsola;
import recursos.*;
import util.C;

public class Gomon extends Thread{
    private Parque p;
    private Carrera c;
    private boolean doble;
    private int id;
    private int limite;

    private LugarGomon lugarGomon;

    public Gomon(Parque parq, Carrera r, int id, boolean doble){
        this.p = parq;
        this.c = r;
        this.doble = doble;
        this.id = id;
        this.limite = (doble) ? 2:1;
        lugarGomon = new LugarGomon(parq, r, doble, id);

    }

    @Override
    public void run() {
        try {
            while (true) {
                lugarGomon.esperar_gente(); //espera que se suba gente
                c.esperar_largada(this);   
                dormir(10000); //lo que tarda en hacer la carrera
                lugarGomon.finCarrera(); //avisa a los visitantes que se bajen
                dejarGomon();
            }
        } catch (InterruptedException  | BrokenBarrierException e) {

        } 
    }

    private void dejarGomon(){
        if(doble){
            c.agregar_gomon_doble(this);
        } else {
            c.agregar_gomon_simple(this);
        }
    }

    public LugarGomon lugarGomon(){
        return lugarGomon;
    }


    public int getID() {
        return id;
    }

    public boolean vacio(){
        return lugarGomon.vacio();
    }

    public String getNombres(){
        return lugarGomon.getNombres();
    }
    
    public void dormir(int bound){
        
        Random r = new Random();
        try {
            sleep(r.nextInt(bound));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


}
