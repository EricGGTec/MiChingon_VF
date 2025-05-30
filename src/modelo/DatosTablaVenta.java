/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Hp EliteBook
 */
public class DatosTablaVenta {
   private String descripcion; // Nombre producto
    private BigDecimal precio; // Precio Producto
    private int cantidad; // 
    private int descuento; // porcentaje: por ejemplo, 10 = 10%
    private BigDecimal subtotal;

    // Constructor
    public DatosTablaVenta(Producto p) {
        this.descripcion = p.getNombre();
        this.precio = p.getPrecio();
        this.cantidad = 1;
        this.descuento = 20;
        calcularSubtotal(); // Calcula el subtotal al crear el objeto
    }

    // Método para calcular el subtotal
    private void calcularSubtotal() {
        BigDecimal cantidadBD = BigDecimal.valueOf(cantidad); // Convertir int a BigDecimal
        BigDecimal totalSinDescuento = precio.multiply(cantidadBD); // precio * cantidad
        BigDecimal porcentaje = BigDecimal.valueOf(descuento).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP); // descuento / 100
        BigDecimal montoDescuento = totalSinDescuento.multiply(porcentaje); // total * porcentaje
        this.subtotal = totalSinDescuento.subtract(montoDescuento); // subtotal final
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    // Getters y Setters
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
        calcularSubtotal();
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}