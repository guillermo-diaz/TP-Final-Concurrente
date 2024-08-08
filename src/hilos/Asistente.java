package hilos;

import recursos.Parque;
import recursos.Snorkell;

public class Asistente extends Thread{
    private final String id;
    private final Snorkell puesto_snorkel;
    private final Parque parque;

    public Asistente(String id, Parque p) {
        this.id = id;
        this.parque = p;
        this.puesto_snorkel = p.snorkell;
    }

    @Override
    public void run() {
        while(true){
            try {
                puesto_snorkel.verificar_equipos();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public String getID(){
        return id;
    }
}
