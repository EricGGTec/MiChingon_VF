/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Desktop;
import com.itextpdf.text.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.DatosTablaVenta;

/**
 *
 * @author Hp EliteBook
 */

public class ModeloTicket {
    private List<String> tipoPago = new ArrayList<>();
    private List<DatosTablaVenta> productosVendidos;
    private String nombreCajero;
    private Date fecha;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal cambio = BigDecimal.ZERO;
    private BigDecimal cantidadPagada = BigDecimal.ZERO;
    private List<BigDecimal> pagos = new ArrayList<>();
    private int numItems = 0;

    public ModeloTicket(List<String> tipoPago, List<BigDecimal> pagosParciales, String cajero,
                           List<DatosTablaVenta> productos, BigDecimal total, BigDecimal cambio, BigDecimal pagado) {
        this.tipoPago = tipoPago;
        this.pagos = pagosParciales;
        this.nombreCajero = cajero;
        this.productosVendidos = productos;
        this.total = total.setScale(2, RoundingMode.HALF_UP);
        this.cambio = cambio.setScale(2, RoundingMode.HALF_UP);
        this.cantidadPagada = pagado.setScale(2, RoundingMode.HALF_UP);
        
    }

    public void generarYMostrarTicket(String nombreArchivo) {
        String textoTicket = generarTextoTicket();
        generarPDF(textoTicket, nombreArchivo);
    }

    private String generarTextoTicket() {
        StringBuilder nombrePrecio = new StringBuilder();
        for (DatosTablaVenta dtv : productosVendidos) {
            nombrePrecio.append(String.format("%-40s %s\n", dtv.getDescripcion(), dtv.getSubtotal()));
            numItems += dtv.getCantidad();
        }
        LocalTime ahora = LocalTime.now();

        // Formato de 24 horas: HH:mm:ss
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String hora = ahora.format(formato);

        StringBuilder tiposDePago = new StringBuilder();
        for (int i = 0; i < tipoPago.size(); i++) {
            tiposDePago.append(String.format("%-40s %s\n", tipoPago.get(i), pagos.get(i)));
        }

        return "                        RFC: CAL971950HR7\n"
                + "               Régimen General de la Ley del ISR\n"
                + "               Calzados MiChingon S.A. de C.V.\n"
                + "                    Emiliano Zapata No. 1\n"
                + "                    Col. Centro C.P. 68000\n"
                + "                   Oaxaca de Juarez, Oaxaca.\n"
                + "---------------------------------------------------------------\n"
                + "Staff: Trans:\n"
                + "Date: " + LocalDate.now() + "\t"+hora+"\n"
                + "---------------------------------------------------------------\n"
                + "NumeroSocio Nombre\n"
                + "---------------------------------------------------------------\n"
                + "Salesperson: " + nombreCajero + "\n"
                + "---------------------------------------------------------------\n"
                + "Descripcion:                                             Monto:\n"
                + "---------------------------------------------------------------\n"
                + nombrePrecio
                + "---------------------------------------------------------------\n"
                + "Total $                                                    " + total + "\n"
                + tiposDePago
                + "El cambio es:                                               " + cambio + "\n"
                + "---------------------------------------------------------------\n"
                + "Numero de Items:                                             " + numItems + "\n"
                + "---------------------------------------------------------------\n"
                + "                                                           Monto:\n"
                + "                                                              " + total + "\n"
                + "---------------------------------------------------------------\n"
                + "                 Gracias por su Compra.\n"
                + "                Solo cambios con ticket.\n"
                + "\n"
                + " Tus datos estan protegidos. Consulta el aviso\n "
                + "       de privacidad en michingon.com\n"
                + "\n"
                + " Si requiere factura solicitela al momento. De\n"
                + "   no hacerlo, esta venta se integrara a la\n"
                + "            factura global diaria.\n"
                + "\n"
                + "                   Politicas de pago\n"
                + "    Todos nuestros productos incluyen I.V.A.\n "
                + "  Aceptamos pagos en efectivo con targetas de\n "
                + "    crédito y débito (VISA MASTERCARD). No\n "
                + "    aceptamos American Express ni vales de "
                + "                    despensa. \n"
                + "    Al pagar con targeta bancaria es  necesario presentar\n"
                + "    una identifcacion oficial. Si necesita factura, debe\n"
                + "    solicitarla al momento de realizar \n"
                + "          su pago. No se realizaran factura de dias \n"
                + "                         posteriores a la compra.\n"
                + "\n"
                + "\t    Politicas de cambio.\n"
                + "     Debe presentar su ticket de compra original. No \n"
                + "        hay cambios en mercancia de oferta por ser \n"
                + "     ultimos pares o piezas. No hay garantia en tapas, \n"
                + "         tacones, y accesorios (luces, cierres, etc). El \n"
                + "         calzado, ropa y accesorios, tiene garantia de \n"
                + "     90 días. El cambio procederá bajo las siguientes \n"
                + "            condiciones: despegado descosturado, \n"
                + "     reventado de correas (bajo condiciones normales \n"
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
                + "                             ultimos pares o piezas.\n";
    }

    private void generarPDF(String contenido, String nombreArchivo) {
        Rectangle ticketSize = new Rectangle(280, 1200); 
        Document doc = new Document(ticketSize, 10, 10, 10, 10);
        
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
            doc.open();
            doc.add(new Paragraph(contenido));
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
}

