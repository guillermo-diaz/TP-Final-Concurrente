package recursos;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import config.*;
import hilos.Visitante;
import util.C;

public class Faro {
   private Lock lock_tobogan = new ReentrantLock(true);
   private Condition esperar_tobogan = lock_tobogan.newCondition();
   private Condition esperar_gente = lock_tobogan.newCondition();
   private Semaphore sem_escalera;
   private int CAPACIDAD_ESCALERA;
   private int tobogan_usado;
   private int CANT_TOBOGANES = 2;
   
   public Faro(){
      CAPACIDAD_ESCALERA = Config.CAPACIDAD_ESCALERA_FARO;
      sem_escalera = new Semaphore(CAPACIDAD_ESCALERA);
      tobogan_usado = 0; 
   }

   public void subir_tobogan(Visitante v) throws InterruptedException{
      lock_tobogan.lock();
      
      while (tobogan_usado >= CANT_TOBOGANES){
         escribir(C.AMARILLO, v.getID()+" espera tobogan");
         esperar_tobogan.await();
      }
      tobogan_usado++;
      escribir(C.AZUL,v.getID()+" entro al tobogan N°"+tobogan_usado);
      lock_tobogan.unlock();
   }

   public void dejar_tobogan(Visitante v){
      lock_tobogan.lock();
      tobogan_usado--;
      escribir(C.PURPLE,v.getID()+" dejo el tobogan ");
      esperar_gente.signal(); //le aviso al admin que ya deje el tobogan
      lock_tobogan.unlock();
   }

   public void subir_escaleras(Visitante v) throws InterruptedException {
      sem_escalera.acquire();
      escribir(C.VERDE, v.getID()+" está subiendo las escaleras");
   }

   public void dejar_escaleras(Visitante v){
      escribir(C.ROJO, v.getID()+" termino de subir las escaleras");
      sem_escalera.release();
   }

   public void administrar() throws InterruptedException {
      lock_tobogan.lock();
      try {
          while (true) {
              // Esperar hasta que haya gente.
              esperar_gente.await(); 
              
              // Indicar a todas las personas que el tobogán está disponible.
              esperar_tobogan.signalAll();
          }
      } finally {
          lock_tobogan.unlock(); 
      }
  }

   private void escribir(String color, String cad){
        System.out.println(color+cad+C.RESET);
    }
}
