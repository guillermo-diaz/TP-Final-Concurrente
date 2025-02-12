package recursos;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import GUI.PrintConsola;
import hilos.Visitante;
import util.C;
import java.awt.Color;

public class LugarGomon {

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

    public LugarGomon (Parque parq, Carrera r, boolean doble){
        this.p = parq;
        this.c = r;
        this.doble = doble;
        this.limite = (doble) ? 2:1;
        lugar = new Semaphore(limite);
        avisos = new Semaphore(0);
        finRace = new Semaphore(0);
    }

    //metodos que usa visitante

    public void subir(Visitante v) throws InterruptedException{
        lugar.acquire();
        lugares.add(v);
        avisos.release();
    }

    public void esperarFinCarrera(Visitante v) throws InterruptedException{
        finRace.acquire(); //espera que termine la carrera y sale
        escribir(C.ROJO, Color.RED,"Visitante " + v.getID() + " dejó el gomon N°" + id);
    }



    //metodos que usa gomon
    public void finCarrera(){
        finRace.release(limite);
        lugares.clear();
        lugar.release(limite);
    }


    public void esperar_gente() throws InterruptedException{
        avisos.acquire(limite);

        if (doble){
            escribir(C.VERDE, Color.GREEN,"Visitantes " + getNombres() + " agarraron el gomon doble N°" +id);
        } else{
            escribir(C.VERDE, Color.GREEN,"Visitante " + lugares.getLast().getID() + " agarro el gomon simple N°" +id);
        }
    }


    public String getNombres(){
        String cad = "-";
        for (Visitante v : lugares) {
            cad = cad + v.getID()+" - ";
        }
        return cad + "";
    }

    public boolean vacio(){
        return lugar.availablePermits() == limite;
    }

    
    

    private void escribir(String color, Color c, String cad) throws InterruptedException{
        mutexConsola.acquire();
        PrintConsola.print(p.consolas[5], c, cad+"\n");
        mutexConsola.release();

        System.out.println(color+cad+C.RESET);
    }
}
