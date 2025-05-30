/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author magal
 */
@Entity
@Table(name = "inventario")
@NamedQueries({
    @NamedQuery(name = "Inventario.findAll", query = "SELECT i FROM Inventario i"),
    @NamedQuery(name = "Inventario.findByIdInventario", query = "SELECT i FROM Inventario i WHERE i.idInventario = :idInventario"),
    @NamedQuery(name = "Inventario.findByCantidadActual", query = "SELECT i FROM Inventario i WHERE i.cantidadActual = :cantidadActual"),
    @NamedQuery(name = "Inventario.findByCantidadMinima", query = "SELECT i FROM Inventario i WHERE i.cantidadMinima = :cantidadMinima"),
    @NamedQuery(name = "Inventario.findByUbicacion", query = "SELECT i FROM Inventario i WHERE i.ubicacion = :ubicacion"),
    @NamedQuery(name = "Inventario.findByFechaUltimaEntrada", query = "SELECT i FROM Inventario i WHERE i.fechaUltimaEntrada = :fechaUltimaEntrada"),
    @NamedQuery(name = "Inventario.findByFechaUltimaSalida", query = "SELECT i FROM Inventario i WHERE i.fechaUltimaSalida = :fechaUltimaSalida"),
    @NamedQuery(name = "Inventario.findByObservaciones", query = "SELECT i FROM Inventario i WHERE i.observaciones = :observaciones"),
    @NamedQuery(name = "Inventario.findByTalla", query = "SELECT i FROM Inventario i WHERE i.talla = :talla"),
    @NamedQuery(name = "Inventario.findByColor", query = "SELECT i FROM Inventario i WHERE i.color = :color")})
public class Inventario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_inventario")
    private Integer idInventario;
    @Basic(optional = false)
    @Column(name = "cantidad_actual")
    private int cantidadActual;
    @Column(name = "cantidad_minima")
    private Integer cantidadMinima;
    @Column(name = "ubicacion")
    private String ubicacion;
    @Column(name = "fecha_ultima_entrada")
    @Temporal(TemporalType.DATE)
    private Date fechaUltimaEntrada;
    @Column(name = "fecha_ultima_salida")
    @Temporal(TemporalType.DATE)
    private Date fechaUltimaSalida;
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "talla")
    private String talla;
    @Column(name = "color")
    private String color;
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")
    @ManyToOne(optional = false)
    private Producto idProducto;

    public Inventario() {
    }

    public Inventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public Inventario(Integer idInventario, int cantidadActual) {
        this.idInventario = idInventario;
        this.cantidadActual = cantidadActual;
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public int getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(int cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public Integer getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(Integer cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Date getFechaUltimaEntrada() {
        return fechaUltimaEntrada;
    }

    public void setFechaUltimaEntrada(Date fechaUltimaEntrada) {
        this.fechaUltimaEntrada = fechaUltimaEntrada;
    }

    public Date getFechaUltimaSalida() {
        return fechaUltimaSalida;
    }

    public void setFechaUltimaSalida(Date fechaUltimaSalida) {
        this.fechaUltimaSalida = fechaUltimaSalida;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
        hash += (idInventario != null ? idInventario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Inventario)) {
            return false;
        }
        Inventario other = (Inventario) object;
        if ((this.idInventario == null && other.idInventario != null) || (this.idInventario != null && !this.idInventario.equals(other.idInventario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Inventario[ idInventario=" + idInventario + " ]";
    }
    
}
