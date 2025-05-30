/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author ericg
 */
public class DatosTablaInventarioBS {
    private Inventario inventario;
    
    public DatosTablaInventarioBS(Inventario i){
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
}
