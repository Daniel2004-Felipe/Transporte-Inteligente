package Modelo;

public class Rutas {
    private int usuarioId;
    private String nombreRuta;
    private String destino;
    private String horaLlegada;
    private double distancia;
    private double tiempo;
    private String paraderoOrigen;
    private String paraderoDestino;

    public Rutas(int usuarioId, String nombreRuta, String destino, String horaLlegada,
                double distancia, double tiempo, String paraderoOrigen, String paraderoDestino) {
        this.usuarioId = usuarioId;
        this.nombreRuta = nombreRuta;
        this.destino = destino;
        this.horaLlegada = horaLlegada;
        this.distancia = distancia;
        this.tiempo = tiempo;
        this.paraderoOrigen = paraderoOrigen;
        this.paraderoDestino = paraderoDestino;
    }

    // Getters simples
    public int getUsuarioId() { return usuarioId; }
    public String getNombreRuta() { return nombreRuta; }
    public String getDestino() { return destino; }
    public String getHoraLlegada() { return horaLlegada; }
    public double getDistancia() { return distancia; }
    public double getTiempo() { return tiempo; }
    public String getParaderoOrigen() { return paraderoOrigen; }
    public String getParaderoDestino() { return paraderoDestino; }
}
