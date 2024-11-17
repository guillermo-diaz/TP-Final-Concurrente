package recursos;

import java.util.concurrent.Semaphore;

public class Gomon {
        private final int id;
        private final String tipo;
        public boolean ocupado;
        public Carrera carrera; //la carrera donde pertenece
    
        public Gomon(int id, String tipo, Carrera c) {
            this.id = id;
            this.tipo = tipo;
            this.ocupado = false;
            this.carrera = c;
        }

        public synchronized void usar() throws InterruptedException {
            while (ocupado) {
                wait(); // Espera hasta que el gomon esté disponible
            }
            ocupado = true;
        }

        public synchronized void dejar() {
            ocupado = false;
            notifyAll(); // Notifica que el gomon está disponible
            carrera.getGomonesSimples().offer(this); //lo pone en la carrera que corresponde
        }
    
        public int getID() {
            return id;
        }
    
        public String getTipo() {
            return tipo;
        }

        public boolean vacio(){
            return !ocupado;
        }
}    

class GomonDoble extends Gomon {

        private int cant_actual;
        private Semaphore sem_espera;
        private Semaphore lugares = new Semaphore(2);
    
        public GomonDoble(int id, Carrera c) {
            super(id, "Doble", c);
            cant_actual = 0;
            sem_espera = new Semaphore(0);
        }

        @Override
        public void usar() throws InterruptedException {
            lugares.acquire();
            cant_actual++;
            if (cant_actual == 2) {
                ocupado = true;
                sem_espera.release(); //le avisa al compañero que ya estan completos
            } else {
                sem_espera.acquire();
            }
        }

        @Override
        public void dejar()  {
            cant_actual--;
            if (cant_actual == 0){ //el ultimo en salir
                ocupado = false;
                carrera.getGomonesDobles().offer(this); //lo pone en la carrera que corresponde
            } 
            lugares.release();
        }


        @Override
        public boolean vacio(){
            return cant_actual == 0;
        }

        public boolean solo(){
            return cant_actual == 1;
        }

        public int getCantActual(){
            return cant_actual;
        }
    
        
        

}

