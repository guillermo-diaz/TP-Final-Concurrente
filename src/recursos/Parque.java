package recursos;

import hilos.Visitante;

public class Parque {
    public Snorkell snorkell;
    public Restaurante[] restaurantes = new Restaurante[3];
    public Carrera carrera;

    public Parque(){
        snorkell = new Snorkell();
        carrera = new Carrera();
        for (int i = 0; i < restaurantes.length; i++) {
            restaurantes[i] = new Restaurante(i);
        }
    }

    public void ir_snorkell(Visitante v) throws InterruptedException{
        snorkell.solicitar_equipo(v);
    }

    public void salir_snorkell(Visitante v) throws InterruptedException{
        snorkell.dejar_equipo(v);
    }

    public void entrar_restaurante(int nro_restaurante, Visitante v, int opcion_menu) throws InterruptedException{
        Restaurante res_elegido = restaurantes[nro_restaurante];
        if (opcion_menu == 1){ //opcion 1 es almuerzo
            res_elegido.almorzar(v);
        } else { //opcion 2 es merienda
            res_elegido.merendar(v);
        }
    }

    public void salir_restaurante(int nro_restaurante, Visitante v) throws InterruptedException{
        restaurantes[nro_restaurante].salir(v);
    }

    
}
