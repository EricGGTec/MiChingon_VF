/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "catalogo")
@NamedQueries({
    @NamedQuery(name = "Catalogo.findAll", query = "SELECT c FROM Catalogo c"),
    @NamedQuery(name = "Catalogo.findByIdCatalogo", query = "SELECT c FROM Catalogo c WHERE c.idCatalogo = :idCatalogo"),
    @NamedQuery(name = "Catalogo.findByNombre", query = "SELECT c FROM Catalogo c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Catalogo.findByDescripcion", query = "SELECT c FROM Catalogo c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "Catalogo.findByFechaInicio", query = "SELECT c FROM Catalogo c WHERE c.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "Catalogo.findByFechaFin", query = "SELECT c FROM Catalogo c WHERE c.fechaFin = :fechaFin"),
    @NamedQuery(name = "Catalogo.findByEstado", query = "SELECT c FROM Catalogo c WHERE c.estado = :estado")})
public class Catalogo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_catalogo")
    private Integer idCatalogo;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @Column(name = "estado")
    private Boolean estado;
    @JoinTable(name = "catalogo_producto", joinColumns = {
        @JoinColumn(name = "id_catalogo", referencedColumnName = "id_catalogo")}, inverseJoinColumns = {
        @JoinColumn(name = "id_producto", referencedColumnName = "id_producto")})
    @ManyToMany
    private Collection<Producto> productoCollection;

    public Catalogo() {
    }

    public Catalogo(Integer idCatalogo) {
        this.idCatalogo = idCatalogo;
    }

    public Catalogo(Integer idCatalogo, String nombre, Date fechaInicio) {
        this.idCatalogo = idCatalogo;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
    }

    public Integer getIdCatalogo() {
        return idCatalogo;
    }

    public void setIdCatalogo(Integer idCatalogo) {
        this.idCatalogo = idCatalogo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Collection<Producto> getProductoCollection() {
        return productoCollection;
    }

    public void setProductoCollection(Collection<Producto> productoCollection) {
        this.productoCollection = productoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCatalogo != null ? idCatalogo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Catalogo)) {
            return false;
        }
        Catalogo other = (Catalogo) object;
        if ((this.idCatalogo == null && other.idCatalogo != null) || (this.idCatalogo != null && !this.idCatalogo.equals(other.idCatalogo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Catalogo[ idCatalogo=" + idCatalogo + " ]";
    }
    
}
