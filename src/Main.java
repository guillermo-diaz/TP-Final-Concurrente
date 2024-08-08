import java.util.Random;

import config.Config;
import hilos.Asistente;
import hilos.Visitante;
import recursos.Parque;

public class Main {
    public static void main(String[] args) {
        Parque p = new Parque();
        Visitante[] visitantes = new Visitante[Config.CANT_VISITANTES];
        Asistente asistente1 = new Asistente("A1", p);
        Asistente asistente2 = new Asistente("A2", p);
        asistente1.start();
        asistente2.start();

        for (int i = 0; i < visitantes.length; i++) {
            visitantes[i] = new Visitante("#"+i, null, p, (get_random(1) == 1) ? true : false);
            if(visitantes[i].quiere_doble()){
                System.out.println("#"+i+" doble");
            } else{
                System.out.println("#"+i+" simple");
            }
        }

        for (int i = 0; i < visitantes.length; i++) {
            visitantes[i].start();
        }
    }

    public static int get_random(int limite){
        Random r = new Random();
        return r.nextInt(limite+1);
    }



}
