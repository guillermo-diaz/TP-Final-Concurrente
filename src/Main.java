import java.util.Random;

import javax.swing.JLabel;

import GUI.GUI;
import config.Config;
import hilos.AdminFaro;
import hilos.Asistente;
import hilos.Reloj;
import hilos.Tren;
import hilos.Visitante;
import recursos.Horario;
import recursos.Parque;

public class Main { static Thread[] hilos;
    public static void main(String[] args) {
        Parque p = new Parque();
        Visitante[] visitantes = new Visitante[Config.CANT_VISITANTES];
        Asistente asistente1 = new Asistente("A1", p);
        Asistente asistente2 = new Asistente("A2", p);
        AdminFaro admin = new AdminFaro("ADMIN", p);
        Tren tren = new Tren(p);
  
        hilos = new Thread[Config.CANT_VISITANTES+5]; //visitantes + 5 hilos

        hilos[1] = asistente1;
        hilos[2] = asistente2;
        hilos[3] = admin;
        hilos[4] = tren;
        int j = 5;

        for (int i = 0; i < visitantes.length; i++) {
            visitantes[i] = new Visitante("#"+i, "Particular", p, (get_random(1) == 1) ? true : false);
            hilos[j] = visitantes[i];
            j++;

            // pa la carrera test
            // if(visitantes[i].quiere_doble()){
            //     System.out.println("#"+i+" doble");
            // } else{
            //     System.out.println("#"+i+" simple");
            // }
        }

        GUI gui = new GUI(hilos);
        p.GUI(gui);
        gui.frame.setVisible(true);
         
        Horario horario = new Horario(gui.horaLabel, p);
        Reloj reloj = new Reloj(horario);
        hilos[0] = reloj;

        reloj.start();
        asistente1.start();
        asistente2.start();
        admin.start();
        tren.start();
        for (int i = 0; i < visitantes.length; i++) {
            visitantes[i].start();

        }

        
    }

    public static int get_random(int limite){
        Random r = new Random();
        return r.nextInt(limite+1);
    }



}
