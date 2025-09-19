package Modelo;

<<<<<<< HEAD

// Clase Paraderos
public class Paradero {

    private int posicion;
    private String nombre;
    private TipoParadero tipo;
    private double lon;
    private double lat;

    // Constructor vacío
    public Paradero() {
    }

    // Constructor con parámetros
    public Paradero(int posicion, String nombre, TipoParadero tipo, double lon, double lat) {
        this.posicion = posicion;
        this.nombre = nombre;
        this.tipo = tipo;
        this.lon = lon;
        this.lat = lat;
    }

    // Getters y setters
    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoParadero getTipo() {
        return tipo;
    }

    public void setTipo(TipoParadero tipo) {
        this.tipo = tipo;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "Paradero{" +
                "posicion=" + posicion +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
=======
public class Paradero {
>>>>>>> c3628db39dc62b472976c8f5187b5eb85aa3341d
}
