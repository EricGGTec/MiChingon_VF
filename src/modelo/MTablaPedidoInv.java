/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ericg
 */
public class MTablaPedidoInv extends AbstractTableModel{

    private ArrayList<DatosTablaPedidoInv> datosPedido;
    private String encabezados [] = {"Id. Ped.","Identificador Cli.","Nombre Cli.","Fecha Ped.","Fecha Ent.","Estado"};
    private Class clasesC [] = {Integer.class,Integer.class,String.class,Date.class,Date.class,String.class};
    
    public MTablaPedidoInv(ArrayList mtp){
        datosPedido = mtp;
    }
    
    private String[] getEncabezados(){
        return encabezados;
    }
    
    @Override
    public int getRowCount() {
        return datosPedido.size();
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
    
    public Pedido getPedido(int r){
        return datosPedido.get(r).getPed();
    }
    
    @Override
    public Object getValueAt(int r, int c) {
        switch(c){
            case 0: return datosPedido.get(r).getPedido();
            case 1: return datosPedido.get(r).getCliente();
            case 2: return datosPedido.get(r).getNombre();
            case 3: return datosPedido.get(r).getFecha();
            case 4: return datosPedido.get(r).getFechaEstimada();
            case 5: return datosPedido.get(r).getEstado();
            default: return null;
        }
    }
    
}
