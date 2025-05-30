/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Hp EliteBook
 */
public class MTablaRetiro extends AbstractTableModel{
    private final String[] columnas = {"Cajero", "Monto", "Fecha"};
    private final List<DatosTablaRetiro> retiros;

    public MTablaRetiro(List<DatosTablaRetiro> productos) {
        this.retiros = productos;
    }

    @Override
    public int getRowCount() {
        return retiros.size();
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
        DatosTablaRetiro retiro = retiros.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> retiro.getNombreCajero();
            case 1 -> retiro.getMonto();
            case 2 -> retiro.getFecha();
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Solo permitir editar cajero y monto
        return columnIndex == 0 || columnIndex == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DatosTablaRetiro retiro = retiros.get(rowIndex);
        fireTableCellUpdated(rowIndex, columnIndex); // Corrige aquí
    }
     // ✅ Método para obtener un retiro en una fila
    public DatosTablaRetiro getRetiroEnFila(int fila) {
        return retiros.get(fila);
    }

    // ✅ Método para eliminar un retiro por índice
    public void eliminarRetiro(int fila) {
        retiros.remove(fila);
        fireTableRowsDeleted(fila, fila);  // Notifica a la tabla del cambio
    }

    // ✅ Método opcional para obtener toda la lista
    public List<DatosTablaRetiro> getListaRetiros() {
        return retiros;
    }

    // ✅ Método opcional para actualizar un retiro en una fila (para modificar)
    public void actualizarRetiro(int fila, DatosTablaRetiro nuevoRetiro) {
        retiros.set(fila, nuevoRetiro);
        fireTableRowsUpdated(fila, fila);
    }
}
