package hilos;

import java.util.Random;

import recursos.Carrera;
import recursos.Parque;

public class Tren extends Thread{
    private Carrera carrera;

    public Tren(Parque p){
        carrera = p.getCarrera();
    }

    @Override
    public void run() {
        while (true){
            try {
                carrera.esperar_gente();
                sleep(5000); //tiempo de ida
                carrera.bajar_gente();
                sleep(5000);
                carrera.avisar_gente();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }


    

}
