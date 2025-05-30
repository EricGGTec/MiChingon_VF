/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista;

import control.AdnDatos;
import control.ClienteJpaController;
import control.DetallePedidoJpaController;
import control.InventarioJpaController;
import control.PedidoJpaController;
import control.ProductoJpaController;
import control.ProveedorJpaController;
import control.UbicacionPedidoJpaController;
import control.exceptions.NonexistentEntityException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Pedido;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.loading.MLet;
import javax.swing.SpinnerNumberModel;
import modelo.Cliente;
import modelo.DetallePedido;
import modelo.Inventario;
import modelo.Producto;
import modelo.Proveedor;
import modelo.UbicacionPedido;

/**
 *
 * @author magal
 */
public class InterfazPed extends javax.swing.JDialog {

    /**
     * Creates new form InterfazPed
     */
    private AdnDatos adn;
    private EntityManagerFactory emf;
    private PedidoJpaController cPedido;
    private DetallePedidoJpaController cDetalleP;
    private ClienteJpaController cCliente;
    private ProductoJpaController cProducto;
    private UbicacionPedidoJpaController cUbicacionP;
    private InventarioJpaController cInventario;
    private ProveedorJpaController cProveedor;

    private List<Inventario> inventario;
    private List<DetallePedido> detalles;
    private List<Pedido> pedidos;
    private List<Cliente> clientes;
    private List<Producto> productos;
    private List<UbicacionPedido> ubicaciones;
    private List<Proveedor> proveedores;
    private List<Pedido> pedidosUbi = new ArrayList<>();
    private List<Producto> productosInv = new ArrayList<>();

    private Map<String, Inventario> inventario_index = new HashMap<>();

    private Pedido pbusqueda;
    private boolean banderaEncontrado = false;
    //tabla buscar
    private DefaultTableModel mt;
    //tabla ubicacion
    private DefaultTableModel mtU;

    private String nl = System.lineSeparator();
    private Date fechaEla = new Date();
    private Date fechaEntre = new Date();
    private SimpleDateFormat formato = new SimpleDateFormat("dd 'de' MMM 'de' yyy ");
    private boolean confirmaFecha = false;
    //combos cargar registraR
    private Inventario selecPro;
    private String selecTa = "";
    private String selecCo = "";
    private SpinnerNumberModel Msp = new SpinnerNumberModel(1, 1, 1, 1);
    //generar total
    private float acumulaTotal = 0;
    private Pedido NuevoP = null;
    //cantidad de inventario

    //guardar detalles externos
    // private Map<Pedido, String> descripciones = new HashMap<>();
    //valores para calcular precios
    private BigDecimal costoProductos = BigDecimal.ZERO;
    private BigDecimal totalMonto = BigDecimal.ZERO;

    //fechas agregar y editar ubicaicion
    private Date fechaE = null;
    private Date fechaA = null;
    //confirmar fechas ubicacion
    private boolean conFub = false;

    public InterfazPed(java.awt.Frame parent, boolean modal) throws Exception {
        super(parent, modal);
        initComponents();
        adn = new AdnDatos();
        cPedido = new PedidoJpaController(adn.getEnf());
        cDetalleP = new DetallePedidoJpaController(adn.getEnf());
        cCliente = new ClienteJpaController(adn.getEnf());
        cProducto = new ProductoJpaController(adn.getEnf());
        cInventario = new InventarioJpaController(adn.getEnf());
        cUbicacionP = new UbicacionPedidoJpaController(adn.getEnf());
        cProveedor = new ProveedorJpaController(adn.getEnf());

        clientes = cCliente.findClienteEntities();
        productos = cProducto.findProductoEntities();
        detalles = cDetalleP.findDetallePedidoEntities();
        inventario = cInventario.findInventarioEntities();
        proveedores = cProveedor.findProveedorEntities();
        try{
        ubicaciones = cUbicacionP.findUbicacionPedidoEntities();}
        catch(Exception e){
            
        }

        mt = new DefaultTableModel(new Object[]{"Id Pedido", "Cliente", "Fecha de entrega", "Productos", "Estado", "Total"}, 0);
        mtU = new DefaultTableModel(new Object[]{"Id Pedido", "Cliente", "Ubicacion", "Proveedor", "Fecha estimada de entrega"}, 0);
        tUbi.setModel(mtU);
        cargarProductoCB();
        pedidos = cPedido.findPedidoEntities();
        cantidadSP.setModel(Msp);
        cargarPedidos();
        calendarioSelec.setEnabled(false);
        Bcf2.setEnabled(false);
        Bcancelar.setEnabled(false);
        cargarCB();
        cargarCBubi();
        productosInvt();

    }

    private void cargarProductoCB() {
        productoCB.removeAllItems();
        inventario_index.clear();
        inventario = cInventario.findInventarioEntities();
        int nc = 0;
        for (Inventario ivn : inventario) {
            inventario_index.put(ivn.getIdProducto().getNombre() + " Talla:" + ivn.getTalla() + " Color:" + ivn.getColor(), ivn);
            productoCB.addItem(ivn.getIdProducto().getNombre() + " Talla:" + ivn.getTalla() + " Color:" + ivn.getColor());
        }
    }

