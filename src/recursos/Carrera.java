package recursos;

import java.awt.Color;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import GUI.PrintConsola;
import config.Config;
import hilos.Gomon;
import hilos.Visitante;
import util.C;

public class Carrera {
    private Parque p;
    private int cant_gomones; // cantidad de gomonos para que salgan
    private int gomones_dobles;
    private int gomones_simples;
    private BlockingQueue<Gomon> gomonesDobleDisponibles;
    private BlockingQueue<Gomon> gomonesSimpleDisponibles;
    private CyclicBarrier barrierCarrera;

    private int total_bicis = 15;
    private int bicis_libres;
    private Lock bicis = new ReentrantLock();
    private Condition bici_disponible = bicis.newCondition();

    private int capacidad_tren = 10;
    private Lock tren = new ReentrantLock();
    private Condition arrancar = tren.newCondition();
    private Condition vacio = tren.newCondition();
    private Condition bajar = tren.newCondition();
    private int pasajeros_actual = 0;
    private LinkedList<Visitante> pasajeros = new LinkedList<>();
   
    private Semaphore mutexConsola1= new Semaphore(1);
    private Semaphore mutexConsola2= new Semaphore(1);

    public Carrera(Parque parq){
        this.p = parq;

        gomones_dobles = Config.CANT_GOMON_DOBLES;
        gomones_simples = Config.CANT_GOMON_SIMPLES;
        cant_gomones = Config.NUMERO_GOMONES;
        gomonesDobleDisponibles = new LinkedBlockingQueue<>(gomones_dobles);
        gomonesSimpleDisponibles = new LinkedBlockingQueue<>(gomones_simples);

        bicis_libres = total_bicis;

        barrierCarrera = new CyclicBarrier(cant_gomones, () -> {
            try {
                escribir_carrera(C.BLANCO, Color.WHITE, "Gomones Listos. Comienza la carrera");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        int ids = 1;
        Gomon g;

        for(int i = 1; i <= gomones_dobles; i++){
            g = new Gomon(p, this,ids, true);
            gomonesDobleDisponibles.add(g);
            ids++;
            g.start();
        }
        for(int i = 1; i <= gomones_simples; i++){
            g = new Gomon(p, this,ids, false);
            gomonesSimpleDisponibles.add(g);
            ids++;
            g.start();
        }
        
    }

  

    //agarra un gomon disponible, elige si quiere doble o simple y luego espera q inicie la carrera
    public Gomon usar_gomon(Visitante v, boolean doble) throws InterruptedException, BrokenBarrierException{  
        Gomon gomonRet;

        if (doble){
            escribir_carrera(C.AMARILLO, Color.YELLOW,"Visitante " + v.getID() + " espera un gomon doble ");    
            gomonRet = gomonesDobleDisponibles.take();   
            if (gomonRet.vacio()){
                agregar_gomon_doble(gomonRet); 
            }          
                     
        } else {
            escribir_carrera(C.AMARILLO, Color.YELLOW,"Visitante " + v.getID() + " espera un gomon simple ");
            gomonRet = gomonesSimpleDisponibles.take();       
          
        }
       
        return gomonRet;
    }

    public void esperar_largada(Gomon g) throws InterruptedException, BrokenBarrierException{
        barrierCarrera.await();
        escribir_carrera(C.PURPLE, Color.MAGENTA, g.getNombres()+" inician la carrera");
    }

    public void acceder_bici(Visitante v) throws InterruptedException{
        bicis.lock();
        while (bicis_libres == 0){
            bici_disponible.await();
        }
        escribir_acceso(C.VERDE, Color.GREEN, v.getID()+" agarró bici");
        bicis_libres--;
        bicis.unlock();
    }

    public void dejar_bici(Visitante v) throws InterruptedException {
        bicis.lock();
        escribir_acceso(C.ROJO, Color.RED, v.getID()+" dejó bici");
        bicis_libres++;
        bicis.unlock();
    }

    public void acceder_tren(Visitante v) throws InterruptedException{
        tren.lock();
        while (pasajeros_actual == capacidad_tren){
            vacio.await();
        }
        pasajeros_actual++;
        pasajeros.add(v); // pasajeros del tren
        escribir_acceso(C.BLANCO, Color.WHITE,v.getID()+" entró al tren");

        if (pasajeros_actual == capacidad_tren){ //es el ultimo, aviso al conductor que arranque
            arrancar.signal();
        }

        bajar.await(); //espero que el tren me diga cuando bajar
        tren.unlock();
    }

    public void esperar_gente() throws InterruptedException{
        tren.lock();
        arrancar.await();
        tren.unlock();
    }

    public void bajar_gente() throws InterruptedException{
        tren.lock();
        pasajeros_actual = 0;
        String cad = "Tren baja a: -";
        while (!pasajeros.isEmpty()){
            Visitante v = pasajeros.poll();
            cad = cad + v.getID()+" - ";
        }
        cad = cad + "\n";
        escribir_acceso(C.BLANCO, Color.WHITE, cad);
        bajar.signalAll();
        tren.unlock();
    }

    public void avisar_gente(){
        tren.lock();
        vacio.signalAll();
        tren.unlock();
    }

    public void agregar_gomon_simple(Gomon g){
        gomonesSimpleDisponibles.offer(g);
    }

    public void agregar_gomon_doble(Gomon g){
        gomonesDobleDisponibles.offer(g);
    }

    public BlockingQueue<Gomon> getGomonesSimples(){
        return this.gomonesSimpleDisponibles;
    }

    public BlockingQueue<Gomon> getGomonesDobles(){
        return this.gomonesDobleDisponibles;
    }

    private void escribir_acceso(String color, Color c, String cad) throws InterruptedException{
        mutexConsola1.acquire();
        PrintConsola.print(p.consolas[4], c, cad+"\n");
        mutexConsola1.release();

        System.out.println(color+cad+C.RESET);
    }
    private void escribir_carrera(String color, Color c, String cad) throws InterruptedException{
        mutexConsola2.acquire();
        PrintConsola.print(p.consolas[5], c, cad+"\n");
        mutexConsola2.release();

        System.out.println(color+cad+C.RESET);
    }
}

