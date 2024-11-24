package recursos;

import java.util.LinkedList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import hilos.Visitante;

public class Gomon {
        private final int id;
        private final String tipo;
        public boolean ocupado;
        public Carrera carrera; //la carrera donde pertenece
        LinkedList<Visitante> lista_visitante = new LinkedList<>();
    
        public Gomon(int id, String tipo, Carrera c) {
            this.id = id;
            this.tipo = tipo;
            this.ocupado = false;
            this.carrera = c;
        }

        public synchronized void usar(Visitante v) throws InterruptedException {
            lista_visitante.add(v);
            while (ocupado) {
                wait(); // Espera hasta que el gomon esté disponible
            }
            ocupado = true;
        }

        public synchronized void dejar(Visitante v) {
            lista_visitante.poll();
            ocupado = false;
            notifyAll(); // Notifica que el gomon está disponible
            carrera.agregar_gomon_simple(this); //lo pone en la carrera que corresponde
        }
    
        public int getID() {
            return id;
        }
    
        public String getTipo() {
            return tipo;
        }

        public boolean vacio(){
            return !ocupado;
        }
}    

class GomonDoble extends Gomon {

    private int cant_actual;
    private Semaphore lugares = new Semaphore(2);
    private Semaphore espera = new Semaphore(0);


    public GomonDoble(int id, Carrera c) {
        super(id, "Doble", c);
        cant_actual = 0;
    }


    public void usar(Visitante v) throws InterruptedException {
        lugares.acquire();
        
        lista_visitante.add(v);
        synchronized (this) {
            cant_actual++;
            if (cant_actual == 2) {
                ocupado = true;
            }
        }
    }

    public void dejar(Visitante v)  {
        lista_visitante.remove(v);
        synchronized (this) {
            cant_actual--;
            if (cant_actual == 0){ //el ultimo en salir
                ocupado = false;
                carrera.getGomonesDobles().offer(this); //lo pone en la carrera que corresponde
            } 
        }
        lugares.release();
    }

    public void esperar_aviso() throws InterruptedException{
        espera.acquire();
    }

    public void avisar(){
        espera.release();
    }

    @Override
    public synchronized boolean vacio(){
        return cant_actual == 0;
    }

    public synchronized boolean solo(){
        return cant_actual == 1;
    }

    public int getCantActual(){
        return cant_actual;
    }

    public String getNombres(){
        String cad = "-";
        for (Visitante v : lista_visitante) {
            cad = cad + v.getID()+" - ";
        }
        return cad + "";
    }

    
    

}

