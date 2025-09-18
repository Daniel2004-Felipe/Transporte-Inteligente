package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Ruta {
    List<Paradero> paraderos;



    public Ruta(){
        this.paraderos=new ArrayList<>();
    }

    public void agregar(Paradero p){
        paraderos.add(p);

    }
    public List<Paradero> getParaderos() {
        return paraderos;
    }









}

