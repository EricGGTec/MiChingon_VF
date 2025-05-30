/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "vale_devolucion")
@NamedQueries({
    @NamedQuery(name = "ValeDevolucion.findAll", query = "SELECT v FROM ValeDevolucion v"),
    @NamedQuery(name = "ValeDevolucion.findByIdVale", query = "SELECT v FROM ValeDevolucion v WHERE v.idVale = :idVale"),
    @NamedQuery(name = "ValeDevolucion.findByMonto", query = "SELECT v FROM ValeDevolucion v WHERE v.monto = :monto"),
    @NamedQuery(name = "ValeDevolucion.findByEstado", query = "SELECT v FROM ValeDevolucion v WHERE v.estado = :estado"),
    @NamedQuery(name = "ValeDevolucion.findByFechaEmision", query = "SELECT v FROM ValeDevolucion v WHERE v.fechaEmision = :fechaEmision")})
public class ValeDevolucion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_vale")
    private Integer idVale;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "monto")
    private BigDecimal monto;
    @Column(name = "estado")
    private String estado;
    @Column(name = "fecha_emision")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEmision;
    @JoinColumn(name = "id_devolucion", referencedColumnName = "id_devolucion")
    @ManyToOne(optional = false)
    private Devolucion idDevolucion;

    public ValeDevolucion() {
    }

    public ValeDevolucion(Integer idVale) {
        this.idVale = idVale;
    }

    public ValeDevolucion(Integer idVale, BigDecimal monto) {
        this.idVale = idVale;
        this.monto = monto;
    }

    public Integer getIdVale() {
        return idVale;
    }

    public void setIdVale(Integer idVale) {
        this.idVale = idVale;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Devolucion getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(Devolucion idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idVale != null ? idVale.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ValeDevolucion)) {
            return false;
        }
        ValeDevolucion other = (ValeDevolucion) object;
        if ((this.idVale == null && other.idVale != null) || (this.idVale != null && !this.idVale.equals(other.idVale))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.ValeDevolucion[ idVale=" + idVale + " ]";
    }
    
}
