package recursos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextPane;

import GUI.GUI;
import GUI.PrintConsola;
import config.Config;
import hilos.Visitante;
import util.C;
import java.awt.Color;

public class Parque {
    public Snorkell snorkell;
    public Restaurante[] restaurantes = new Restaurante[3];
    public Carrera carrera;
    public Faro faro;
    public Tienda shop;
    public boolean abierto;
    public int cant_actual;
    public int limite;
    
    
 
    private int espacioCol;

    //tour
    private CyclicBarrier colectivoBarrier;

    //gui
    public GUI gui;
    public JTextPane[] consolas = new JTextPane[7];
    private Semaphore mutexConsola= new Semaphore(1);

    public Parque(){
        snorkell = new Snorkell(this);
        carrera = new Carrera(this);
        faro = new Faro(this);
        shop = new Tienda(this);
        limite = Config.CANT_MOLINETES;
        espacioCol = Config.CAPACIDAD_COLECTIVO;

        colectivoBarrier = new CyclicBarrier(espacioCol, () -> {
            try {
                Thread.sleep(3000); //tiempo que tarda en llevarlos el cole
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restaurar el estado de interrupción del hilo
            }
        });
        

        cant_actual = 0;
        for (int i = 0; i < restaurantes.length; i++) {
            restaurantes[i] = new Restaurante(i, this);
        }
    }

    public void acceder_tour(Visitante v) throws InterruptedException, BrokenBarrierException{
        escribir(C.BLANCO, Color.WHITE, v.getID()+" espera cole para acceder: ");
        colectivoBarrier.await();
        escribir(C.PURPLE, Color.magenta, v.getID()+" accedio por Tour al parque");
    }

    public synchronized void entrar(Visitante v) throws InterruptedException{
        while (!abierto || cant_actual >= limite) {
            escribir(C.AMARILLO, Color.yellow, v.getID()+" espera en la entrada");
            wait();
        }
        escribir(C.VERDE, Color.green, v.getID()+" entró al parque");
        cant_actual++;
    }

    public synchronized void salir(Visitante v) throws InterruptedException{
        cant_actual--;
        escribir(C.ROJO, Color.red,  v.getID()+" salió del parque");
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

    public void GUI(GUI gui){
        this.gui = gui;
        consolas = gui.consolas;
    }
    
    private void escribir(String color, Color c, String cad) throws InterruptedException{
        mutexConsola.acquire();
        PrintConsola.print(consolas[6], c, cad+"\n");
        mutexConsola.release();

        System.out.println(color+cad+C.RESET);
    }
}
