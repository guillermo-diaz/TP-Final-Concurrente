package hilos;

import recursos.Faro;
import recursos.Parque;
import recursos.Snorkell;

public class AdminFaro extends Thread{
    private final String id;
    private final Faro faro;
    private final Parque parque;

    public AdminFaro(String id, Parque p) {
        this.id = id;
        this.parque = p;
        this.faro = p.faro;
    }

    @Override
    public void run() {
        try {
            faro.administrar();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            
        }
    }


    public String getID(){
        return id;
    }
}
