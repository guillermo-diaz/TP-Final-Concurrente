package recursos;

import java.util.concurrent.Semaphore;

import hilos.Visitante;
import util.C;

public class Tienda {
    private int cajas_disponibles = 2;
    private Semaphore cajas = new Semaphore(cajas_disponibles, true);

    public void pagar(Visitante v) throws InterruptedException  {
        if (cajas.availablePermits() == 0){
            escribir(C.AMARILLO, v.getID()+" espera caja");
        }
        cajas.acquire(1);
        escribir(C.VERDE, v.getID()+" entro a caja");
        cajas_disponibles--;
    }

    public void salir_shop(Visitante v) {
        escribir(C.ROJO, v.getID()+" salio de la caja");
        cajas_disponibles++;
        cajas.release(1); 
    }

    public int getCantCajasDisponibles() {
        return cajas.availablePermits();
    }

     private void escribir(String color, String cad){
        System.out.println(color+cad+C.RESET);
    }

}
