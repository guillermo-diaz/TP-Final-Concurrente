package hilos;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

import recursos.*;

public class Visitante extends Thread{
    private final String id;
    private final String tipo_acceso; // "Particular" o "Tour"s
    private boolean doble;
    private Parque parque;
    public boolean almuerzo;
    public boolean merienda;

    public Visitante(String id, String tipo_acceso, Parque parque, boolean doble_gomon) {
        this.parque = parque;
        this.id = id;
        this.tipo_acceso = tipo_acceso;
        this.doble = doble_gomon;
        almuerzo = false;
        merienda = false;
    }

    @Override
    public void run() {
        ir_carrera();
        while (true) {
        }
        // while (true) { //mientras este abierto (me falta ver eso)
        //     Random r = new Random();
        //     switch (r.nextInt(6)) { //actividad aleatoria
        //         case 0:
        //             ir_tienda();
        //             break;
        //         case 1:
        //             ir_faro();
        //             break;
        //         case 2:
        //             ir_restaurante();
        //             break;
        //         case 4:
        //             ir_snorkell();
        //             break;
        //         default:
        //             ir_carrera();
        //             break;
        //     }
            
        // }
        
    }

    private void ir_restaurante(){
        try {
            Random r = new Random();
            int nro_res = r.nextInt(3); //restaurante random
            if (!almuerzo){ //puede ir solo si puede realizar alguna de estas dos cosas 
                parque.entrar_restaurante(nro_res, this, 1);
                dormir(3000);
                parque.salir_restaurante(nro_res, this);
                almuerzo = true;
            } else if(!merienda){
                parque.entrar_restaurante(nro_res, this, 2);
                dormir(3000);
                parque.salir_restaurante(nro_res, this);
                merienda = true;
            }
            //nadie merienda y almuerza a la vez, ni tampoco lo hace 2 veces

        } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                
        }
    }

    private void ir_tienda(){
        Random r = new Random();
  


    }

    private void ir_carrera(){
        Carrera car = parque.carrera;
    
        try {
            Gomon g = car.usar_gomon(this, doble);
            dormir(8000);
            car.dejar_gomon(this, g);
        } catch (InterruptedException | BrokenBarrierException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
    }

    private void ir_snorkell() {
        try {
            parque.ir_snorkell(this);
            dormir(10000);
            parque.salir_snorkell(this);   
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void ir_faro(){

    }

     public void dormir(int bound){
        
        Random r = new Random();
        try {
            sleep(r.nextInt(bound));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public String getID(){
        return id;
    }

    public int get_random(int limite){
        Random r = new Random();
        return r.nextInt(limite+1);
    }

    public int get_random(int inicio, int limite){
        Random r = new Random();
        return r.nextInt(limite-inicio)+inicio;
    }


  
    public boolean equals(Visitante v) {
        return this.id == v.getID();
    }

    public boolean quiere_doble(){
        return doble;
    }
    

}
