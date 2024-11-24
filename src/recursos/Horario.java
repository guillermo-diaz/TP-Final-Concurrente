package recursos;

import javax.swing.JLabel;

import config.Config;

public class Horario {
    private int hora;
    private int minuto;
    private int CIERRE;
    private int APERTURA;
    JLabel horaUI;
    Parque parque;

    public Horario(JLabel horaGUI, Parque p) {
        this.hora = Config.HORA_APERTURA; 
        this.CIERRE = Config.HORA_CIERRE;
        this.minuto = 0;
        this.horaUI = horaGUI; //de la interfaz
        this.parque = p;  
    }

    public void avanzar_minuto() {
        minuto++;
        if (minuto == 60) {
            minuto = 0;
            hora = (hora + 1) % 24;
            if (hora == CIERRE) {
                parque.cerrar();
            }
            if (hora == APERTURA) {
                parque.cerrar();
            }            
        }
        horaUI.setText(getHora());
    }

    private String getHora() {
        return String.format("%02d:%02d", hora, minuto);
    }
    

}
