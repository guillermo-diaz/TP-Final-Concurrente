package recursos;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import config.Config;
import hilos.Visitante;
import util.C;

public class Carrera {
    private int cant_gomones; // cantidad de gomonos para que salgan
    private int gomones_dobles;
    private int gomones_simples;
    private BlockingQueue<GomonDoble> gomonesDobleDisponibles;
    private BlockingQueue<Gomon> gomonesSimpleDisponibles;
    private boolean estado_carrera;
    private Lock carrera = new ReentrantLock();
    private Condition terminada = carrera.newCondition();
    private Condition largada = carrera.newCondition();
    private int gomones_listos;

    private int total_bicis = 15;
    private int bicis_libres;
    private Lock bicis;
    private Condition bici_disponible;

    private int capacidad_tren = 10;
    private Lock tren = new ReentrantLock();
    private Condition arrancar = tren.newCondition();
    private Condition vacio = tren.newCondition();
    private Condition bajar = tren.newCondition();
    private int pasajeros_actual = 0;
    private LinkedList<Visitante> pasajeros = new LinkedList<>();
   

    public Carrera(){
        gomones_dobles = Config.CANT_GOMON_DOBLES;
        gomones_simples = Config.CANT_GOMON_SIMPLES;
        cant_gomones = Config.NUMERO_GOMONES;
        gomonesDobleDisponibles = new LinkedBlockingQueue<>(gomones_dobles);
        gomonesSimpleDisponibles = new LinkedBlockingQueue<>(gomones_simples);
        estado_carrera = false;

        bicis_libres = total_bicis;

        int ids = 1;
        for(int i = 1; i <= gomones_dobles; i++){
            gomonesDobleDisponibles.add(new GomonDoble(ids, this));
            ids++;
        }
        for(int i = 1; i <= gomones_simples; i++){
            gomonesSimpleDisponibles.add(new Gomon(ids, "SIMPLE", this));
            ids++;
        }
    }

  
    

    //agarra un gomon disponible, elige si quiere doble o simple y luego espera q inicie la carrera
    public Gomon usar_gomon(Visitante v, boolean doble) throws InterruptedException, BrokenBarrierException{  
        Gomon gomonRet;
        GomonDoble gomonD;

        if (doble){
            escribir(C.AMARILLO,"Visitante " + v.getID() + " espera un gomon doble ");    
            gomonD = gomonesDobleDisponibles.take(); 
            gomonRet = gomonD;

            carrera.lock();
            if (estado_carrera){
                terminada.await(); //si hay una carrera actual espera
            }
            
            gomonD.usar(v);            
            if (gomonD.solo()){
                //lo pone otra vez en la cola y espera un compañero
                this.agregar_gomon_doble(gomonD);
            } else { 
                gomones_listos++; //ya estan listos los 2 
                escribir(C.VERDE,"Visitantes " + gomonD.getNombres() + " agarraron el gomon doble N°" +gomonD.getID());
            }
        } else {
            escribir(C.AMARILLO,"Visitante " + v.getID() + " espera un gomon simple ");
            gomonRet = gomonesSimpleDisponibles.take();

            carrera.lock();
            if (estado_carrera){
                terminada.await(); //si hay una carrera actual espera
            }
            gomones_listos++;
            escribir(C.VERDE,"Visitante " + v.getID() + " agarro el gomon simple N°" +gomonRet.getID());
            gomonRet.usar(v);
             
        }
        if (gomones_listos == cant_gomones){
            //ya esta lista la carrera, aviso a todos
            estado_carrera = true;
            largada.signalAll();
            escribir(C.BLANCO, "COMIENZA RACE");
        } else {
            largada.await(); //espero a que estemos todos listos
        }
        carrera.unlock(); 

        return gomonRet;
    }



    public void dejar_gomon(Visitante visitante, Gomon gomon) {
        carrera.lock();
        gomones_listos--;
        if (gomones_listos < 1){
            estado_carrera = false;
            terminada.signalAll();
        }
        gomon.dejar(visitante);
        escribir(C.ROJO, "Visitante " + visitante.getID() + " dejó el gomon N°" + gomon.getID());
        carrera.unlock();
    }

    public void acceder_bici(Visitante v) throws InterruptedException{
        bicis.lock();
        while (bicis_libres == 0){
            bici_disponible.await();
        }
        System.out.println(v.getID()+" agarró bici");
        bicis_libres--;
        bicis.unlock();
    }

    public void dejar_bici(Visitante v) {
        bicis.lock();
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
        escribir(C.BLANCO, v.getID()+" entró al tren");

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

    public void bajar_gente(){
        tren.lock();
        pasajeros_actual = 0;
        String cad = "Tren baja a: -";
        while (!pasajeros.isEmpty()){
            Visitante v = pasajeros.poll();
            cad = cad + v.getID()+" - ";
        }
        cad = cad + "\n";
        System.out.println(cad);
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

    public void agregar_gomon_doble(GomonDoble g){
        gomonesDobleDisponibles.offer(g);
    }

    private void escribir(String color, String cad){
        System.out.println(color+cad+C.RESET);
    }

    public BlockingQueue<Gomon> getGomonesSimples(){
        return this.gomonesSimpleDisponibles;
    }

    public BlockingQueue<GomonDoble> getGomonesDobles(){
        return this.gomonesDobleDisponibles;
    }
}

