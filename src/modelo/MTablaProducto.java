/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.w3c.dom.Text;

/**
 *
 * @author ericg
 */
public class MTablaProducto extends AbstractTableModel{
    
    private ArrayList<Producto> datosProductos;
    private String encabezados [] = {"Identificador","Nombre","Descripcion","Talla","Color","Precio"};
    private Class clasesC [] = {Integer.class,String.class,Text.class,String.class,String.class,BigDecimal.class};
    public MTablaProducto(ArrayList mtp){
        datosProductos = mtp;
    }

    private String[] getEncabezados(){
        return encabezados;
    }
    
    @Override
    public int getRowCount() {
        return datosProductos.size();
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

    public Producto getProducto(int r){
        return datosProductos.get(r);
    }
    @Override
    public Object getValueAt(int r, int c) {
        switch(c){
            case 0: return datosProductos.get(r).getIdProducto();
            case 1: return datosProductos.get(r).getNombre();
            case 2: return datosProductos.get(r).getDescripcion();
            case 3: return datosProductos.get(r).getTalla();
            case 4: return datosProductos.get(r).getColor();
            case 5: return datosProductos.get(r).getPrecio();
            default: return null;
        }
    }
}