    private void productosInvt() {
        productosInv.clear();
        productos = cProducto.findProductoEntities();
        for (Producto p : productos) {//agregar solo los que estan en inven

            if (buscaInventario(p) != null) {
                productosInv.add(p);
                System.out.println("cargar" + p.getNombre());
            }
            System.out.println("productos en inventario actualizado");
        }
        //inventario = cInventario.findInventarioEntities();
        //for(Inventario inv:inventario)

    }

    public void cargarPedidos() throws Exception {
        pedidos = cPedido.findPedidoEntities();
        int numfilas = mt.getRowCount();
        for (int i = numfilas - 1; i >= 0; i--) {
            mt.removeRow(i);
        }
        if (banderaEncontrado) {
            String pds = buscaProductos(pbusqueda);
            if (pds == null) {
                pds = "detalles no encontrados";
            }
            Object[] fila = {
                pbusqueda.getIdPedido(),
                pbusqueda.getIdCliente().getNombre() + " " + pbusqueda.getIdCliente().getApellido(),
                formato.format(pbusqueda.getFechaEntregaEstimada()), pds,
                pbusqueda.getEstado(),
                pbusqueda.getTotal()
            };
            mt.addRow(fila);
            tPedidos.setModel(mt);
            String text = "Id.Pedido:" + pbusqueda.getIdPedido() + nl + " Cliente:" + pbusqueda.getIdCliente().getNombre() + " " + pbusqueda.getIdCliente().getApellido() + nl
                    + " Fecha elaboracion: " + formato.format(pbusqueda.getFechaPedido())
                    + " Fecha estimada de entrega: " + formato.format(pbusqueda.getFechaEntregaEstimada()) + nl + "Productos:" + nl + buscaProductos(pbusqueda) + nl
                    + "Estado: " + pbusqueda.getEstado() + nl + " TOTAL: " + pbusqueda.getTotal();
            detallesText.setText(text);
            banderaEncontrado = false;
        } else {
            for (Pedido p : pedidos) {
                String pds = buscaProductosMuestra(p);
                if (pds == null) {
                    pds = "detalles no encontrados";
                }
                Object[] fila = {
                    p.getIdPedido(),
                    p.getIdCliente().getNombre() + " " + p.getIdCliente().getApellido(),
                    convertirFecha(p.getFechaEntregaEstimada()), pds, p.getEstado(),
                    p.getTotal()
                };
                mt.addRow(fila);

            }
        }
        tPedidos.setModel(mt);

    }

    private void cargarCB() {
        clientes = cCliente.findClienteEntities();
        productos = cProducto.findProductoEntities();

        clienteCB.removeAllItems();
        productoCB.removeAllItems();

        clienteCB.addItem("Selecciona cliente");
        productoCB.addItem("Selecciona producto");
        cantidadSP.setValue(0);

        for (Cliente c : clientes) {
            clienteCB.addItem(c.getNombre() + " " + c.getApellido());
        }
        /*for (Producto p : productosInv) {//agregar solo los que estan en inven
            productoCB.addItem("id: " + p.getIdProducto() + " -- " + p.getNombre());
            
        }*/

    }

    private void cargarCBubi() {
        actualizaPedidosUbi();
        proveedores = cProveedor.findProveedorEntities();

        ubiCB.removeAllItems();
        ubiCB2.removeAllItems();

        pedidosUbiCB.removeAllItems();
        proveCB1.removeAllItems();
        proveCB2.removeAllItems();

        ubiCB.addItem("Seleciona ubicacion");
        ubiCB2.addItem("Seleciona ubicacion");
        pedidosUbiCB.addItem("Selecciona pedido");
        proveCB1.addItem("Selecciona proveedor ");
        proveCB2.addItem("Selecciona proveedor ");

        ubiCB.addItem("En transito");
        ubiCB.addItem("En almacen");
        ubiCB.addItem("Entregado");
        ubiCB.addItem("Recogido");

        ubiCB2.addItem("En transito");
        ubiCB2.addItem("En almacen");
        ubiCB2.addItem("Entregado");
        ubiCB2.addItem("Recogido");

        for (Pedido ped : pedidosUbi) {
            pedidosUbiCB.addItem("id: " + ped.getIdPedido() + " -- " + ped.getIdCliente().getNombre() + " " + ped.getIdCliente().getApellido());

        }
        for (Proveedor pro : proveedores) {
            proveCB1.addItem("id: " + pro.getIdProveedor() + " -- " + pro.getNombre());
            proveCB2.addItem("id: " + pro.getIdProveedor() + " -- " + pro.getNombre());

        }
        BactualizarU.setEnabled(false);
        fechaA = null;
        fechaE = null;
        Bcf1.setEnabled(false);
        Bcf2.setEnabled(false);
        textFechaAcU.setText("");
        fE.setText("Fecha de entrega");
        calenA.setEnabled(false);
        calenE.setEnabled(false);
        cargarRegistrosUbi();
        conFub = false;

    }

