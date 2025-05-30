/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ericg
 */
public class MTablaInventario extends AbstractTableModel{

    private ArrayList<DatosTablaInventario> datosInventario;
    private String encabezados [] = {"Id. Inv.","Producto","Ubicacion","Cant.","Cant. Min.","Observ."};
    private Class clasesC [] = {Integer.class,String.class,String.class,Integer.class,Integer.class,String.class};
    
    public MTablaInventario(ArrayList mtp){
        datosInventario = mtp;
    }
    
    private String[] getEncabezados(){
        return encabezados;
    }
    
    @Override
    public int getRowCount() {
        return datosInventario.size();
    }

    @Override
    public int getColumnCount() {
        return encabezados.length;
    }
    
    public String getColumnName(int c){
        return encabezados[c];
    }

    public Class getColumnClass(int c){
        return clasesC[c];
    }
    public Inventario getInventario(int r){
        return datosInventario.get(r).getObjInventatio();
    }
    
    @Override
    public Object getValueAt(int r, int c) {
        switch(c){
            case 0: return datosInventario.get(r).getInventario();
            case 1: return datosInventario.get(r).getProducto();
            case 2: return datosInventario.get(r).getUbicacion();
            case 3: return datosInventario.get(r).getCantidad();
            case 4: return datosInventario.get(r).getCantidadmi();
            case 5: return datosInventario.get(r).getObservaciones();
            default: return null;
        }
    }
}
