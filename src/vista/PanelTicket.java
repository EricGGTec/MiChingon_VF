/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import com.itextpdf.text.pdf.PdfWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.DatosTablaVenta;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.time.LocalDate;
/**
 *
 * @author Hp EliteBook
 */
public class PanelTicket extends javax.swing.JDialog {
    private List<String> tipoPago = new ArrayList<>();
    private List<DatosTablaVenta> productosVendidos;
    private String nombreCajero;
    private String precioPorProducto;
    private String nombreProducto;
    private Date fecha;
    private String NombrePrecio= "";
    private BigDecimal total = new BigDecimal(0), pago = new BigDecimal(0), CantidadPag =  new BigDecimal(0);
    private int numItems = 0;
    private String Tp= "";
    private List<BigDecimal> pagos = new ArrayList<>();
    
    /**
     * Creates new form PanelTicket
     */
    
    public PanelTicket(java.awt.Frame parent, boolean modal, List<String> tipo,List<BigDecimal> pagosParciales, String cajero, List<DatosTablaVenta> productos, BigDecimal Total, BigDecimal cambio, BigDecimal Pagado) {
        super(parent, modal);
        initComponents();
        tipoPago = tipo;
        this.nombreCajero = cajero;
        cargarProductos(productos);
        total = Total;
        pago = cambio;
        CantidadPag = Pagado;
        pagos = pagosParciales;
        ModificarTicket();
        String texto =  jTextArea2.getText();
        generarTicket(texto, "ticket.pdf");
        // Esto hace que se cierre justo cuando se muestre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                dispose(); // Ahora sí se cierra bien
            }
        });
    }
    
    public void ModificarTicket(){
        total = total.setScale(2, RoundingMode.HALF_UP);
        pago = pago.setScale(2, RoundingMode.HALF_UP);
        agregarTipoPago();
        jTextArea2.setText(
                  "                        RFC: CAL971950HR7\n"
                + "               Régimen General de la Ley del ISR\n"
                + "               Calzados MiChingon S.A. de C.V.\n"
                + "                        Emiliano Zapata No. 1\n"
                + "                       Col. Centro C.P. 68000\n"
                + "                     Oaxaca de Juarez, Oaxaca.\n"
                + "---------------------------------------------------------------\n"
                + "Staff: Trans:\n"
                + "Date: "+ LocalDate.now()+"\n"
                + "---------------------------------------------------------------\n"
                + "NumeroSocio Nombre\n"
                + "---------------------------------------------------------------\n"
                + "Salesperson: "+nombreCajero+"\n"
                + "---------------------------------------------------------------\n"
                + "Descripcion:                                             Monto:\n"
                + "---------------------------------------------------------------\n"
                + ""+NombrePrecio+""
                + "---------------------------------------------------------------\n"
                + "Total $                                                      "+total+"\n"
                + ""+Tp+""+"\n"
                + ""+"El cambio es:                                             "+pago+"\n"
                + "---------------------------------------------------------------\n"
                + "Numero de Items:                                             "+numItems+"\n"
                + "---------------------------------------------------------------\n"
                + "                                                             Monto:\n"
                + "                                                                 "+total+"\n"
                + "---------------------------------------------------------------\n"
                + "                 Gracias por su Compra.\n"
                + "                Solo cambios con ticket.\n"
                + "\n"
                + "      Tus datos estan protegidos. Consulta el aviso de \n"
                + "              privacidad en michingon.com\n"
                + "\n"
                + "     Si requiere factura solicitela al momento. De no \n"
                + "    hacerlo, esta venta se integrara a la factura global\n"
                + "                                      diaria.\n"
                + "\n"
                + "                             Politicas de pago\n"
                + "           Todos nuestros productos incluyen I.V.A \n"
                + "        Aceptamos pagos en efectivo con targetas de \n"
                + "       crédito y débito  (VISA MASTERCARD). No \n"
                + "   aceptamos American Express ni vales de despensa. \n"
                + "          Al pagar con targeta bancaria es  necesario \n"
                + "       presentar una identifcacion oficial. Si necesita\n"
                + "      factura, debe solicitarla al momento de realizar \n"
                + "          su pago. No se realizaran factura de dias \n"
                + "                         posteriores a la compra.\n"
                + "\n"
                + "\t    Politicas de cambio.\n"
                + "     Debe presetnar su ticket de compra original. No \n"
                + "        hay cambios en mercancia de oferta por ser \n"
                + "     utlimos pares o piezas. No hay garantia en tapas, \n"
                + "         tacones, y accesorios (luces, cierres, etc). El \n"
                + "         calzado, ropa y accesorios, tiene garantia de \n"
                + "     90 días. El cambio procederá bajo las siguientes \n"
                + "            condiciones: despegado descosturado, \n"
                + "     reventado de correas(bajo condiciones normales \n"
                + "      de uso). Mercancia que no procede al cambio: \n"
                + "       mojado, con mal olor, mal uso del cliente. Si \n"
                + "       el producto presenta una anomalía, de dudosa \n"
                + "       procedencia este se mandará a revisión con el \n"
                + "      proveedor para determinar la causa del defecto. \n"
                + "        En un periodo de 3 a 4 días hábiles se le dará \n"
                + "         una respuesta sobre el reclamo del producto.\n"
                + "\n"
                + "\t   Politica de devolucion:\n"
                + "     Debe presentar su ticket de compra original. Que \n"
                + "              el Zapato no este pisado, ni sucio. Las \n"
                + "     devoluciones deberan hacerse en su caja original. \n"
                + "       No se aceptan devoluciones de impares, ni de \n"
                + "      productos de promocion y/o descuento por ser \n"
                + "                             ultimos pares o piezas.\n");
    }
    
    public static void generarTicket(String contenido, String nombreArchivo) {
        Document doc = new Document();

        try {
            PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
            doc.open();

            // Agregar el texto completo del JTextArea
            Paragraph parrafo = new Paragraph(contenido);
            doc.add(parrafo);

            doc.close();

            // Abrir el PDF automáticamente
            File archivoPDF = new File(nombreArchivo);
            if (archivoPDF.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoPDF);
            }

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public void cargarProductos(List<DatosTablaVenta> productos){
        for(DatosTablaVenta dtv: productos){
            NombrePrecio += dtv.getDescripcion() +"                                    " + dtv.getSubtotal()+"\n";
            numItems = dtv.getCantidad();
        }
    }
    public void agregarTipoPago(){
        for (int i = 0; i < tipoPago.size(); i++) 
            Tp += tipoPago.get(i) + "                                             " + pagos.get(i) + "\n";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setText("                        RFC: CAL971950HR7\n               Régimen General de la Ley del ISR\n               Calzados MiChingon S.A. de C.V.\n                        Emiliano Zapata No. 1\n                       Col. Centro C.P. 68000\n                     Oaxaca de Juarez, Oaxaca.\n---------------------------------------------------------------\nStaff: Trans:\nDate: \n---------------------------------------------------------------\nNumeroSocio Nombre\n---------------------------------------------------------------\nSalesperson: Nombr del vendedor\n---------------------------------------------------------------\nDescripcion:\t\t\tMonto:\n---------------------------------------------------------------\nNombre Producto\t\tPrecio:\n---------------------------------------------------------------\nTotal $\t\t\ttotal\nFormato de Pago\t\tpag\n---------------------------------------------------------------\nNumber of items:\t\titems\n---------------------------------------------------------------\n\t\t                 \tMonto:\n\t\t                 \tCantidad\n---------------------------------------------------------------\n\tGracias por su Compra.\n\tSolo cambios con ticket.\n\n      Tus datos estan protegidos. Consulta el aviso de \n              privacidad en www.michingon.com\n\n     Si requiere factura solicitela al momento. De no \n    hacerlo, esta venta se integrara a la factura global\n                                      diaria.\n\n                             Politicas de pago\n           Todos nuestros productos incluyen I.V.A \n        Aceptamos pagos en efectivo con targetas de \n       crédito y débito  (VISA MASTERCARD). No \n   aceptamos American Express ni vales de despensa. \n          Al pagar con targeta bancaria es  necesario \n       presentar una identifcacion oficial. Si necesita\n      factura, debe solicitarla al momento de realizar \n          su pago. No se realizaran factura de dias \n                         posteriores a la compra.\n\n\t    Politicas de cambio.\n     Debe presetnar su ticket de compra original. No \n        hay cambios en mercancia de oferta por ser \n     utlimos pares o piezas. No hay garantia en tapas, \n         tacones, y accesorios (luces, cierres, etc). El \n         calzado, ropa y accesorios, tiene garantia de \n     90 días. El cambio procederá bajo las siguientes \n            condiciones: despegado descosturado, \n     reventado de correas(bajo condiciones normales \n      de uso). Mercancia que no procede al cambio: \n       mojado, con mal olor, mal uso del cliente. Si \n       el producto presenta una anomalía, de dudosa \n       procedencia este se mandará a revisión con el \n      proveedor para determinar la causa del defecto. \n        En un periodo de 3 a 4 días hábiles se le dará \n         una respuesta sobre el reclamo del producto.\n\n\t   Politica de devolucion:\n     Debe presentar su ticket de compra original. Que \n              el Zapato no este pisado, ni sucio. Las \n     devoluciones deberan hacerse en su caja original. \n       No se aceptan devoluciones de impares, ni de \n      productos de promocion y/o descuento por ser \n                             ultimos pares o piezas.\n");
        jTextArea2.setPreferredSize(new java.awt.Dimension(400, 1228));
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PanelTicket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PanelTicket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PanelTicket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PanelTicket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PanelTicket dialog = new PanelTicket(new javax.swing.JFrame(), true, new ArrayList<>(), new ArrayList<>(),"", new ArrayList<DatosTablaVenta>(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}
