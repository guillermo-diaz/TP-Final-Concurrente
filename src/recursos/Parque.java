package recursos;

import hilos.Visitante;

public class Parque {
    public Snorkell snorkell;
    public Restaurante[] restaurantes = new Restaurante[3];
    public Carrera carrera;
    public Faro faro;

    public Parque(){
        snorkell = new Snorkell();
        carrera = new Carrera();
        faro = new Faro();
        for (int i = 0; i < restaurantes.length; i++) {
            restaurantes[i] = new Restaurante(i);
        }
    }

    public Snorkell getSnorkell(){
        return snorkell;
    }

    public Restaurante[] getRestaurantes(){
        return restaurantes;
    }

    public Carrera getCarrera(){
        return carrera;
    }

    public Faro getFaro(){
        return faro;
    }

    
}
