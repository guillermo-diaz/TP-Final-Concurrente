package recursos;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import config.Config;
import hilos.Visitante;
import util.C;

public class Snorkell {
    private int cant_equipos;

    private Lock mutex = new ReentrantLock();
    private Condition avisar_asistente;
    private Condition equipo_disponible;

    public Snorkell(){
        cant_equipos = Config.EQUIPOS_SNORKEL;
        avisar_asistente = mutex.newCondition();
        equipo_disponible = mutex.newCondition();
    }

    public void solicitar_equipo(Visitante visitante) throws InterruptedException {
        mutex.lock();

        while (cant_equipos < 1){ //mientras no hayan equipos espera
            escribir(C.AMARILLO, visitante.getID() + " espera un equipo");
            equipo_disponible.await();
        }
        cant_equipos--;
        escribir(C.VERDE, visitante.getID() + " agarró un equipo ");

        mutex.unlock();
    }

    public void dejar_equipo(Visitante visitante) {
        mutex.lock();

        cant_equipos++;
        escribir(C.ROJO, visitante.getID()+" devolvió el equipo ");
        avisar_asistente.signalAll(); //le aviso a los asistentes que deje el equipo

        mutex.unlock();
    }

    public void verificar_equipos() throws InterruptedException{
        mutex.lock();

        avisar_asistente.await();
        equipo_disponible.signalAll(); //avisa a todos que hay uno disponible

        mutex.unlock();
    }



    private void escribir(String color, String cad){
        System.out.println(color+cad+C.RESET);
    }
    
    
}



