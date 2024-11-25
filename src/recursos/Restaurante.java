package recursos;

import java.awt.Color;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import GUI.PrintConsola;
import config.Config;
import hilos.Visitante;
import util.C;

public class Restaurante {
    private Parque parque;
    private int nro;
    public int capacidad;
    private BlockingQueue<Visitante> cola_visitantes;
    private int lugares_ocupados;
    private Semaphore mutexConsola= new Semaphore(1);

    public Restaurante(int nro, Parque p){
        parque = p;
        this.nro = nro;
        capacidad = Config.CAPACIDAD_RESTAURANTE;
        lugares_ocupados = 0;
        this.cola_visitantes = new LinkedBlockingQueue<>();
    }

    public synchronized void almorzar(Visitante v) throws InterruptedException{
        if (!v.almuerzo){
            cola_visitantes.add(v);
            while(!v.equals(cola_visitantes.peek()) || lugares_ocupados >= capacidad){ //mientras no sea su turno o no hayan lugares espera
                escribir(C.AMARILLO, Color.YELLOW, v.getID()+" espera el almuerzo en restaurante N°"+nro);
                this.wait();
            }
            lugares_ocupados++;
            escribir(C.VERDE, Color.GREEN, v.getID()+" empezó a almorzar en restaurante N°"+nro+": "+lugares_ocupados+"/"+capacidad);
            cola_visitantes.poll();
        }

    }

    public synchronized void salir(Visitante v) throws InterruptedException{
        lugares_ocupados--;
        escribir(C.ROJO, Color.RED, v.getID()+" salió del restaurante N°"+nro+": "+lugares_ocupados+"/"+capacidad);
        this.notifyAll();
    }

    public synchronized void merendar(Visitante v) throws InterruptedException{
        if (!v.merienda){
            cola_visitantes.add(v);
            while(!v.equals(cola_visitantes.peek()) || lugares_ocupados >= capacidad){ //mientras no sea su turno o no hayan lugares espera
                escribir(C.AMARILLO, Color.YELLOW,v.getID()+" espera la merienda en restaurante N°"+nro);
                this.wait();
            }
            lugares_ocupados++;
            escribir(C.VERDE, Color.GREEN, v.getID()+" empezó a merendar en restaurante N°"+nro+": "+lugares_ocupados+"/"+capacidad);
            cola_visitantes.poll();
            
        }
    }

    private void escribir(String color, Color c, String cad) throws InterruptedException{
        mutexConsola.acquire();
        PrintConsola.print(parque.consolas[0], c, cad+"\n");
        mutexConsola.release();

        System.out.println(color+cad+C.RESET);
    }
}
