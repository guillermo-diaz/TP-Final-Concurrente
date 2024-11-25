package recursos;

import java.awt.Color;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import GUI.PrintConsola;
import config.Config;
import hilos.Visitante;
import util.C;

public class Snorkell {
    private Parque parque;
    private int equipos_usados;
    private int total_equipos;

    private Lock mutex = new ReentrantLock();
    private Condition avisar_asistente;
    private Condition equipo_disponible;

    private Semaphore mutexConsola= new Semaphore(1);

    public Snorkell(Parque p){
        parque = p;
        total_equipos = Config.EQUIPOS_SNORKEL;
        equipos_usados = 0;
        avisar_asistente = mutex.newCondition();
        equipo_disponible = mutex.newCondition();
    }

    public void solicitar_equipo(Visitante visitante) throws InterruptedException {
        mutex.lock();

        while (equipos_usados >= total_equipos){ //mientras no hayan equipos espera
            escribir(C.AMARILLO, Color.YELLOW, visitante.getID() + " espera un equipo");
            equipo_disponible.await();
        }
        equipos_usados++;
        escribir(C.VERDE, Color.GREEN, visitante.getID() + " agarró un equipo: "+equipos_usados+"/"+total_equipos);

        mutex.unlock();
    }

    public void dejar_equipo(Visitante visitante) throws InterruptedException {
        mutex.lock();

        equipos_usados--;
        escribir(C.ROJO, Color.RED,visitante.getID()+" devolvió el equipo: "+equipos_usados+"/"+total_equipos);
        avisar_asistente.signalAll(); //le aviso a los asistentes que deje el equipo

        mutex.unlock();
    }

    public void verificar_equipos() throws InterruptedException{
        mutex.lock();

        avisar_asistente.await();
        equipo_disponible.signalAll(); //avisa a todos que hay uno disponible

        mutex.unlock();
    }



    private void escribir(String color, Color c, String cad) throws InterruptedException{
        mutexConsola.acquire();
        PrintConsola.print(parque.consolas[1], c, cad+"\n");
        mutexConsola.release();

        System.out.println(color+cad+C.RESET);
    }
    
    
}



