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
@Table(name = "reporte")
@NamedQueries({
    @NamedQuery(name = "Reporte.findAll", query = "SELECT r FROM Reporte r"),
    @NamedQuery(name = "Reporte.findByIdReporte", query = "SELECT r FROM Reporte r WHERE r.idReporte = :idReporte"),
    @NamedQuery(name = "Reporte.findByTipoReporte", query = "SELECT r FROM Reporte r WHERE r.tipoReporte = :tipoReporte"),
    @NamedQuery(name = "Reporte.findByFechaGenerado", query = "SELECT r FROM Reporte r WHERE r.fechaGenerado = :fechaGenerado"),
    @NamedQuery(name = "Reporte.findByDescripcion", query = "SELECT r FROM Reporte r WHERE r.descripcion = :descripcion"),
    @NamedQuery(name = "Reporte.findByParametros", query = "SELECT r FROM Reporte r WHERE r.parametros = :parametros")})
public class Reporte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_reporte")
    private Integer idReporte;
    @Column(name = "tipo_reporte")
    private String tipoReporte;
    @Column(name = "fecha_generado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGenerado;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "parametros")
    private String parametros;
    @JoinColumn(name = "generado_por", referencedColumnName = "id_trabajador")
    @ManyToOne
    private Trabajador generadoPor;

    public Reporte() {
    }

    public Reporte(Integer idReporte) {
        this.idReporte = idReporte;
    }

    public Integer getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Integer idReporte) {
        this.idReporte = idReporte;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Date getFechaGenerado() {
        return fechaGenerado;
    }

    public void setFechaGenerado(Date fechaGenerado) {
        this.fechaGenerado = fechaGenerado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getParametros() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public Trabajador getGeneradoPor() {
        return generadoPor;
    }

    public void setGeneradoPor(Trabajador generadoPor) {
        this.generadoPor = generadoPor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idReporte != null ? idReporte.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reporte)) {
            return false;
        }
        Reporte other = (Reporte) object;
        if ((this.idReporte == null && other.idReporte != null) || (this.idReporte != null && !this.idReporte.equals(other.idReporte))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Reporte[ idReporte=" + idReporte + " ]";
    }
    
}
