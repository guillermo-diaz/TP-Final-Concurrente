package recursos;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import config.Config;
import hilos.Visitante;
import util.C;

public class Restaurante {
    private int nro;
    public int capacidad;
    private Semaphore lugares;
    private BlockingQueue<Visitante> cola_visitantes;
    private int lugares_ocupados;

    public Restaurante(int nro){
        this.nro = nro;
        capacidad = Config.CAPACIDAD_RESTAURANTE;
        lugares_ocupados = 0;
        this.cola_visitantes = new LinkedBlockingQueue<>();
    }

    public synchronized void almorzar(Visitante v) throws InterruptedException{
        if (!v.almuerzo){
            cola_visitantes.add(v);
            while(!v.equals(cola_visitantes.peek()) || lugares_ocupados >= capacidad){ //mientras no sea su turno o no hayan lugares espera
                escribir(C.AMARILLO, v.getID()+" espera el almuerzo en restaurante "+nro);
                this.wait();
            }
            lugares_ocupados++;
            cola_visitantes.poll();
            escribir(C.VERDE, v.getID()+" empezó a almorzar en restaurante "+nro);
        }

    }

    public synchronized void salir(Visitante v){
        escribir(C.ROJO, v.getID()+" salió del restaurante "+nro);
        lugares_ocupados--;
        this.notifyAll();
    }

    public synchronized void merendar(Visitante v) throws InterruptedException{
        if (!v.merienda){
            cola_visitantes.add(v);
            while(!v.equals(cola_visitantes.peek()) || lugares_ocupados >= capacidad){ //mientras no sea su turno o no hayan lugares espera
                escribir(C.AMARILLO, v.getID()+" espera la merienda en restaurante "+nro);
                this.wait();
            }
            escribir(C.VERDE, v.getID()+" empezó a merendar en restaurante "+nro);
            
            lugares_ocupados++;
            cola_visitantes.poll();
            
        }
    }

    private void escribir(String color, String cad){
        System.out.println(color+cad+C.RESET);
    }
}
