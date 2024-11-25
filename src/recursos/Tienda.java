package recursos;

import java.awt.Color;
import java.util.concurrent.Semaphore;

import GUI.PrintConsola;
import hilos.Visitante;
import util.C;

public class Tienda {
    private Parque parque;
    private int cajas_usadas = 0;
    private Semaphore cajas = new Semaphore(2, true);
    private Semaphore mutexConsola= new Semaphore(1);

    public Tienda(Parque p){
        parque = p;
    }

    public void entrar(Visitante v) throws InterruptedException{
        escribir(C.BLANCO, Color.white, v.getID()+" entro a la tienda ");
    }

    public void pagar(Visitante v) throws InterruptedException  {
        if (cajas.availablePermits() == 0){
            escribir(C.AMARILLO, Color.YELLOW, v.getID()+" espera caja");
        }
        cajas.acquire(1);
        cajas_usadas++;
        escribir(C.VERDE, Color.GREEN, v.getID()+" entro a caja: "+cajas_usadas+"/2");
    }

    public void salir_shop(Visitante v) throws InterruptedException {
        cajas_usadas--;
        escribir(C.ROJO, Color.RED, v.getID()+" salio de la caja: "+cajas_usadas+"/2");
        cajas.release(1); 
    }

    public int getCantCajasDisponibles() {
        return cajas.availablePermits();
    }

    private void escribir(String color, Color c, String cad) throws InterruptedException{
        mutexConsola.acquire();
        PrintConsola.print(parque.consolas[3], c, cad+"\n");
        mutexConsola.release();

        System.out.println(color+cad+C.RESET);
    }
}
