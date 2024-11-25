package config;

public class Config {
   

    public static int HORA_APERTURA = 9;
    public static int HORA_CIERRE = 16;
    public static int DELAY_RELOJ = 70; // en ms

    public static int CANT_VISITANTES = 30;
    public static int CANT_MOLINETES = 25; //k molinetes
    public static int CAPACIDAD_COLECTIVO = 15;


    //snorkel
    public static int EQUIPOS_SNORKEL = 5;

    //restaurante
    public static int CAPACIDAD_RESTAURANTE = 3;

    //carrera gomones
    public static int CAPACIDAD_TREN = 15;
    public static int NUMERO_BICIS = 6;
    public static int NUMERO_GOMONES = 5; //para la salida
    public static int CANT_GOMON_DOBLES = 2;
    public static int CANT_GOMON_SIMPLES = 4;

    // faro mirador
    public static int CAPACIDAD_ESCALERA_FARO = 5;


    

    /*para probar una actividad en particular, 
    El visitante solo realizará dicha actividad solamente en vez de ser random
    */
    public static boolean PRUEBA_INDIVIDUAL = false; 
    //poner alguna actividad que quiera probar, poner el nombre completo. Ejemplo: restaurante/carrera/faro/tienda/snorkell
    public static String ACTIVIDAD_AISLADA = "Ninguna";  

}
