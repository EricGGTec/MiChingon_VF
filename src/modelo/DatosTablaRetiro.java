/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;

/**
 *
 * @author Hp EliteBook
 */
public class DatosTablaRetiro {
    private String nombreCajero; // Nombre producto
    private int monto;
    private LocalDate fecha;

    // Constructor
    public DatosTablaRetiro(String nombre, int monto, LocalDate fecha) {
        this.nombreCajero = nombre;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getNombreCajero() {
        return nombreCajero;
    }

    public void setNombreCajero(String nombreCajero) {
        this.nombreCajero = nombreCajero;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    } 
   
}