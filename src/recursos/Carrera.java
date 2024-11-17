package recursos;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

import config.Config;
import hilos.Visitante;
import util.C;

public class Carrera {
    private int cant_gomones; // cantidad de gomonos para que salgan
    private int gomones_dobles;
    private int gomones_simples;
    private BlockingQueue<GomonDoble> gomonesDobleDisponibles;
    private BlockingQueue<Gomon> gomonesSimpleDisponibles;
    private CyclicBarrier barrier;
   

    public Carrera(){
        gomones_dobles = Config.CANT_GOMON_DOBLES;
        gomones_simples = Config.CANT_GOMON_SIMPLES;
        cant_gomones = Config.NUMERO_GOMONES;
        gomonesDobleDisponibles = new LinkedBlockingQueue<>(gomones_dobles);
        gomonesSimpleDisponibles = new LinkedBlockingQueue<>(gomones_simples);

        int ids = 1;
        for(int i = gomones_dobles; i > 0; i--){
            gomonesDobleDisponibles.add(new GomonDoble(ids, this));
            ids++;
        }
        for(int i = gomones_simples; i > 0; i--){
            gomonesSimpleDisponibles.add(new Gomon(ids, "SIMPLE", this));
            ids++;
        }

        barrier = new CyclicBarrier(cant_gomones, new Runnable() {
            @Override
            public void run() {
                System.out.println("Todos los gomones están listos, comienza la carrera");
            }
        });

    }

    //agarra un gomon disponible, elige si quiere doble o simple y luego espera q inicie la carrera
    public Gomon usar_gomon(Visitante v, boolean doble) throws InterruptedException, BrokenBarrierException{  
        Gomon gomonRet;

        if (doble){
            GomonDoble gomonD;
            if (gomonesDobleDisponibles.size() == 0){
                escribir(C.AMARILLO,"Visitante " + v.getID() + " espera un gomon doble ");
            }
            gomonD = gomonesDobleDisponibles.take(); 
            gomonRet = gomonD;

            
            if (gomonD.vacio()){
                //lo pone otra vez en la cola y espera un compañero
                gomonesDobleDisponibles.offer(gomonD);
                gomonD.usar();
                escribir(C.VERDE,"Visitante " + v.getID() + " agarro el gomon doble N°" +gomonD.getID());
                barrier.await();
            } else { //si es el acompañante espera que su acompañanente le avise que inició la carrera
                gomonD.usar();
                escribir(C.VERDE,"Visitante " + v.getID() + " agarro el gomon doble N°" +gomonD.getID());
            }
        } else {
            if (gomonesSimpleDisponibles.size() == 0){
                escribir(C.AMARILLO,"Visitante " + v.getID() + " espera un gomon simple ");
            }
            Gomon gomon = gomonesSimpleDisponibles.take();
            gomonRet = gomon;

            gomon.usar();
            escribir(C.VERDE,"Visitante " + v.getID() + " agarro el gomon simple N°" +gomon.getID());
            
            barrier.await();
        }

        return gomonRet;
    }



    public void dejar_gomon(Visitante visitante, Gomon gomon) {
        gomon.dejar();
        escribir(C.ROJO, "Visitante " + visitante.getID() + " dejó el gomon N°" + gomon.getID());
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

