package hilos;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

import config.Config;
import recursos.*;
import util.C;


public class Visitante extends Thread{
    private final String id;
    public String tipo_acceso; // "Particular" o "Tour"
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
        try {
            if (tipo_acceso.toLowerCase().equals("tour")){
                parque.acceder_tour(this);
            }
            parque.entrar(this); //intenta entrar

            if (Config.PRUEBA_INDIVIDUAL){
                actividad_individual();
            } else {
                actividades(); //ya entro, intento acceder a las actividades
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public void actividad_individual(){
        String act = Config.ACTIVIDAD_AISLADA.toLowerCase();

        try {
            switch (act) {
                case "restaurante":
                    while (parque.esperar_apertura()) {
                        ir_restaurante();
                    }
                    break;
            
                case "snorkell":
                    while (parque.esperar_apertura()) {
                        ir_snorkell();
                    }
                    break;
            
                case "faro":
                    while (parque.esperar_apertura()) {
                        ir_faro();
                    }
                    break;
            
                case "tienda":
                    while (parque.esperar_apertura()) {
                        ir_tienda();
                    }
                    break;    
                case "carrera":
                    while (parque.esperar_apertura()) {
                        ir_carrera();
                    }
                    break;
            
                default:
                    System.out.println("Actividad no reconocida: " + act);
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
       
        
    }

    public void actividades(){
        
        try {
            while (parque.esperar_apertura()) { //mientras este abierto 
                Random r = new Random();
                switch (r.nextInt(6)) { //actividad aleatoria
                    case 0:
                        ir_tienda();
                        break;
                    case 1:
                        ir_faro();
                        break;
                    case 2:
                        ir_restaurante();
                        break;
                    case 4:
                        ir_snorkell();
                        break;
                    default:
                        ir_carrera();
                        break;
                }
                
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void ir_restaurante(){
        try {

            Random r = new Random();
            int nro_res = r.nextInt(3); //restaurante random
            Restaurante res_elegido = parque.getRestaurantes()[nro_res];

            if (!almuerzo){ //puede ir solo si puede realizar alguna de estas dos cosas 
                res_elegido.almorzar(this);;
                dormir(3000);
                res_elegido.salir(this);
                almuerzo = true;
            } else if(!merienda){
                res_elegido.merendar(this);
                dormir(3000);
                res_elegido.salir(this);
                merienda = true;
            }
            //nadie merienda y almuerza a la vez, ni tampoco lo hace 2 veces

        } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                
        }
    }

    private void ir_tienda(){
        try {
            Tienda tienda = parque.getTienda();
            tienda.entrar(this);
            sleep(get_random(3000, 10000)); //simula tiempo que pasa en la tienda eligiendo el souvenir
            // System.out.println(C.PURPLE+id+" encontró un souvenir en la tienda"+C.RESET);
            tienda.pagar(this);
            dormir(get_random(5000)); //paga
            tienda.salir_shop(this);
        } catch (Exception e) {
            // TODO: handle exception
        }
  


    }

    private void ir_carrera(){
        Carrera car = parque.carrera;
    
        try {
            if (Math.random() < 0.5){
                car.acceder_tren(this);
            } else {
                car.acceder_bici(this);
                dormir(5000);
                car.dejar_bici(this);
            }
            Gomon g = car.usar_gomon(this, doble);
            g.subir(this);
            g.iniciar_carrera(this);
        } catch (InterruptedException | BrokenBarrierException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
    }

    private void ir_snorkell() {
        try {
            Snorkell snorkell = parque.getSnorkell();
            snorkell.solicitar_equipo(this);
            dormir(10000);
            snorkell.dejar_equipo(this);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void ir_faro(){
        Faro faro = parque.getFaro();

        try {
            faro.subir_escaleras(this);
            dormir(3000);
            faro.dejar_escaleras(this);
            faro.subir_tobogan(this);
            dormir(3500);
            faro.dejar_tobogan(this);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
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

    private int get_random(int limite){
        Random r = new Random();
        return r.nextInt(limite+1);
    }

    private int get_random(int inicio, int limite){
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
