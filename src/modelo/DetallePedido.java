/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author magal
 */
@Entity
@Table(name = "detalle_pedido")
@NamedQueries({
    @NamedQuery(name = "DetallePedido.findAll", query = "SELECT d FROM DetallePedido d"),
    @NamedQuery(name = "DetallePedido.findByCantidad", query = "SELECT d FROM DetallePedido d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "DetallePedido.findByIdpedidoProducto", query = "SELECT d FROM DetallePedido d WHERE d.idpedidoProducto = :idpedidoProducto"),
    @NamedQuery(name = "DetallePedido.findByTalla", query = "SELECT d FROM DetallePedido d WHERE d.talla = :talla"),
    @NamedQuery(name = "DetallePedido.findByColor", query = "SELECT d FROM DetallePedido d WHERE d.color = :color")})
public class DetallePedido implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "cantidad")
    private BigInteger cantidad;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idpedido_producto")
    private Integer idpedidoProducto;
    @Column(name = "talla")
    private Integer talla;
    @Column(name = "color")
    private String color;
    @JoinColumn(name = "id_pedido", referencedColumnName = "id_pedido")
    @ManyToOne(optional = false)
    private Pedido idPedido;
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")
    @ManyToOne
    private Producto idProducto;

    public DetallePedido() {
    }

    public DetallePedido(Integer idpedidoProducto) {
        this.idpedidoProducto = idpedidoProducto;
    }

    public BigInteger getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigInteger cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getIdpedidoProducto() {
        return idpedidoProducto;
    }

    public void setIdpedidoProducto(Integer idpedidoProducto) {
        this.idpedidoProducto = idpedidoProducto;
    }

    public Integer getTalla() {
        return talla;
    }

    public void setTalla(Integer talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Pedido getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Pedido idPedido) {
        this.idPedido = idPedido;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idpedidoProducto != null ? idpedidoProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetallePedido)) {
            return false;
        }
        DetallePedido other = (DetallePedido) object;
        if ((this.idpedidoProducto == null && other.idpedidoProducto != null) || (this.idpedidoProducto != null && !this.idpedidoProducto.equals(other.idpedidoProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.DetallePedido[ idpedidoProducto=" + idpedidoProducto + " ]";
    }
    
}
