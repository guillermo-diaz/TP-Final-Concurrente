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
    private LinkedList<Visitante> lugares = new LinkedList<>();
    public boolean carrera = false;

    public Semaphore avisos;
    private Semaphore lugar;
    private Semaphore finRace;

    //gui
    private Semaphore mutexConsola= new Semaphore(1);

    public Gomon(Parque parq, Carrera r, int id, boolean doble){
        this.p = parq;
        this.c = r;
        this.doble = doble;
        this.id = id;
        this.limite = (doble) ? 2:1;
        lugar = new Semaphore(limite);
        avisos = new Semaphore(0);
        finRace = new Semaphore(0);
    }

    @Override
    public void run() {
        try {
            while (true) {
                esperar_gente();
                c.esperar_largada(this);   
                dormir(10000); //lo que tarda en hacer la carrera
                finRace.release(limite);
                lugares.clear();
                lugar.release(limite);
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

    public void esperar_gente() throws InterruptedException{
        avisos.acquire(limite);

        if (doble){
            escribir(C.VERDE, Color.GREEN,"Visitantes " + getNombres() + " agarraron el gomon doble N°" +id);
        } else{
            escribir(C.VERDE, Color.GREEN,"Visitante " + lugares.getLast().getID() + " agarro el gomon simple N°" +id);
        }
    }

    public void subir(Visitante v) throws InterruptedException{
        lugar.acquire();
        lugares.add(v);
        avisos.release();
    }

    public void iniciar_carrera(Visitante v) throws InterruptedException{
        finRace.acquire(); //espera que termine la carrera y sale
        escribir(C.ROJO, Color.RED,"Visitante " + v.getID() + " dejó el gomon N°" + id);
    }


    public int getID() {
        return id;
    }

    public boolean vacio(){
        return lugar.availablePermits() == limite;
    }

    public String getNombres(){
        String cad = "-";
        for (Visitante v : lugares) {
            cad = cad + v.getID()+" - ";
        }
        return cad + "";
    }
    
    public void dormir(int bound){
        
        Random r = new Random();
        try {
            sleep(r.nextInt(bound));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    private void escribir(String color, Color c, String cad) throws InterruptedException{
        mutexConsola.acquire();
        PrintConsola.print(p.consolas[5], c, cad+"\n");
        mutexConsola.release();

        System.out.println(color+cad+C.RESET);
    }
}
