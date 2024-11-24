package recursos;

import config.Config;
import hilos.Visitante;

public class Parque {
    public Snorkell snorkell;
    public Restaurante[] restaurantes = new Restaurante[3];
    public Carrera carrera;
    public Faro faro;
    public Tienda shop;
    public boolean abierto;
    public int cant_actual;
    public int limite;


    public Parque(){
        snorkell = new Snorkell();
        carrera = new Carrera();
        faro = new Faro();
        shop = new Tienda();
        limite = Config.CANT_MOLINETES;
        cant_actual = 0;
        for (int i = 0; i < restaurantes.length; i++) {
            restaurantes[i] = new Restaurante(i);
        }
       
    }

    public synchronized void entrar() throws InterruptedException{
        while (!abierto && cant_actual >= limite) {
            wait();
        }
        cant_actual++;
    }

    public synchronized void salir(){
        cant_actual--;
        notifyAll();
    }

    public synchronized boolean esperar_apertura() throws InterruptedException{
        while (!abierto){
            wait();
        }
        return true;
    }
    
    

    public synchronized void abrir(){
        abierto = true;
        notifyAll();
    }

    public synchronized void cerrar(){
        abierto = false;
    }

    public Snorkell getSnorkell(){
        return snorkell;
    }

    public Restaurante[] getRestaurantes(){
        return restaurantes;
    }

    public Carrera getCarrera(){
        return carrera;
    }

    public Faro getFaro(){
        return faro;
    }

    public Tienda getTienda(){
        return shop;
    }

    
}
