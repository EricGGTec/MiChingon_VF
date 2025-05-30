/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author magal
 */
@Entity
@Table(name = "trabajador")
@NamedQueries({
    @NamedQuery(name = "Trabajador.findAll", query = "SELECT t FROM Trabajador t"),
    @NamedQuery(name = "Trabajador.findByIdTrabajador", query = "SELECT t FROM Trabajador t WHERE t.idTrabajador = :idTrabajador"),
    @NamedQuery(name = "Trabajador.findByNombre", query = "SELECT t FROM Trabajador t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Trabajador.findByApellidoMaterno", query = "SELECT t FROM Trabajador t WHERE t.apellidoMaterno = :apellidoMaterno"),
    @NamedQuery(name = "Trabajador.findByApellidoPaterno", query = "SELECT t FROM Trabajador t WHERE t.apellidoPaterno = :apellidoPaterno"),
    @NamedQuery(name = "Trabajador.findByHoraEntrada", query = "SELECT t FROM Trabajador t WHERE t.horaEntrada = :horaEntrada"),
    @NamedQuery(name = "Trabajador.findByHoraSalida", query = "SELECT t FROM Trabajador t WHERE t.horaSalida = :horaSalida"),
    @NamedQuery(name = "Trabajador.findByFechaNacimiento", query = "SELECT t FROM Trabajador t WHERE t.fechaNacimiento = :fechaNacimiento"),
    @NamedQuery(name = "Trabajador.findByCorreo", query = "SELECT t FROM Trabajador t WHERE t.correo = :correo"),
    @NamedQuery(name = "Trabajador.findByNumeroTelefonico", query = "SELECT t FROM Trabajador t WHERE t.numeroTelefonico = :numeroTelefonico"),
    @NamedQuery(name = "Trabajador.findByRol", query = "SELECT t FROM Trabajador t WHERE t.rol = :rol"),
    @NamedQuery(name = "Trabajador.findByUsuario", query = "SELECT t FROM Trabajador t WHERE t.usuario = :usuario"),
    @NamedQuery(name = "Trabajador.findByPassword", query = "SELECT t FROM Trabajador t WHERE t.password = :password")})
public class Trabajador implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_trabajador")
    private Long idTrabajador;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "apellido_materno")
    private String apellidoMaterno;
    @Basic(optional = false)
    @Column(name = "apellido_paterno")
    private String apellidoPaterno;
    @Basic(optional = false)
    @Column(name = "hora_entrada")
    @Temporal(TemporalType.TIME)
    private Date horaEntrada;
    @Basic(optional = false)
    @Column(name = "hora_salida")
    @Temporal(TemporalType.TIME)
    private Date horaSalida;
    @Basic(optional = false)
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Basic(optional = false)
    @Column(name = "correo")
    private String correo;
    @Basic(optional = false)
    @Column(name = "numero_telefonico")
    private String numeroTelefonico;
    @Basic(optional = false)
    @Column(name = "rol")
    private String rol;
    @Basic(optional = false)
    @Column(name = "usuario")
    private String usuario;
    @Basic(optional = false)
    @Column(name = "password")
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCajero")
    private Collection<Ticket> ticketCollection;
    @OneToMany(mappedBy = "generadoPor")
    private Collection<Reporte> reporteCollection;

    public Trabajador() {
    }

    public Trabajador(Long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public Trabajador(Long idTrabajador, String nombre, String apellidoMaterno, String apellidoPaterno, Date horaEntrada, Date horaSalida, Date fechaNacimiento, String correo, String numeroTelefonico, String rol, String usuario, String password) {
        this.idTrabajador = idTrabajador;
        this.nombre = nombre;
        this.apellidoMaterno = apellidoMaterno;
        this.apellidoPaterno = apellidoPaterno;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.numeroTelefonico = numeroTelefonico;
        this.rol = rol;
        this.usuario = usuario;
        this.password = password;
    }

    public Long getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(Long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public Date getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Date horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Date getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(Date horaSalida) {
        this.horaSalida = horaSalida;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNumeroTelefonico() {
        return numeroTelefonico;
    }

    public void setNumeroTelefonico(String numeroTelefonico) {
        this.numeroTelefonico = numeroTelefonico;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Ticket> getTicketCollection() {
        return ticketCollection;
    }

    public void setTicketCollection(Collection<Ticket> ticketCollection) {
        this.ticketCollection = ticketCollection;
    }

    public Collection<Reporte> getReporteCollection() {
        return reporteCollection;
    }

    public void setReporteCollection(Collection<Reporte> reporteCollection) {
        this.reporteCollection = reporteCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTrabajador != null ? idTrabajador.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Trabajador)) {
            return false;
        }
        Trabajador other = (Trabajador) object;
        if ((this.idTrabajador == null && other.idTrabajador != null) || (this.idTrabajador != null && !this.idTrabajador.equals(other.idTrabajador))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Trabajador[ idTrabajador=" + idTrabajador + " ]";
    }
    
}
