package hilos;

import config.Config;
import recursos.Horario;

public class Reloj extends Thread {
    Horario horario;

    public Reloj(Horario h){
        horario = h;
    }
    @Override
    public void run() {
        try {
            while (true) {
                horario.avanzar_minuto();
                sleep(Config.DELAY_RELOJ);
            }
        } catch (Exception e) {

        }
    }
}
