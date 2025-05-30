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
@Table(name = "ubicacion_pedido")
@NamedQueries({
    @NamedQuery(name = "UbicacionPedido.findAll", query = "SELECT u FROM UbicacionPedido u"),
    @NamedQuery(name = "UbicacionPedido.findByIdUbicacion", query = "SELECT u FROM UbicacionPedido u WHERE u.idUbicacion = :idUbicacion"),
    @NamedQuery(name = "UbicacionPedido.findByEstadoLogistica", query = "SELECT u FROM UbicacionPedido u WHERE u.estadoLogistica = :estadoLogistica"),
    @NamedQuery(name = "UbicacionPedido.findByFechaActualizacion", query = "SELECT u FROM UbicacionPedido u WHERE u.fechaActualizacion = :fechaActualizacion")})
public class UbicacionPedido implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_ubicacion")
    private Integer idUbicacion;
    @Column(name = "estado_logistica")
    private String estadoLogistica;
    @Column(name = "fecha_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    @JoinColumn(name = "id_pedido", referencedColumnName = "id_pedido")
    @ManyToOne(optional = false)
    private Pedido idPedido;
    @JoinColumn(name = "id_proveedor", referencedColumnName = "id_proveedor")
    @ManyToOne
    private Proveedor idProveedor;

    public UbicacionPedido() {
    }

    public UbicacionPedido(Integer idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getEstadoLogistica() {
        return estadoLogistica;
    }

    public void setEstadoLogistica(String estadoLogistica) {
        this.estadoLogistica = estadoLogistica;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Pedido getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Pedido idPedido) {
        this.idPedido = idPedido;
    }

    public Proveedor getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Proveedor idProveedor) {
        this.idProveedor = idProveedor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUbicacion != null ? idUbicacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UbicacionPedido)) {
            return false;
        }
        UbicacionPedido other = (UbicacionPedido) object;
        if ((this.idUbicacion == null && other.idUbicacion != null) || (this.idUbicacion != null && !this.idUbicacion.equals(other.idUbicacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.UbicacionPedido[ idUbicacion=" + idUbicacion + " ]";
    }
    
}
