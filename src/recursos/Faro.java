package recursos;
import java.awt.Color;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import config.*;
import hilos.Visitante;
import util.C;
import GUI.PrintConsola;

public class Faro {
   private Parque parque;
   private Lock lock_tobogan = new ReentrantLock(true);
   private Condition esperar_tobogan = lock_tobogan.newCondition();
   private Condition esperar_gente = lock_tobogan.newCondition();
   private Semaphore sem_escalera;
   private int CAPACIDAD_ESCALERA;
   private int tobogan_usado;
   private int CANT_TOBOGANES = 2;
   private int escalera_actual = 0;

   private Semaphore mutexConsola= new Semaphore(1);
   
   public Faro(Parque p){
      parque = p;
      CAPACIDAD_ESCALERA = Config.CAPACIDAD_ESCALERA_FARO;
      sem_escalera = new Semaphore(CAPACIDAD_ESCALERA);
      tobogan_usado = 0; 
   }

   public void subir_tobogan(Visitante v) throws InterruptedException{
      lock_tobogan.lock();
      
      while (tobogan_usado >= CANT_TOBOGANES){
         escribir(C.AMARILLO, Color.YELLOW, v.getID()+" espera tobogan");
         esperar_tobogan.await();
      }
      tobogan_usado++;
      
      escribir(C.VERDE, Color.green,v.getID()+" entro a un tobogan "+tobogan_usado+"/"+CANT_TOBOGANES);
      lock_tobogan.unlock();
   }

   public void dejar_tobogan(Visitante v) throws InterruptedException{
      lock_tobogan.lock();
      tobogan_usado--;
      escribir(C.ROJO, Color.red, v.getID()+" dejo un tobogan "+tobogan_usado+"/"+CANT_TOBOGANES);
      esperar_gente.signal(); //le aviso al admin que ya deje el tobogan
      lock_tobogan.unlock();
   }

   public void subir_escaleras(Visitante v) throws InterruptedException {
      sem_escalera.acquire();
      escalera_actual++;
      escribir(C.BLANCO, Color.white,v.getID()+" está subiendo las escaleras "+escalera_actual+"/"+CAPACIDAD_ESCALERA);
   }

   public void dejar_escaleras(Visitante v) throws InterruptedException{
      escalera_actual--;
      escribir(C.PURPLE, Color.magenta, v.getID()+" termino de subir las escaleras "+escalera_actual+"/"+CAPACIDAD_ESCALERA);
      sem_escalera.release();
   }

   public void administrar() throws InterruptedException {
      lock_tobogan.lock();
      try {
          while (true) {
              // Esperar hasta que haya gente.
              esperar_gente.await(); 
              
              // Indicar que hay uno disponible.
              esperar_tobogan.signal();
          }
      } catch(InterruptedException e){

      } finally {
          lock_tobogan.unlock(); 
      }
  }



  private void escribir(String color, Color c, String cad) throws InterruptedException{
      mutexConsola.acquire();
      PrintConsola.print(parque.consolas[2], c, cad+"\n");
      mutexConsola.release();

      System.out.println(color+cad+C.RESET);
   }
}
