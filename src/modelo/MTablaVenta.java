/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Hp EliteBook
 */
public class MTablaVenta extends AbstractTableModel{
    private final String[] columnas = {"Descripción", "Precio", "Cantidad", "Descuento (%)", "Subtotal"};
    private final List<DatosTablaVenta> productos;

    public MTablaVenta(List<DatosTablaVenta> productos) {
        this.productos = productos;
    }

    @Override
    public int getRowCount() {
        return productos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DatosTablaVenta producto = productos.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> producto.getDescripcion();
            case 1 -> producto.getPrecio();
            case 2 -> producto.getCantidad();
            case 3 -> producto.getDescuento();
            case 4 -> producto.getSubtotal();
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Por ejemplo: solo permitir editar cantidad y descuento
        return columnIndex == 2 || columnIndex == 3;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DatosTablaVenta producto = productos.get(rowIndex);
        switch (columnIndex) {
            case 2 -> producto.setCantidad(Integer.parseInt(aValue.toString()));
            case 3 -> producto.setDescuento(Integer.parseInt(aValue.toString()));
        }
        fireTableCellUpdated(rowIndex, 4); // Actualiza subtotal
    }
}

