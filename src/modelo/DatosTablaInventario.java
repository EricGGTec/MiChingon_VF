/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author ericg
 */
public class DatosTablaInventario {
    private Inventario inventario;
    
    public DatosTablaInventario(Inventario i){
        this.inventario = i;
    }
    public int getInventario(){
        return inventario.getIdInventario();
    }
    public String getProducto(){
        return inventario.getIdProducto().getNombre();
    }
    public String getUbicacion(){
        return inventario.getUbicacion();
    }
    public int getCantidad(){
        return inventario.getCantidadActual();
    }
    public int getCantidadmi(){
        return inventario.getCantidadMinima();
    }
    public String getObservaciones(){
        return inventario.getObservaciones();
    }
    public Inventario getObjInventatio(){
        return inventario;
    }
}