    private void actualizaPedidosUbi() {
        ubicaciones = cUbicacionP.findUbicacionPedidoEntities();
        pedidos = cPedido.findPedidoEntities();
        pedidosUbi.clear();
        for (Pedido p : pedidos) {

            boolean encontrado = false;

            for (UbicacionPedido u : ubicaciones) {
                if (u.getIdPedido().getIdPedido().equals(p.getIdPedido())) {
                    encontrado = true;
                    break;
                }

            }

            if (!encontrado) {
                pedidosUbi.add(p); // No tiene ubicación asignada
            }
        }
    }

    private String convertirFecha(Date fecha) {
        if (fecha == null) {
            return "No se registró fecha";
        }

        // Convertir Date a LocalDateTime
        LocalDateTime fechaHora = fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM yyyy - HH:mm:ss");
        return fechaHora.format(formatter);
    }

    private String buscaProductos(Pedido p) throws Exception {
        String listaP = "";
        float precioP = 0;
        float totalPedido = 0;
        BigDecimal totalTem;
        BigDecimal precioUnitario;
        detalles = cDetalleP.findDetallePedidoEntities();

        for (DetallePedido de : detalles) {
            if (de.getIdPedido().getIdPedido() == p.getIdPedido()) {
                //calcular precio por cantidad de productos
                precioUnitario = new BigDecimal(de.getCantidad());
                totalTem = precioUnitario.multiply(de.getIdProducto().getPrecio());
                precioP = totalTem.floatValue();
                totalPedido += precioP;
                listaP += "Producto: " + de.getIdProducto().getNombre() + " Cantidad: " + de.getCantidad() + " Talla: " + de.getTalla() + " Color: " + de.getColor() + " Precio Individual: $" + de.getIdProducto().getPrecio() + nl
                        + " Subtotal:" + precioP + nl;
            }
        }

        return listaP;

    }

    private Inventario buscaInventario(Producto p) {
        inventario = cInventario.findInventarioEntities();
        for (Inventario in : inventario) {
            System.out.println("buscando en inventario, reg " + in.getIdInventario());
            if (in.getIdProducto().getIdProducto() == p.getIdProducto()) {
                return in;
            }
        }
        return null;
    }

