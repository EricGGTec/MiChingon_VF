/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Date;

/**
 *
 * @author ericg
 */
public class DatosTablaPedidoInv {
    private Pedido pedido;
    
    public DatosTablaPedidoInv(Pedido p){
        this.pedido = p;
    }
    public int getPedido(){
        return pedido.getIdPedido();
    }
    public int getCliente(){
        return pedido.getIdCliente().getIdCliente();
    }
    public String getNombre(){
        return pedido.getIdCliente().getNombre();
    }
    public Date getFecha(){
        return pedido.getFechaPedido();
    }
    public Date getFechaEstimada(){
        return pedido.getFechaEntregaEstimada();
    }
    public String getEstado(){
        return pedido.getEstado();
    }
    public Pedido getPed(){
        return pedido;
    }
}