    private String buscaProductosMuestra(Pedido p) throws Exception {
        String listaP = "";

        detalles = cDetalleP.findDetallePedidoEntities();
        for (DetallePedido de : detalles) {
            if (de.getIdPedido().getIdPedido() == p.getIdPedido()) {
                //calcular precio por cantidad de productos
                listaP += "Producto: " + de.getIdProducto().getNombre();
            }
        }
        return listaP;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCalendar1 = new com.toedter.calendar.JCalendar();
        jButton7 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        panelesPedido = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtResult = new javax.swing.JLabel();
        TextIdP = new javax.swing.JTextField();
        bBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tPedidos = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detallesText = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        Bcancelar = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        calendarioSelec = new com.toedter.calendar.JCalendar();
        Bconfi = new javax.swing.JButton();
        textFecha = new javax.swing.JLabel();
        clienteCB = new javax.swing.JComboBox<>();
        productoCB = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cantidadSP = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        textPrecio = new javax.swing.JLabel();
        textTotal = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        textIdU = new javax.swing.JTextField();
        BconsultarU = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        textInfo = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tUbi = new javax.swing.JTable();
        panelEA = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        BactualizarU = new javax.swing.JButton();
        ubiCB = new javax.swing.JComboBox<>();
        textFechaAcU = new javax.swing.JLabel();
        fE = new javax.swing.JLabel();
        calenE = new com.toedter.calendar.JCalendar();
        Bcf1 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        proveCB1 = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        pedidosUbiCB = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        ubiCB2 = new javax.swing.JComboBox<>();
        fA = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        calenA = new com.toedter.calendar.JCalendar();
        Bcf2 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        proveCB2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();

        jButton7.setText("jButton7");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel1.setText("Pedido");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panelesPedido.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelesPedidoStateChanged(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel2.setText("Codigo del pedido:");

        txtResult.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        txtResult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtResult.setText("Resultados");

        TextIdP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextIdPActionPerformed(evt);
            }
        });

        bBuscar.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        bBuscar.setText("Buscar");
        bBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bBuscarMouseClicked(evt);
            }
        });
        bBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBuscarActionPerformed(evt);
                bBuscarActionPerformed1(evt);
            }
        });

        tPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tPedidosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tPedidos);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel9.setText("Busca el codigo del producto para ver detalles");

        detallesText.setColumns(20);
        detallesText.setRows(5);
        jScrollPane2.setViewportView(detallesText);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel11.setText("Detalles del pedido");

        Bcancelar.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        Bcancelar.setText("Cancelar pedido ");
        Bcancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BcancelarActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton4.setText("Actualizar tabla");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextIdP, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81)
                        .addComponent(bBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Bcancelar)
                        .addGap(24, 24, 24))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(txtResult, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TextIdP, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Bcancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtResult)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(113, 113, 113))
        );

        panelesPedido.addTab("Busqueda", jPanel3);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel4.setText("Registrar pedido");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel5.setText("Cliente:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel6.setText("Producto:");

        jButton2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton2.setText("Agregar a pedido");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton3.setText("Finalizar pedido");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel10.setText("Fecha de entrega:");

        jButton5.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton5.setText("Seleccionar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        calendarioSelec.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                calendarioSelecMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                calendarioSelecMousePressed(evt);
            }
        });

        Bconfi.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        Bconfi.setText("Confirmar");
        Bconfi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BconfiActionPerformed(evt);
            }
        });

        textFecha.setText("Fecha:");

        clienteCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        clienteCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteCBActionPerformed(evt);
            }
        });

        productoCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        productoCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productoCBActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel7.setText("Cantidad: ");

        cantidadSP.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cantidadSPStateChanged(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel15.setText("Precio individual:");

        textTotal.setText("Total: $");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(65, 65, 65)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(textTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel7))
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(clienteCB, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(productoCB, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(cantidadSP, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 236, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(textFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49)
                                .addComponent(Bconfi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(calendarioSelec, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(0, 64, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(clienteCB, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(productoCB, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(38, 38, 38)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(4, 4, 4)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(textFecha)
                                    .addComponent(Bconfi, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(calendarioSelec, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(textPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(jButton5))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(cantidadSP, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        panelesPedido.addTab("Registro", jPanel4);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel12.setText("Consultar la ubicacion del pedido");

        textIdU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textIdUActionPerformed(evt);
            }
        });

        BconsultarU.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        BconsultarU.setText("Consultar");
        BconsultarU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BconsultarUActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel13.setText("Ingresa el identificador del pedido:");

        textInfo.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        textInfo.setText("Informacion");

        tUbi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tUbi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tUbiMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tUbi);

        panelEA.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelEAStateChanged(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel14.setText("Selecciona  un pedido en la tabla para editar la ubicacion");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel16.setText("Ubicacion:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel17.setText("Fecha de actualizacion:");

        BactualizarU.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        BactualizarU.setText("Actualizar");
        BactualizarU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BactualizarUActionPerformed(evt);
            }
        });

        ubiCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ubiCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubiCBActionPerformed(evt);
            }
        });

        fE.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        fE.setText("Fecha de entrega: ");

        Bcf1.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        Bcf1.setText("Cambiar fecha");
        Bcf1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Bcf1ActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel22.setText("Proveedor:");

        proveCB1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fE, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(36, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ubiCB, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(61, 61, 61))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(BactualizarU, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(textFechaAcU, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(41, 41, 41)
                                        .addComponent(proveCB1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(26, 26, 26)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(Bcf1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(calenE, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(ubiCB, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(proveCB1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                        .addComponent(BactualizarU, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(textFechaAcU, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(calenE, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Bcf1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelEA.addTab("Editar", jPanel5);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel18.setText("Pedido:");

        pedidosUbiCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        pedidosUbiCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pedidosUbiCBActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel19.setText("Ubicaion:");

        ubiCB2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ubiCB2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubiCB2ActionPerformed(evt);
            }
        });

        fA.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        fA.setText("Fecha de entrega:");

        jButton6.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton6.setText("Agregar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        Bcf2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        Bcf2.setText("Cambiar  fecha");
        Bcf2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Bcf2ActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel20.setText("Proveedor:");

        proveCB2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(59, 59, 59))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(ubiCB2, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(pedidosUbiCB, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(proveCB2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(77, 77, 77)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(Bcf2, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(calenA, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(pedidosUbiCB, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ubiCB2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(proveCB2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fA, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calenA, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(Bcf2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        panelEA.addTab("Agregar", jPanel6);

        jButton1.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton1.setText("Actualizar tabla");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel3.setText("Encuentra en la tabla los pedidos que tienen valor de ubicacion ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(panelEA, javax.swing.GroupLayout.PREFERRED_SIZE, 795, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 23, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 771, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(textIdU, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(102, 102, 102)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(BconsultarU, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(textInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel13))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textIdU, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BconsultarU, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textInfo)
                .addGap(9, 9, 9)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelEA)
                .addContainerGap())
        );

        panelesPedido.addTab("Localizacion", jPanel2);

        jButton8.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jButton8.setText("Salir");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelesPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 818, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(226, 226, 226)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(501, 501, 501)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(panelesPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 681, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBuscarActionPerformed
        // TODO add your handling code here:
        botonBP();

    }//GEN-LAST:event_bBuscarActionPerformed

    private void bBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bBuscarMouseClicked
        // TODO add your handling code here:
        botonBP();

    }//GEN-LAST:event_bBuscarMouseClicked

    private void bBuscarActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBuscarActionPerformed1
        // TODO add your handling code here:

    }//GEN-LAST:event_bBuscarActionPerformed1

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            registrarPedido();

        } catch (Exception ex) {
            Logger.getLogger(InterfazPed.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        calendarioSelec.setEnabled(true);
        calendarioSelec.setMinSelectableDate(new Date());
        Bconfi.setEnabled(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void BconfiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BconfiActionPerformed
        // TODO add your handling code here:
        guardarFecha();
        confirmaFecha = true;
    }//GEN-LAST:event_BconfiActionPerformed

    private void calendarioSelecMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarioSelecMouseClicked
        // TODO add your handling code here:
        textFecha.setText("Fecha: " + calendarioSelec.getDate());
    }//GEN-LAST:event_calendarioSelecMouseClicked

    private void calendarioSelecMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarioSelecMousePressed
        // TODO add your handling code here:
        textFecha.setText("Fecha: " + calendarioSelec.getDate());
    }//GEN-LAST:event_calendarioSelecMousePressed

    private void clienteCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteCBActionPerformed

    private void productoCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productoCBActionPerformed
        // TODO add your handling code here:
        //cambiar talla, tipo y color segun producto
        selecPro = (Inventario) inventario_index.get(productoCB.getSelectedItem());
        //mostrar solo productos en inventario
        /*colorCB.removeAllItems();
            tallaCB.removeAllItems();

            Producto seP = productosInv.get(selecPro - 1);
            System.out.println("producto sleciconadao" + seP.getNombre());
            
            String tallas[] = tallasInventario(seP);//metodo talla
            if (tallas != null) {
                for (int i = 0; i < tallas.length; i++) {
                    tallaCB.addItem(tallas[i]);
                }
            }*/
        if (selecPro != null) {
            textPrecio.setText(" $ " + selecPro.getIdProducto().getPrecio());
            Msp.setMaximum(selecPro.getCantidadActual());
            Msp.setMinimum(1);
            Msp.setValue(1);
            cantidadSP.setModel(Msp);
        }
    }//GEN-LAST:event_productoCBActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:

        if (NuevoP != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Pedido" + NuevoP.getIdPedido() + " finalizado Monto Total: $" + totalMonto.toString(),
                    "Informacion",
                    JOptionPane.WARNING_MESSAGE
            );
            //guardarTotal

            NuevoP.setTotal(totalMonto);
            try {
                cPedido.edit(NuevoP);
                System.out.println(totalMonto + "Total registrado de pedido" + NuevoP.getIdPedido());

            } catch (NonexistentEntityException ex) {
                Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        //preparar para nuevo pedido
        NuevoP = null;
        totalMonto = BigDecimal.ZERO;
        costoProductos = BigDecimal.ZERO;
        cargarCB();
        textTotal.setText("Total: $0");
        textPrecio.setText("$");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void BcancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BcancelarActionPerformed
        // TODO add your handling code here:
        //cancelacion
        int filaS = tPedidos.getSelectedRow();

        if (filaS >= 0) {
            int idPed = Integer.parseInt(mt.getValueAt(filaS, 0).toString());
            Pedido ped = buscaPedidoTabla(idPed);
            int resp = JOptionPane.showConfirmDialog(
                    null,
                    "¿Cancelar el pedido?" + ped.getIdPedido(),
                    "Aviso",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (resp == JOptionPane.YES_OPTION) {

                try {
                    ped.setEstado("cancelado");
                    cPedido.edit(ped);
                    System.out.println("cancelado pedido " + ped.getIdCliente());

                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(InterfazPed.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (Exception ex) {
                    Logger.getLogger(InterfazPed.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Se evito la cancelacion",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );

            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona una fila",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }//GEN-LAST:event_BcancelarActionPerformed

    private void botonBP() {
        detallesText.setText("");
        String idbusqueda = TextIdP.getText();
        if (!idbusqueda.isEmpty() && esEntero(idbusqueda)) {
            pedidos = cPedido.findPedidoEntities();
            pbusqueda = cPedido.findPedido(Integer.parseInt(idbusqueda));

            if (pbusqueda != null) {
                banderaEncontrado = true;
                txtResult.setText("Resultados: Pedido encontrado");
            } else {
                banderaEncontrado = false;
            }

            if (!banderaEncontrado) {
                txtResult.setText(" Resultados: No se encontraron resultados");
            }
            try {
                cargarPedidos();

            } catch (Exception ex) {
                Logger.getLogger(InterfazPed.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            txtResult.setText(" Resultados: No se encontraron resultados");
            JOptionPane.showMessageDialog(
                    null,
                    "Ingresa un identificador",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            try {
                cargarPedidos();

            } catch (Exception ex) {
                Logger.getLogger(InterfazPed.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    private void tPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tPedidosMouseClicked
        // TODO add your handling code here:
        Bcancelar.setEnabled(true);


    }//GEN-LAST:event_tPedidosMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            // TODO add your handling code here:
            cargarPedidos();

        } catch (Exception ex) {
            Logger.getLogger(InterfazPed.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void textIdUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textIdUActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textIdUActionPerformed

    private void BconsultarUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BconsultarUActionPerformed
        // TODO add your handling code here:
        cargarUbicacion();
    }//GEN-LAST:event_BconsultarUActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        //agregar nuevo pedido a ubicacion
        int s = pedidosUbiCB.getSelectedIndex();
        int su = ubiCB2.getSelectedIndex();
        fechaA = calenA.getDate();
        if (s > 0 && su > 0 && proveCB2.getSelectedIndex() > 0) {
            String uSelec = leeUbiSelec(s);
            Proveedor pro = proveedores.get(proveCB2.getSelectedIndex() - 1);
            //crear registro en ubicaicon
            UbicacionPedido ub = new UbicacionPedido();
            ub.setIdPedido(pedidosUbi.get(s - 1));
            ub.setIdProveedor(pro);
            ub.setEstadoLogistica(uSelec);
            ub.setFechaActualizacion(new Date());

            cUbicacionP.create(ub);
            System.out.println("pedido " + pedidosUbi.get(s - 1) + "se creo registro ubi " + ub.getIdUbicacion());

//editar fecha entrega en pedido
            if (fechaA != null && conFub) {
                try {
                    Pedido pd = pedidosUbi.get(s - 1);
                    pd.setFechaEntregaEstimada(fechaA);
                    cPedido.edit(pd);

                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(InterfazPed.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (Exception ex) {
                    Logger.getLogger(InterfazPed.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Se actualizo fecha entrega de pedido " + pedidosUbi.get(s - 1).getIdPedido());

            }

            cargarCBubi();
            cargarRegistrosUbi();

        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Ingresa valores validos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );

        }

    }//GEN-LAST:event_jButton6ActionPerformed

    private void BactualizarUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BactualizarUActionPerformed
        // TODO add your handling code here:
        int ubiS = tUbi.getSelectedRow();
        if (ubiS >= 0) {
            int cbS = ubiCB.getSelectedIndex();
            if (cbS > 0 || proveCB1.getSelectedIndex() > 0 || conFub) {
                int idPed = Integer.parseInt(mtU.getValueAt(ubiS, 0).toString());
                Pedido ped = buscaPedidoTabla(idPed);

                int resp = JOptionPane.showConfirmDialog(
                        null,
                        "¿Modificar la ubicacion del pedido?" + ped.getIdPedido() + " a " + ubiCB.getItemAt(cbS),
                        "Aviso",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (resp == JOptionPane.YES_OPTION) {
                    UbicacionPedido encontradoU = buscarUbiPedidos(ped);
                    try {
                        if (cbS > 0) {
                            String valorU = leeUbiSelec(cbS);
                            encontradoU.setEstadoLogistica(valorU);
                        }
                        if (proveCB1.getSelectedIndex() > 0) {
                            Proveedor prov = proveedores.get(proveCB1.getSelectedIndex() - 1);
                            encontradoU.setIdProveedor(prov);

                        }
                        cUbicacionP.edit(encontradoU);
                        System.out.println(encontradoU.getIdPedido().getIdPedido() + " modiicado  ");

                        if (fechaE != null && conFub) {
                            ped.setFechaEntregaEstimada(fechaE);

                            cPedido.edit(ped);
                            System.out.println("fecha entrega modifcada a " + fechaE);

                        }

                        cargarCBubi();
                    } catch (Exception e) {
                        System.out.println("ERROR al cambiar ubicacion");
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Se cancelo el cambio",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE
                    );

                }

            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Ingresa campos validos",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona una fila valida en la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        cargarCBubi();
    }//GEN-LAST:event_BactualizarUActionPerformed

    private void tUbiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tUbiMouseClicked
        // TODO add your handling code here:
        //activar edicion
        BactualizarU.setEnabled(true);

    }//GEN-LAST:event_tUbiMouseClicked

    private void ubiCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubiCBActionPerformed
        // TODO add your handling code here:
        textFechaAcU.setText(formato.format(new Date()));
        calenE.setEnabled(true);
        calenE.setMinSelectableDate(new Date());
        Bcf1.setEnabled(true);
    }//GEN-LAST:event_ubiCBActionPerformed

    private void Bcf2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Bcf2ActionPerformed
        // TODO add your handling code here:
        fechaA = calenA.getDate();
        conFub = true;
        fA.setText("Fecha de entrega " + formato.format(fechaA));
    }//GEN-LAST:event_Bcf2ActionPerformed

    private void pedidosUbiCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pedidosUbiCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pedidosUbiCBActionPerformed


    private void ubiCB2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubiCB2ActionPerformed
        // TODO add your handling code here:
        calenA.setEnabled(true);
        calenA.setMinSelectableDate(new Date());
        Bcf2.setEnabled(true);
    }//GEN-LAST:event_ubiCB2ActionPerformed

    private void Bcf1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Bcf1ActionPerformed
        // TODO add your handling code here:
        fechaE = calenE.getDate();
        conFub = true;
        fE.setText("Fecha de entrega " + formato.format(fechaE));

    }//GEN-LAST:event_Bcf1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        try {
            // TODO add your handling code here:
            cargarPedidos();
        } catch (Exception ex) {
            Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4MouseClicked

    private void TextIdPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextIdPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TextIdPActionPerformed

    private void panelesPedidoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panelesPedidoStateChanged
        int ind = panelesPedido.getSelectedIndex();
        if (ind == 1) {
            cargarProductoCB();

        }
    }//GEN-LAST:event_panelesPedidoStateChanged

    private void cantidadSPStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cantidadSPStateChanged
        // TODO add your handling code here:
        int ent = Integer.parseInt(cantidadSP.getValue().toString());
        if (selecPro != null) {
            if (ent > selecPro.getCantidadActual()) {
                Msp.setValue(1);
                cantidadSP.setModel(Msp);
            }
        }
    }//GEN-LAST:event_cantidadSPStateChanged

    private void panelEAStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panelEAStateChanged
        // TODO add your handling code here:
        //cargar pedidos nuevos para agrgar a comno pedidos
        if (panelEA.getSelectedIndex() == 2) {
            cargarCBubi();
        }

    }//GEN-LAST:event_panelEAStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        cargarUbicacion();
    }//GEN-LAST:event_jButton1ActionPerformed
    private String[] colorInventario(Producto p, String t) {
        String colores[] = null;
        inventario = cInventario.findInventarioEntities();
        int c = 0;
        for (Inventario inv : inventario) {
            if (inv.getIdProducto() == p && inv.getTalla().equals(t)) {
                colores[c] = inv.getColor();
                c++;
            }
        }
        return colores;
    }

    private String[] tallasInventario(Producto p) {
        String tallas[] = null;
        inventario = cInventario.findInventarioEntities();
        int c = 0;
        for (Inventario inv : inventario) {
            if (inv.getIdProducto() == p) {
                tallas[c] = inv.getTalla();
                c++;
            }
        }
        return tallas;
    }

    private void cargarUbicacion() {

        String idUbi = textIdU.getText();
        if (!idUbi.isEmpty() && esEntero(idUbi)) {
            boolean ubi = false;
            Pedido pencontrado = null;
            UbicacionPedido uencontrado = null;
            ubicaciones = cUbicacionP.findUbicacionPedidoEntities();
            pedidos = cPedido.findPedidoEntities();

            for (UbicacionPedido u : ubicaciones) {
                for (Pedido p : pedidos) {
                    if (u.getIdPedido().getIdPedido() == p.getIdPedido()) {
                        pencontrado = p;
                        uencontrado = u;
                        ubi = true;
                        break;
                    } else {
                        ubi = false;
                    }
                }

            }
            if (ubi) {
                textInfo.setText("Informacion: Pedido encontrado");
                int numfilas = mtU.getRowCount();
                for (int i = numfilas - 1; i >= 0; i--) {
                    mtU.removeRow(i);
                }
                Object[] fila = {pencontrado.getIdPedido(),
                    pencontrado.getIdCliente().getNombre() + " " + pencontrado.getIdCliente().getApellido(),
                    uencontrado.getEstadoLogistica(),
                    uencontrado.getIdProveedor().getNombre(),
                    formato.format(pencontrado.getFechaEntregaEstimada())

                };
                mtU.addRow(fila);
                tUbi.setModel(mtU);
            } else {
                textInfo.setText("Información: Pedido no encontrado, pedidos con ubicacion resgitrada ");
                cargarRegistrosUbi();
            }

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Ingresa el identificador de un pedido",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            textInfo.setText("Información: Pedido no encontrado, pedidos con ubicacion resgitrada ");

            cargarRegistrosUbi();

        }
    }

    private String leeUbiSelec(int ubiS) {
        String valorU = "";
        switch (ubiS) {
            case 1:
                valorU = "en_transito";
                break;

            case 2:
                valorU = "almacen";
                break;
            case 3:
                valorU = "entregado";
                break;
            case 4:
                valorU = "recogido";
                break;
            default:
                valorU = "";
        }
        return valorU;
    }

    private void cargarRegistrosUbi() {
        ubicaciones = cUbicacionP.findUbicacionPedidoEntities();
        int numfilas = mtU.getRowCount();
        for (int i = numfilas - 1; i >= 0; i--) {
            mtU.removeRow(i);
        }
        for (UbicacionPedido u : ubicaciones) {
            Object[] fila = {u.getIdPedido().getIdPedido(),
                u.getIdPedido().getIdCliente().getNombre() + " " + u.getIdPedido().getIdCliente().getApellido(),
                u.getEstadoLogistica(), u.getIdProveedor().getNombre(),
                formato.format(u.getIdPedido().getFechaEntregaEstimada())

            };
            mtU.addRow(fila);
        }
        tUbi.setModel(mtU);
    }

    private UbicacionPedido buscarUbiPedidos(Pedido p) {
        ubicaciones = cUbicacionP.findUbicacionPedidoEntities();
        for (UbicacionPedido u : ubicaciones) {
            if (u.getIdPedido().getIdPedido() == p.getIdPedido()) {
                return u;
            }
        }
        return null;

    }

    private static boolean esEntero(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void guardarFecha() {

        fechaEntre = calendarioSelec.getDate();
        textFecha.setText("Fecha: " + formato.format(fechaEntre));
        calendarioSelec.setEnabled(false);

    }

    public void registrarPedido() throws NonexistentEntityException, Exception {//agrega a detalle
        if (clienteCB.getSelectedIndex() != 0 && productoCB.getSelectedIndex() >= 0 && fechaEla != null && fechaEntre != null && (Integer) cantidadSP.getValue() != 0 && confirmaFecha) {

            //  if (tallaCB.getSelectedIndex() >= 0) {
            Cliente client = clientes.get(clienteCB.getSelectedIndex() - 1);
            Date fechaActual = new Date();//fecha de generacion de pedido actual

            Producto prod = selecPro.getIdProducto();
            BigInteger cant = new BigInteger(cantidadSP.getValue().toString());
            costoProductos = (selecPro.getIdProducto().getPrecio()).multiply(new BigDecimal(cant));

            totalMonto = totalMonto.add(costoProductos);
            System.out.println(costoProductos + " cantidad" + cant + "total:" + totalMonto);

            String msj = "Cliente: " + client.getNombre() + " " + client.getApellido() + " " + productoCB.getSelectedItem().toString() + " Costo: $" + costoProductos;

            int resp = JOptionPane.showConfirmDialog(
                    null,
                    "¿Agregar al pedido: " + msj + "?",
                    "Aviso",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (resp == JOptionPane.YES_OPTION) {
                //verificar si existe talla y color en alamcen
                /*Inventario reg = existenciaInventario(prod, talla, color);
                    if (reg != null) {*/

                //agregar nuevo
                if (NuevoP == null) {

                    Pedido nuevoP = new Pedido();
                    nuevoP.setIdCliente(client);
                    nuevoP.setFechaPedido(fechaActual);
                    nuevoP.setFechaEntregaEstimada(fechaEntre);//FECHA SELECCIONADA EN CALENDARIO
                    nuevoP.setEstado("pendiente");//para nuevo pedido con minuscula

                    cPedido.create(nuevoP);
                    NuevoP = nuevoP;
                    System.out.println("Pedido nuevo registrado" + NuevoP.getIdPedido());

                }
                DetallePedido producto = new DetallePedido();
                producto.setIdPedido(NuevoP);
                producto.setIdProducto(prod);
                producto.setCantidad(cant);
                producto.setColor(inventario_index.get(selecPro).getColor());
                producto.setTalla(Integer.parseInt(selecPro.getTalla()));

                cDetalleP.create(producto);

                textTotal.setText("Total: $" + totalMonto);

                //restar de cantidad en inventario
                selecPro.setCantidadActual(selecPro.getCantidadActual() - cant.intValue());//contenplar productos futuros a retirar

                cInventario.edit(selecPro);

                /* } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "No se encuentra producto en inventario",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }*/
            } else {
                cargarCB();
                System.out.println("Pedido cancelado...");
            }

            /* } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Ingresa una talla para este ipo de producto",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
            }*/
            confirmaFecha = false;

        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Ingresa cadenas validas para los campos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        textPrecio.setText("$0");
    }

    private Inventario existenciaInventario(Producto p, String t, String c) {
        inventario = cInventario.findInventarioEntities();
        for (Inventario i : inventario) {
            if (i.getIdProducto() == p && c.equalsIgnoreCase(i.getColor()) && i.getTalla().equals(t)) {
                return i;
            }
        }
        return null;
    }

    private Pedido buscaPedidoTabla(int id) {
        /*for (Pedido p : pedidos) {
            if (id == p.getIdPedido()) {
                return p;
            }
        }*/
        if (cPedido.findPedido(id) != null) {
            return cPedido.findPedido(id);
        }
        return null;
    }

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
            java.util.logging.Logger.getLogger(InterfazPed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazPed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazPed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazPed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InterfazPed dialog = null;
                try {
                    dialog = new InterfazPed(new javax.swing.JFrame(), true);

                } catch (Exception ex) {
                    Logger.getLogger(InterfazPed.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

                if (dialog != null) {
                    dialog.setLocationRelativeTo(null); // <-- AQUÍ: centra la ventana
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                }
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BactualizarU;
    private javax.swing.JButton Bcancelar;
    private javax.swing.JButton Bcf1;
    private javax.swing.JButton Bcf2;
    private javax.swing.JButton Bconfi;
    private javax.swing.JButton BconsultarU;
    private javax.swing.JTextField TextIdP;
    private javax.swing.JButton bBuscar;
    private com.toedter.calendar.JCalendar calenA;
    private com.toedter.calendar.JCalendar calenE;
    private com.toedter.calendar.JCalendar calendarioSelec;
    private javax.swing.JSpinner cantidadSP;
    private javax.swing.JComboBox<String> clienteCB;
    private javax.swing.JTextArea detallesText;
    private javax.swing.JLabel fA;
    private javax.swing.JLabel fE;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane panelEA;
    private javax.swing.JTabbedPane panelesPedido;
    private javax.swing.JComboBox<String> pedidosUbiCB;
    private javax.swing.JComboBox<String> productoCB;
    private javax.swing.JComboBox<String> proveCB1;
    private javax.swing.JComboBox<String> proveCB2;
    private javax.swing.JTable tPedidos;
    private javax.swing.JTable tUbi;
    private javax.swing.JLabel textFecha;
    private javax.swing.JLabel textFechaAcU;
    private javax.swing.JTextField textIdU;
    private javax.swing.JLabel textInfo;
    private javax.swing.JLabel textPrecio;
    private javax.swing.JLabel textTotal;
    private javax.swing.JLabel txtResult;
    private javax.swing.JComboBox<String> ubiCB;
    private javax.swing.JComboBox<String> ubiCB2;
    // End of variables declaration//GEN-END:variables
}
