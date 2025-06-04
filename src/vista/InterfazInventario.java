/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista;

import control.AdnDatos;
import control.DetallePedidoJpaController;
import control.InventarioJpaController;
import control.PedidoJpaController;
import control.ProductoJpaController;
import control.exceptions.IllegalOrphanException;
import control.exceptions.NonexistentEntityException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.DatosTablaInventario;
import modelo.DatosTablaInventarioBS;
import modelo.DatosTablaPedidoInv;
import modelo.DetallePedido;
import modelo.Inventario;
import modelo.MTablaInventario;
import modelo.MTablaInventarioBS;
import modelo.MTablaProducto;
import modelo.MTablaPedidoInv;
import modelo.Pedido;
import modelo.Producto;

/**
 *
 * @author ericg
 */
public class InterfazInventario extends javax.swing.JDialog {
    private ProductoJpaController cProducto;
    private PedidoJpaController cPedido;
    private InventarioJpaController cInventario;
    private DetallePedidoJpaController cDetallePedido;
    private AdnDatos adn;
    private ArrayList<Producto> datosProductos;
    private MTablaProducto modTabProducto;
    private ArrayList<DatosTablaPedidoInv> datosPedidos;
    private MTablaPedidoInv modTabPedido;
    private ArrayList<DatosTablaInventario> datosInventarios;
    private MTablaInventario modTabInventario;
    private ArrayList<DatosTablaInventarioBS> datosInventariosBS;
    private MTablaInventarioBS modTabInventarioBS;
    private final String SELECTCBM = "Selecciona Filtro";
    private final String SELECTCBM2 = "Sin Filtro";
    private final String SELECTCBM3 = "Precio menor a";
    private final String SELECTCBM4 = "Precio mayor a";
    private final String SELECTCBM5 = "Id. Inventario";
    private final String SELECTCBM6 = "Nombre Producto";
    private final String SELECTCBM7 = "Ubicacion";
    private List<Producto> productos;
    private List<Producto> productos_s;
    private List<Pedido> pedidos;
    private List<Pedido> pedidos_s;
    private List<Inventario> inventarios;
    private List<Inventario> inventarios_s;
    private List<DetallePedido> detallePedidos;
    private List<DetallePedido> detallePedidos_s;
    private boolean confirmacion;
    private int indice;
    
    /**
     * Creates new form InterfazInventario
     */
    public InterfazInventario(java.awt.Frame parent, boolean modal, String rolUsuario) {
        super(parent, modal);        
        initComponents();
        adn = new AdnDatos();
        cProducto = new ProductoJpaController(adn.getEnf());
        productos = cProducto.findProductoEntities();
        cPedido = new PedidoJpaController(adn.getEnf());
        pedidos = cPedido.findPedidoEntities();
        cInventario = new InventarioJpaController(adn.getEnf());
        inventarios = cInventario.findInventarioEntities();
        cDetallePedido = new DetallePedidoJpaController(adn.getEnf());
        detallePedidos = cDetallePedido.findDetallePedidoEntities();
        datosProductos = new ArrayList<>();
        modTabProducto = new MTablaProducto(datosProductos);
        lproductos.setModel(modTabProducto);
        btnBuscar.setEnabled(false);
        cargarProductos();
        if(rolUsuario.equals("Almacenista")) Inicializar();
        
        confirmacion = false;
        
        /*datosPedidos  = new ArrayList<>();
        modTabPedido = new MTablaPedidoInv(datosPedidos);
        lpedidos.setModel(modTabPedido);*/
        
    }
    public void Inicializar(){
        btnEliminar.setEnabled(false);
        btnEliminarPP.setEnabled(false);
        btnEliminarSP.setEnabled(false);
        
    }
    
    private void cargarProductos(){
        cbmColumn.removeAllItems();
        cbmColumn.addItem(SELECTCBM);
        cbmColumn.addItem(SELECTCBM2);
        for(int i = 0; i<modTabProducto.getColumnCount()-1; i++){
            cbmColumn.addItem(modTabProducto.getColumnName(i));
        }
        cbmColumn.addItem(SELECTCBM3);
        cbmColumn.addItem(SELECTCBM4);
        
        productos_s = new ArrayList<>();
        for(Producto pr : productos)
            productos_s.add(pr);
        datosProductos = new ArrayList<>();
        for(Producto pr : productos_s)
            datosProductos.add(pr);
        modTabProducto = new MTablaProducto(datosProductos);
        lproductos.setModel(modTabProducto);
    }
    
    private void cargarPedidos(){
        cbmFiltroP.removeAllItems();
        cbmFiltroP.addItem(SELECTCBM);
        cbmFiltroP.addItem(SELECTCBM2);
        cbmFiltroP.addItem("Identificador Pedido");
        cbmFiltroP.addItem("Cancelado");
        cbmFiltroP.addItem("No recogido");
        cargarDeNuevoPedidos();
    }
    private void cargarDeNuevoPedidos(){
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-dd-MM");
            Date fecha_actual = new Date();
            String f1 = formato.format(fecha_actual);
            Date fecha_ac = formato.parse(f1);
            pedidos_s = new ArrayList<>();
            for(Pedido pd : pedidos)
                if((pd.getEstado().toLowerCase().contains("pendiente") && fecha_ac.after(pd.getFechaEntregaEstimada())) || pd.getEstado().toLowerCase().contains("cancelado"))
                    pedidos_s.add(pd);
            datosPedidos = new ArrayList<>();
            for(Pedido pd : pedidos_s){
                DatosTablaPedidoInv pdd = new DatosTablaPedidoInv(pd);
                datosPedidos.add(pdd);
            }
            modTabPedido = new MTablaPedidoInv(datosPedidos);
            lpedidos.setModel(modTabPedido);
        } catch (ParseException ex) {
                //Error en crear Fecha Actual
        }
    }
    
    private void cargarInventario(){
        cbmFiltroA.removeAllItems();
        cbmFiltroA.addItem(SELECTCBM);
        cbmFiltroA.addItem(SELECTCBM2);
        cbmFiltroA.addItem(SELECTCBM5);
        cbmFiltroA.addItem(SELECTCBM6);
        cbmFiltroA.addItem(SELECTCBM7);
        
        inventarios_s = new ArrayList<>();
        for(Inventario inv: inventarios)
            inventarios_s.add(inv);
        datosInventarios = new ArrayList<>();
        for(Inventario inv: inventarios_s){
            DatosTablaInventario di = new DatosTablaInventario(inv);
            datosInventarios.add(di);
        }
        modTabInventario = new MTablaInventario(datosInventarios);
        linventarios.setModel(modTabInventario);
    }
    
    private void cargarInventarioBS(){
        inventarios_s = new ArrayList<>();
        for(Inventario inv: inventarios)
            inventarios_s.add(inv);
        datosInventariosBS = new ArrayList<>();
        for(Inventario inv: inventarios_s){
            if(inv.getCantidadActual()<=inv.getCantidadMinima()){
                DatosTablaInventarioBS di = new DatosTablaInventarioBS(inv);
                datosInventariosBS.add(di);
            }
        }
        modTabInventarioBS = new MTablaInventarioBS(datosInventariosBS);
        linventariosBS.setModel(modTabInventarioBS);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        EditarPedido = new javax.swing.JDialog();
        jLabel14 = new javax.swing.JLabel();
        txtClientePF = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtPedidoPF = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        JCalendarF = new com.toedter.calendar.JCalendar();
        AgregarStock = new javax.swing.JDialog();
        jLabel18 = new javax.swing.JLabel();
        txtNombreAS = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtAgregarAS = new javax.swing.JTextField();
        btnCancelarAgregarStock = new javax.swing.JButton();
        btnAgregarStock = new javax.swing.JButton();
        txtStockAP = new javax.swing.JTextField();
        AgregarProducto = new javax.swing.JDialog();
        jLabel22 = new javax.swing.JLabel();
        txtCantAAP = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtProductoAP = new javax.swing.JTextField();
        btnCancelarAgregarProductoInv = new javax.swing.JButton();
        btnAgregarProductoInv = new javax.swing.JButton();
        txtNombrePAP = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        cbmTallaAP = new javax.swing.JComboBox<>();
        cbmColorAP = new javax.swing.JComboBox<>();
        txtCantMAP = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtObservacionesAP = new javax.swing.JTextArea();
        txtUbicacionAPP = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        EditarProducto = new javax.swing.JDialog();
        jLabel26 = new javax.swing.JLabel();
        txtPrecioEP = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtProductoEP = new javax.swing.JTextField();
        btnCancelarEP = new javax.swing.JButton();
        btnModigicarEP = new javax.swing.JButton();
        txtTallaEP = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtColorEP = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtDescripcionEP = new javax.swing.JTextArea();
        Confirmacion = new javax.swing.JDialog();
        jLabel33 = new javax.swing.JLabel();
        btnCancelarC = new javax.swing.JButton();
        btnAceptarC = new javax.swing.JButton();
        tabPanel = new javax.swing.JTabbedPane();
        menuInventario = new javax.swing.JPanel();
        scrollProductos = new javax.swing.JScrollPane();
        lproductos = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        cbmColumn = new javax.swing.JComboBox<>();
        btnBuscar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        menuProducto = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        txtTalla = new javax.swing.JTextField();
        txtColor = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        btnR = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        menuPedido = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lpedidos = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        txtBuscarP = new javax.swing.JTextField();
        cbmFiltroP = new javax.swing.JComboBox<>();
        btnBuscarPP = new javax.swing.JButton();
        btnPosponer = new javax.swing.JButton();
        btnEliminarPP = new javax.swing.JButton();
        menuStock = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        linventarios = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        linventariosBS = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtBuscarA = new javax.swing.JTextField();
        btnBuscarSP = new javax.swing.JButton();
        cbmFiltroA = new javax.swing.JComboBox<>();
        btnAumentarSP = new javax.swing.JButton();
        txtAgregarSP = new javax.swing.JButton();
        btnEliminarSP = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        EditarPedido.setModal(true);
        EditarPedido.setPreferredSize(new java.awt.Dimension(430, 350));
        EditarPedido.setResizable(false);
        EditarPedido.setSize(new java.awt.Dimension(560, 460));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel14.setText("Identificador Pedido:");

        txtClientePF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClientePFActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel15.setText("Posponer Fecha");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel16.setText("Modificar Fecha:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel17.setText("Nombre Cliente:");

        jButton3.setText("Cancelar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Modificar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout EditarPedidoLayout = new javax.swing.GroupLayout(EditarPedido.getContentPane());
        EditarPedido.getContentPane().setLayout(EditarPedidoLayout);
        EditarPedidoLayout.setHorizontalGroup(
            EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditarPedidoLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3)
                .addGroup(EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtClientePF, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPedidoPF, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(JCalendarF, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
            .addGroup(EditarPedidoLayout.createSequentialGroup()
                .addGap(150, 150, 150)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(150, 150, 150))
        );
        EditarPedidoLayout.setVerticalGroup(
            EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditarPedidoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel15)
                .addGap(29, 29, 29)
                .addGroup(EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtPedidoPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtClientePF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EditarPedidoLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(EditarPedidoLayout.createSequentialGroup()
                        .addComponent(JCalendarF, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addGroup(EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton4))
                        .addGap(29, 29, 29))))
        );

        AgregarStock.setModal(true);
        AgregarStock.setResizable(false);
        AgregarStock.setSize(new java.awt.Dimension(402, 268));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel18.setText("Identificador Inv. :");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel19.setText("Agregar Stock");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel20.setText("Stock a aumentar:");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel21.setText("Nombre Producto:");

        btnCancelarAgregarStock.setText("Cancelar");
        btnCancelarAgregarStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarAgregarStockActionPerformed(evt);
            }
        });

        btnAgregarStock.setText("Agregar");
        btnAgregarStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarStockActionPerformed(evt);
            }
        });

        txtStockAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStockAPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AgregarStockLayout = new javax.swing.GroupLayout(AgregarStock.getContentPane());
        AgregarStock.getContentPane().setLayout(AgregarStockLayout);
        AgregarStockLayout.setHorizontalGroup(
            AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AgregarStockLayout.createSequentialGroup()
                .addContainerGap(117, Short.MAX_VALUE)
                .addComponent(jLabel19)
                .addGap(117, 117, 117))
            .addGroup(AgregarStockLayout.createSequentialGroup()
                .addGroup(AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AgregarStockLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(AgregarStockLayout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(btnCancelarAgregarStock)))
                .addGroup(AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AgregarStockLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombreAS, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAgregarAS, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStockAP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AgregarStockLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgregarStock)
                        .addGap(100, 100, 100))))
        );
        AgregarStockLayout.setVerticalGroup(
            AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AgregarStockLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel19)
                .addGap(30, 30, 30)
                .addGroup(AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtAgregarAS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtNombreAS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtStockAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelarAgregarStock)
                    .addComponent(btnAgregarStock))
                .addGap(15, 15, 15))
        );

        AgregarProducto.setModal(true);
        AgregarProducto.setResizable(false);
        AgregarProducto.setSize(new java.awt.Dimension(426, 528));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel22.setText("Identificador Producto:");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel23.setText("Agregar Producto");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel24.setText("Cantidad Actual:");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel25.setText("Ubicacion:");

        txtProductoAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProductoAPActionPerformed(evt);
            }
        });

        btnCancelarAgregarProductoInv.setText("Cancelar");
        btnCancelarAgregarProductoInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarAgregarProductoInvActionPerformed(evt);
            }
        });

        btnAgregarProductoInv.setText("Agregar");
        btnAgregarProductoInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProductoInvActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel30.setText("Cantidad Minima:");

        jLabel34.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel34.setText("Color:");

        jLabel35.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel35.setText("Talla:");

        cbmTallaAP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbmColorAP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel36.setText("Observaciones:");

        txtObservacionesAP.setColumns(20);
        txtObservacionesAP.setRows(5);
        jScrollPane6.setViewportView(txtObservacionesAP);

        txtUbicacionAPP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUbicacionAPPActionPerformed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel37.setText("Nombre Producto:");

        javax.swing.GroupLayout AgregarProductoLayout = new javax.swing.GroupLayout(AgregarProducto.getContentPane());
        AgregarProducto.getContentPane().setLayout(AgregarProductoLayout);
        AgregarProductoLayout.setHorizontalGroup(
            AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AgregarProductoLayout.createSequentialGroup()
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(AgregarProductoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtNombrePAP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(127, 127, 127)
                            .addComponent(jLabel23))
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(43, 43, 43)
                            .addComponent(jLabel22)
                            .addGap(18, 18, 18)
                            .addComponent(txtProductoAP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(43, 43, 43)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(cbmTallaAP, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(43, 43, 43)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(cbmColorAP, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(43, 43, 43)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtUbicacionAPP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(43, 43, 43)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtCantAAP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(43, 43, 43)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtCantMAP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(43, 43, 43)
                            .addComponent(jLabel36)
                            .addGap(18, 18, 18)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(AgregarProductoLayout.createSequentialGroup()
                            .addGap(100, 100, 100)
                            .addComponent(btnCancelarAgregarProductoInv)
                            .addGap(74, 74, 74)
                            .addComponent(btnAgregarProductoInv))))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        AgregarProductoLayout.setVerticalGroup(
            AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AgregarProductoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel23)
                .addGap(24, 24, 24)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(txtProductoAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombrePAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(11, 11, 11)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addComponent(cbmTallaAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34)
                    .addComponent(cbmColorAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(txtUbicacionAPP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(txtCantAAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(txtCantMAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AgregarProductoLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel36))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCancelarAgregarProductoInv)
                    .addComponent(btnAgregarProductoInv))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        EditarProducto.setModal(true);
        EditarProducto.setResizable(false);
        EditarProducto.setSize(new java.awt.Dimension(403, 428));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel26.setText("Producto:");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel27.setText("Editar Producto");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel28.setText("Color:");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel29.setText("Descripcion:");

        btnCancelarEP.setText("Cancelar");
        btnCancelarEP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarEPActionPerformed(evt);
            }
        });

        btnModigicarEP.setText("Modificar");
        btnModigicarEP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModigicarEPActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel31.setText("Talla:");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel32.setText("Precio:");

        txtDescripcionEP.setColumns(20);
        txtDescripcionEP.setRows(5);
        jScrollPane5.setViewportView(txtDescripcionEP);

        javax.swing.GroupLayout EditarProductoLayout = new javax.swing.GroupLayout(EditarProducto.getContentPane());
        EditarProducto.getContentPane().setLayout(EditarProductoLayout);
        EditarProductoLayout.setHorizontalGroup(
            EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditarProductoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditarProductoLayout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(109, 109, 109))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EditarProductoLayout.createSequentialGroup()
                        .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductoEP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTallaEP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtColorEP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecioEP, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(44, 44, 44))))
            .addGroup(EditarProductoLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EditarProductoLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(btnCancelarEP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(btnModigicarEP)
                        .addGap(100, 100, 100))
                    .addGroup(EditarProductoLayout.createSequentialGroup()
                        .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel28)
                            .addComponent(jLabel32)
                            .addComponent(jLabel31)
                            .addComponent(jLabel29)
                            .addComponent(jLabel26))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        EditarProductoLayout.setVerticalGroup(
            EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EditarProductoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel27)
                .addGap(30, 30, 30)
                .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtProductoEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(EditarProductoLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel29)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTallaEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(11, 11, 11)
                .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtColorEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addGap(11, 11, 11)
                .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecioEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addGap(25, 25, 25)
                .addGroup(EditarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelarEP)
                    .addComponent(btnModigicarEP))
                .addGap(15, 15, 15))
        );

        Confirmacion.setModal(true);
        Confirmacion.setResizable(false);
        Confirmacion.setSize(new java.awt.Dimension(414, 138));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel33.setText("¿Desea eliminar el elemento seleccionado?");

        btnCancelarC.setText("Cancelar");
        btnCancelarC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarCActionPerformed(evt);
            }
        });

        btnAceptarC.setText("Aceptar");
        btnAceptarC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ConfirmacionLayout = new javax.swing.GroupLayout(Confirmacion.getContentPane());
        Confirmacion.getContentPane().setLayout(ConfirmacionLayout);
        ConfirmacionLayout.setHorizontalGroup(
            ConfirmacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ConfirmacionLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel33)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ConfirmacionLayout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(btnCancelarC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptarC)
                .addGap(125, 125, 125))
        );
        ConfirmacionLayout.setVerticalGroup(
            ConfirmacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ConfirmacionLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel33)
                .addGap(18, 18, 18)
                .addGroup(ConfirmacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAceptarC)
                    .addComponent(btnCancelarC))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        tabPanel.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPanelStateChanged(evt);
            }
        });

        lproductos.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollProductos.setViewportView(lproductos);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel8.setText("Dato a buscar:");

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });

        cbmColumn.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbmColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmColumnActionPerformed(evt);
            }
        });

        btnBuscar.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuInventarioLayout = new javax.swing.GroupLayout(menuInventario);
        menuInventario.setLayout(menuInventarioLayout);
        menuInventarioLayout.setHorizontalGroup(
            menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuInventarioLayout.createSequentialGroup()
                .addGroup(menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuInventarioLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(scrollProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuInventarioLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(menuInventarioLayout.createSequentialGroup()
                                .addComponent(btnEditar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEliminar))
                            .addGroup(menuInventarioLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cbmColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnBuscar)))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        menuInventarioLayout.setVerticalGroup(
            menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuInventarioLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminar)
                    .addComponent(btnEditar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(cbmColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        tabPanel.addTab("Buscar", menuInventario);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel1.setText("Nombre:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel2.setText("Descripcion:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setText("Talla:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel4.setText("Color:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel5.setText("Precio:");

        txtDescripcion.setColumns(20);
        txtDescripcion.setRows(5);
        jScrollPane1.setViewportView(txtDescripcion);

        txtTalla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTallaActionPerformed(evt);
            }
        });

        btnR.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        btnR.setText("Registrar");
        btnR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setText("Registrar Producto");

        javax.swing.GroupLayout menuProductoLayout = new javax.swing.GroupLayout(menuProducto);
        menuProducto.setLayout(menuProductoLayout);
        menuProductoLayout.setHorizontalGroup(
            menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuProductoLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuProductoLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuProductoLayout.createSequentialGroup()
                        .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(40, 40, 40)
                        .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTalla, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(menuProductoLayout.createSequentialGroup()
                                .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(btnR))))
                    .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel6)
                        .addGroup(menuProductoLayout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(27, 27, 27)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(93, Short.MAX_VALUE))
        );
        menuProductoLayout.setVerticalGroup(
            menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuProductoLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuProductoLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuProductoLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel2)))
                .addGap(14, 14, 14)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTalla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnR))
                .addGap(6, 6, 6)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        tabPanel.addTab("Registrar", menuProducto);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setText("Pedidos Pendientes");

        lpedidos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(lpedidos);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel10.setText("Dato a buscar:");

        cbmFiltroP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbmFiltroP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmFiltroPActionPerformed(evt);
            }
        });

        btnBuscarPP.setText("Buscar");
        btnBuscarPP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPPActionPerformed(evt);
            }
        });

        btnPosponer.setText("Posponer");
        btnPosponer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPosponerActionPerformed(evt);
            }
        });

        btnEliminarPP.setText("Eliminar");
        btnEliminarPP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuPedidoLayout = new javax.swing.GroupLayout(menuPedido);
        menuPedido.setLayout(menuPedidoLayout);
        menuPedidoLayout.setHorizontalGroup(
            menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPedidoLayout.createSequentialGroup()
                .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuPedidoLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(menuPedidoLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPosponer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminarPP))
                            .addGroup(menuPedidoLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarP, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbmFiltroP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscarPP))))
                    .addGroup(menuPedidoLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        menuPedidoLayout.setVerticalGroup(
            menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPedidoLayout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPedidoLayout.createSequentialGroup()
                        .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPosponer)
                            .addComponent(btnEliminarPP))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPedidoLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(27, 27, 27)))
                .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtBuscarP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbmFiltroP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarPP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        tabPanel.addTab("Pedidos", menuPedido);

        linventarios.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(linventarios);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel11.setText("Stock Producto");

        linventariosBS.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(linventariosBS);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel12.setText("Productos Bajo Stock:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel13.setText("Dato a buscar:");

        btnBuscarSP.setText("Buscar");
        btnBuscarSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarSPActionPerformed(evt);
            }
        });

        cbmFiltroA.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbmFiltroA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmFiltroAActionPerformed(evt);
            }
        });

        btnAumentarSP.setText("Aumentar Stock");
        btnAumentarSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAumentarSPActionPerformed(evt);
            }
        });

        txtAgregarSP.setText("Agregar Producto");
        txtAgregarSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAgregarSPActionPerformed(evt);
            }
        });

        btnEliminarSP.setText("Eliminar");
        btnEliminarSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarSPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuStockLayout = new javax.swing.GroupLayout(menuStock);
        menuStock.setLayout(menuStockLayout);
        menuStockLayout.setHorizontalGroup(
            menuStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuStockLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuStockLayout.createSequentialGroup()
                .addGroup(menuStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuStockLayout.createSequentialGroup()
                        .addGroup(menuStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(menuStockLayout.createSequentialGroup()
                                .addContainerGap(25, Short.MAX_VALUE)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarA, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbmFiltroA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBuscarSP))
                            .addGroup(menuStockLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnEliminarSP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAgregarSP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnAumentarSP)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(menuStockLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(menuStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuStockLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(12, 12, 12)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        menuStockLayout.setVerticalGroup(
            menuStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuStockLayout.createSequentialGroup()
                .addGroup(menuStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuStockLayout.createSequentialGroup()
                        .addContainerGap(14, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuStockLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(menuStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAumentarSP)
                            .addComponent(txtAgregarSP)
                            .addComponent(btnEliminarSP))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(menuStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtBuscarA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscarSP)
                            .addComponent(cbmFiltroA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        tabPanel.addTab("Almacen", menuStock);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel7.setText("Inventario");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
                .addContainerGap(13, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(273, 273, 273)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTallaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTallaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTallaActionPerformed

    private void btnRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRActionPerformed
        Producto p = new Producto();
        if(!txtNombre.getText().trim().isEmpty() && !txtDescripcion.getText().trim().isEmpty() && !txtTalla.getText().trim().isEmpty()
                && !txtColor.getText().trim().isEmpty()){
            p.setNombre((String)txtNombre.getText());
            p.setDescripcion(txtDescripcion.getText());
            p.setTalla((String)txtTalla.getText());
            p.setColor((String)txtColor.getText());
            try{
                p.setPrecio(new BigDecimal(txtPrecio.getText()));
                cProducto.create(p);
                productos = cProducto.findProductoEntities();
                /*productos_s = new ArrayList<>();
                for(Producto pr:productos)
                    productos_s.add(pr);
                datosProductos = new ArrayList<>();
                for(Producto pr:productos_s)
                    datosProductos.add(pr);
                MTablaProducto modTabp = new MTablaProducto(datosProductos);
                lproductos.setModel(modTabp);*/
                txtNombre.setText("");
                txtDescripcion.setText("");
                txtTalla.setText("");
                txtColor.setText("");
                txtPrecio.setText("");
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,"Formato de Precio invalido, si el formato fuera correcto presione nuevamente");
            }
        }
        else
            JOptionPane.showMessageDialog(this,"Exiten campos vacios");
    }//GEN-LAST:event_btnRActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void cbmColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmColumnActionPerformed
        if(cbmColumn.getSelectedItem()!= SELECTCBM)
            btnBuscar.setEnabled(true);
        else
            btnBuscar.setEnabled(false);
    }//GEN-LAST:event_cbmColumnActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        int opc = cbmColumn.getSelectedIndex();
        lproductos.setModel(new MTablaProducto(new ArrayList<>()));
        try{
            productos_s = new ArrayList<>();
            switch(opc){
                case 2:
                    for(Producto pr : productos){
                        int idp = Integer.parseInt(txtBuscar.getText());
                        if(pr.getIdProducto()== idp)
                            productos_s.add(pr);
                    }
                    break;
                case 3:
                    for(Producto pr : productos)
                        if(pr.getNombre().toLowerCase().contains(txtBuscar.getText().toLowerCase()))
                            productos_s.add(pr);
                    break;
                case 4:
                    for(Producto pr : productos)
                        if(pr.getDescripcion().toLowerCase().contains(txtBuscar.getText().toLowerCase()))
                            productos_s.add(pr);
                    break;
                case 5:
                    for(Producto pr : productos)
                        if(pr.getTalla().toLowerCase().contains(txtBuscar.getText().toLowerCase()))
                            productos_s.add(pr);
                    break;
                case 6:
                    for(Producto pr : productos)
                        if(pr.getColor().toLowerCase().contains(txtBuscar.getText().toLowerCase()))
                            productos_s.add(pr);
                    break;
                case 7:
                    BigDecimal bd = new BigDecimal(txtBuscar.getText());
                    for(Producto pr : productos)
                        if(bd.compareTo(pr.getPrecio())>=0)
                            productos_s.add(pr);
                    break;
                case 8:
                    BigDecimal bdM = new BigDecimal(txtBuscar.getText());
                    for(Producto pr : productos)
                        if(bdM.compareTo(pr.getPrecio())<=0)
                            productos_s.add(pr);
                    break;
                default:
                    for(Producto pr : productos)
                        productos_s.add(pr);
                    break;
            }
            datosProductos = new ArrayList<>();
            for(Producto ps : productos_s)
                datosProductos.add(ps);
            modTabProducto  = new MTablaProducto(datosProductos);
            lproductos.setModel(modTabProducto);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this,"Formato de Busqueda invalido");
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void cbmFiltroPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmFiltroPActionPerformed
        if(cbmFiltroP.getSelectedItem()!= SELECTCBM)
            btnBuscarPP.setEnabled(true);
        else
            btnBuscarPP.setEnabled(false);
        if(cbmFiltroP.getSelectedItem()== "Identificador Pedido")
            txtBuscarP.setEnabled(true);
        else
            txtBuscarP.setEnabled(false);
    }//GEN-LAST:event_cbmFiltroPActionPerformed

    private void btnBuscarPPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarPPActionPerformed
        int opc2 = cbmFiltroP.getSelectedIndex();
        lpedidos.setModel(new MTablaPedidoInv(new ArrayList<>()));
        try{
            pedidos_s = new ArrayList<>();
            switch(opc2){
                case 2:
                    for(Pedido pd : pedidos){
                        int idp = Integer.parseInt(txtBuscarP.getText());
                        if(pd.getIdPedido()== idp)
                            pedidos_s.add(pd);
                    }
                    break;
                case 3:
                    for(Pedido pd : pedidos)
                        if(pd.getEstado().toLowerCase().contains("cancelado"))
                            pedidos_s.add(pd);
                    break;
                case 4:
                    SimpleDateFormat formato = new SimpleDateFormat("yyyy-dd-MM");
                    Date fecha_actual = new Date();
                    String f1 = formato.format(fecha_actual);
                    Date fecha_ac = formato.parse(f1);
                    for(Pedido pd : pedidos)
                        if(fecha_ac.after(pd.getFechaEntregaEstimada()))
                            pedidos_s.add(pd);
                    break;
                default:
                    cargarDeNuevoPedidos();
                    break;
            }
            datosPedidos = new ArrayList<>();
            for(Pedido ps : pedidos_s){
                DatosTablaPedidoInv psd = new DatosTablaPedidoInv(ps);
                datosPedidos.add(psd);
            }
            modTabPedido  = new MTablaPedidoInv(datosPedidos);
            lpedidos.setModel(modTabPedido);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this,"Formato de Busqueda invalido");
        }
    }//GEN-LAST:event_btnBuscarPPActionPerformed

    private void tabPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPanelStateChanged
        int ind = tabPanel.getSelectedIndex();
        if(ind == 2)
            cargarPedidos();
        if(ind == 3){
            cargarInventario();
            cargarInventarioBS();
        }
    }//GEN-LAST:event_tabPanelStateChanged

    private void cbmFiltroAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmFiltroAActionPerformed
        if(cbmFiltroA.getSelectedItem()!= SELECTCBM)
            btnBuscarSP.setEnabled(true);
        else
            btnBuscarSP.setEnabled(false);
    }//GEN-LAST:event_cbmFiltroAActionPerformed

    private void btnBuscarSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarSPActionPerformed
        int opc3= cbmFiltroA.getSelectedIndex();
        linventarios.setModel(new MTablaInventario(new ArrayList<>()));
        try{
            inventarios_s = new ArrayList<>();
            switch(opc3){
                case 2:
                    for(Inventario iv : inventarios){
                        int idi = Integer.parseInt(txtBuscarA.getText());
                        if(iv.getIdInventario()== idi)
                            inventarios_s.add(iv);
                    }
                    break;
                case 3:
                    for(Inventario iv : inventarios)
                        if(iv.getIdProducto().getNombre().toLowerCase().contains(txtBuscarA.getText().toLowerCase()))
                            inventarios_s.add(iv);
                    break;
                case 4:
                    for(Inventario iv : inventarios)
                        if(iv.getIdProducto().getDescripcion().toLowerCase().contains(txtBuscarA.getText().toLowerCase()))
                            inventarios_s.add(iv);
                    break;
                default:
                    for(Inventario iv : inventarios)
                        inventarios_s.add(iv);
                    break;
            }
            
            datosInventarios = new ArrayList<>();
            for(Inventario ps : inventarios_s){
                DatosTablaInventario psi = new DatosTablaInventario(ps);
                datosInventarios.add(psi);
            }
            modTabInventario  = new MTablaInventario(datosInventarios);
            linventarios.setModel(modTabInventario);
            cargarInventarioBS();
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this,"Formato de Busqueda invalido");
        }
    }//GEN-LAST:event_btnBuscarSPActionPerformed

    private void btnAumentarSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAumentarSPActionPerformed
        try{
        Inventario pa = modTabInventario.getInventario(linventarios.getSelectedRow());
        txtAgregarAS.setText(Integer.toString(pa.getIdInventario()));
        txtNombreAS.setText(pa.getIdProducto().getNombre());
        txtAgregarAS.setEnabled(false);
        txtNombreAS.setEnabled(false);
        AgregarStock.setVisible(true);
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Seleccione una fila de la tabla");
        }
    }//GEN-LAST:event_btnAumentarSPActionPerformed

    private void txtAgregarSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAgregarSPActionPerformed
        //Producto pa = modTabInventario.getInventario(linventarios.getSelectedRow()).getIdProducto();
        //txtProductoAP.setText(Integer.toString(pa.getIdProducto()));
        txtProductoAP.setEnabled(true);
        txtNombrePAP.setText("");
        txtUbicacionAPP.setText("");
        txtCantAAP.setText("");
        txtCantMAP.setText("");
        txtObservacionesAP.setText("");
        cbmTallaAP.removeAllItems();
        cbmColorAP.removeAllItems();
        AgregarProducto.setVisible(true);
        /*String talla = pa.getTalla();
        String tallas []= talla.split(",");
        for(String tal:tallas)
            cbmTallaAP.addItem(tal);
        String color = pa.getColor();
        String colores []= color.split(",");
        for(String col:colores)
            cbmColorAP.addItem(col);
        AgregarProducto.setVisible(true);*/
    }//GEN-LAST:event_txtAgregarSPActionPerformed

    private void btnEliminarPPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPPActionPerformed
        try{
        Pedido pe = modTabPedido.getPedido(lpedidos.getSelectedRow());
        Confirmacion.setVisible(true);
        if(confirmacion){
            int id_e = pe.getIdPedido();
            for(Pedido pd: pedidos)
                if(pd.getIdPedido()==id_e){
                    detallePedidos = cDetallePedido.findDetallePedidoEntities();
                    for(DetallePedido dp: detallePedidos)
                        if(dp.getIdPedido().getIdPedido()==pd.getIdPedido())
                            detallePedidos_s.add(dp);
                    inventarios = cInventario.findInventarioEntities();
                    for(DetallePedido dp:detallePedidos_s)
                        for(Inventario ivn: inventarios)
                            if(dp.getIdProducto().getIdProducto()==ivn.getIdProducto().getIdProducto() && dp.getColor().equals(ivn.getColor())
                                    && dp.getTalla()==Integer.parseInt(ivn.getTalla())){
                                ivn.setCantidadActual(ivn.getCantidadActual()+dp.getCantidad().intValue());
                                cInventario.edit(cInventario.findInventario(ivn.getIdInventario()));
                                cDetallePedido.destroy(dp.getIdpedidoProducto());
                                break;
                            }
                                
                    try {
                         cPedido.destroy(id_e);
                        break;
                    } catch (IllegalOrphanException ex) {
                        Logger.getLogger("Hola");
                    } catch (NonexistentEntityException ex) {
                        Logger.getLogger("Hola 2");
                    }
                }
            pedidos = cPedido.findPedidoEntities();
            pedidos_s = new ArrayList<>();
            for(Pedido pd:pedidos)
                pedidos_s.add(pd);
        }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Seleccione una fila de la tabla");
        }
    }//GEN-LAST:event_btnEliminarPPActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        try{
        Producto pe = modTabProducto.getProducto(lproductos.getSelectedRow());
        int r=0;
        inventarios = cInventario.findInventarioEntities();
        for(Inventario p:inventarios)
            if(p.getIdProducto().getIdProducto()==pe.getIdProducto())
                r++;
        if(!(r>0)){
        Confirmacion.setVisible(true);
        if(confirmacion){
            int id_e = pe.getIdProducto();
            //for(Producto pr: productos)
                //if(pr.getIdProducto()==id_e){
                    //try {
                        cProducto.destroy(cProducto.findProducto(id_e).getIdProducto());
                      //  break;
                    //} catch (IllegalOrphanException ex) {
                      //  Logger.getLogger("Hola");
                    //} catch (NonexistentEntityException ex) {
                      //  Logger.getLogger("Hola 2");
                    //}
                //}
            productos = cProducto.findProductoEntities();
            productos_s = new ArrayList<>();
            for(Producto pr:productos)
                productos_s.add(pr);
        }
        }
        else
            JOptionPane.showMessageDialog(this,"Existe una referencia del producto en inventario, elimine primero en inventario");
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Seleccione una fila de la tabla");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnEliminarSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarSPActionPerformed
        try{
        Inventario invs = modTabInventario.getInventario(linventarios.getSelectedRow());
        //try{
        Confirmacion.setVisible(true);
        if(confirmacion){
            int id_e = invs.getIdInventario();
            for(Inventario ivp: inventarios)
                if(ivp.getIdInventario()==id_e)
                    try {
                         cInventario.destroy(id_e);
                    } catch (NonexistentEntityException ex) {
                        Logger.getLogger("Hola 2");
                    }
            inventarios = new ArrayList<>();
            inventarios = cInventario.findInventarioEntities();
            inventarios_s = new ArrayList<>();
            for(Inventario iv:inventarios)
                inventarios_s.add(iv);
        }
        //}catch(Exception e){
            
        //}
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Seleccione una fila de la tabla");
        }
    }//GEN-LAST:event_btnEliminarSPActionPerformed

    private void btnAceptarCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarCActionPerformed
        confirmacion=true;
        Confirmacion.dispose();
    }//GEN-LAST:event_btnAceptarCActionPerformed

    private void btnCancelarCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarCActionPerformed
        confirmacion=false;
        Confirmacion.dispose();
    }//GEN-LAST:event_btnCancelarCActionPerformed

    private void btnCancelarAgregarProductoInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarAgregarProductoInvActionPerformed
        AgregarProducto.dispose();
    }//GEN-LAST:event_btnCancelarAgregarProductoInvActionPerformed

    private void btnAgregarProductoInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductoInvActionPerformed
        Inventario inv = new Inventario();
        if(!txtProductoAP.getText().trim().isEmpty() && !txtProductoAP.getText().trim().isEmpty() && !txtUbicacionAPP.getText().trim().isEmpty() && !txtCantAAP.getText().trim().isEmpty()
                && !txtCantMAP.getText().trim().isEmpty() && !txtObservacionesAP.getText().trim().isEmpty()){
        int id_a=Integer.parseInt(txtProductoAP.getText());
        for(Producto pr:productos)
            if(pr.getIdProducto()==id_a){
                inv.setIdProducto(pr);
                inv.setUbicacion(txtUbicacionAPP.getText());
                try{
                    inv.setCantidadActual(Integer.parseInt(txtCantAAP.getText()));
                    inv.setCantidadMinima(Integer.parseInt(txtCantMAP.getText()));
                    inv.setTalla(String.valueOf(cbmTallaAP.getSelectedItem()));
                    inv.setColor(String.valueOf(cbmColorAP.getSelectedItem()));
                    inv.setObservaciones(txtObservacionesAP.getText());
                    cInventario.create(inv);
                    inventarios = cInventario.findInventarioEntities();
                    inventarios_s = new ArrayList<>();
                    for(Inventario ipv:inventarios)
                        inventarios_s.add(ipv);
                    AgregarProducto.dispose();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(this,"Formato de Cantidades invalido, si el formato fuera correcto presione nuevamente");
                }
            }
        }
        else
            JOptionPane.showMessageDialog(this,"Existen campos vacios");
    }//GEN-LAST:event_btnAgregarProductoInvActionPerformed

    private void txtProductoAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductoAPActionPerformed
        int id_pa = Integer.parseInt(txtProductoAP.getText());
        int conf =0;
        for(Producto iv_ap:productos)
            if(iv_ap.getIdProducto()==id_pa){
                txtNombrePAP.setText(iv_ap.getNombre());
                String talla = iv_ap.getTalla().trim();
                String tallas []= talla.split(",");
                for(String tal:tallas)
                    cbmTallaAP.addItem(tal);
                String color = iv_ap.getColor().trim();
                String colores []= color.split(",");
                for(String col:colores)
                    cbmColorAP.addItem(col);
                conf++;
                txtProductoAP.setEnabled(false);
                txtNombrePAP.setEnabled(false);
                break;
            }
        if(conf==0)
            JOptionPane.showMessageDialog(this,"No existe un producto con ese Identificador");
    }//GEN-LAST:event_txtProductoAPActionPerformed

    private void btnAgregarStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarStockActionPerformed
        Inventario pa = modTabInventario.getInventario(linventarios.getSelectedRow());
        if(!txtStockAP.getText().trim().isEmpty())
            try{
                if(!((Integer.parseInt(txtStockAP.getText())<=0))){
                    //for(Inventario pr: inventarios)
                        //if(pr.getIdProducto().getIdProducto()==pa.getIdProducto()){
                            pa.setCantidadActual(pa.getCantidadActual()+Integer.parseInt(txtStockAP.getText()));
                            try{
                                cInventario.edit(cInventario.findInventario(pa.getIdInventario()));
                            }
                            catch(Exception e){
                                cInventario.edit(cInventario.findInventario(pa.getIdInventario()));
                            }
                            JOptionPane.showMessageDialog(this,"Stock aumentado");
                            AgregarStock.dispose();
                }
                else
                    JOptionPane.showMessageDialog(this,"La cantidad a agregar es negativo o igual a cero");
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,"Formato de Cantidades invalido");
            }
        else
            JOptionPane.showMessageDialog(this,"No aumento");
    }//GEN-LAST:event_btnAgregarStockActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        try{
        Producto pa = modTabProducto.getProducto(lproductos.getSelectedRow());
        
        txtProductoEP.setText(pa.getNombre());
        txtDescripcionEP.setText(pa.getDescripcion());
        txtTallaEP.setText(pa.getTalla());
        txtColorEP.setText(pa.getColor());
        txtPrecioEP.setText(pa.getPrecio().toString());
        EditarProducto.setVisible(true);
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Seleccione una fila de la tabla");
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        EditarPedido.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try{
            Pedido pa= modTabPedido.getPedido(lpedidos.getSelectedRow());
            Date f = JCalendarF.getDate();
            pa.setFechaEntregaEstimada(f);
            try{
                cPedido.edit(cPedido.findPedido(pa.getIdPedido()));
            }catch(Exception e){
                cPedido.edit(cPedido.findPedido(pa.getIdPedido()));
            }
            EditarPedido.dispose();
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"No se ha seleccionado Fecha");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnCancelarAgregarStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarAgregarStockActionPerformed
        AgregarStock.dispose();
    }//GEN-LAST:event_btnCancelarAgregarStockActionPerformed

    private void btnCancelarEPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarEPActionPerformed
        EditarProducto.dispose();
    }//GEN-LAST:event_btnCancelarEPActionPerformed

    private void btnModigicarEPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModigicarEPActionPerformed
        Producto pa = modTabProducto.getProducto(lproductos.getSelectedRow());
        if(!txtProductoEP.getText().trim().isEmpty() && !txtDescripcionEP.getText().trim().isEmpty() && !txtTallaEP.getText().trim().isEmpty() && !txtColorEP.getText().trim().isEmpty()
                && !txtPrecioEP.getText().trim().isEmpty())
        try{
            BigDecimal num1=new BigDecimal(txtPrecioEP.getText());
            int re =num1.compareTo(new BigDecimal(0));
            if(!(re<=0)){
                pa.setNombre(txtProductoEP.getText());
                pa.setDescripcion(txtDescripcionEP.getText());
                pa.setTalla(txtTallaEP.getText());
                pa.setColor(txtColorEP.getText());
                pa.setPrecio(num1);
                try{
                    cProducto.edit(cProducto.findProducto(pa.getIdProducto()));
                }catch(Exception e){
                    cProducto.edit(cProducto.findProducto(pa.getIdProducto()));
                }
                EditarProducto.dispose();
            }
        else
            JOptionPane.showMessageDialog(this,"El precio no puede ser menor o igual a 0");
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Formato de Precio invalido");
        }
        else
            JOptionPane.showMessageDialog(this,"Existen campos vacios");
    }//GEN-LAST:event_btnModigicarEPActionPerformed

    private void txtStockAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockAPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockAPActionPerformed

    private void txtUbicacionAPPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUbicacionAPPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUbicacionAPPActionPerformed

    private void btnPosponerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPosponerActionPerformed
        try{
        Pedido pa= modTabPedido.getPedido(lpedidos.getSelectedRow());
        if(!pa.getEstado().trim().toLowerCase().equals("cancelado")){
            txtPedidoPF.setText(Integer.toString(pa.getIdPedido()));
            txtClientePF.setText(pa.getIdCliente().getNombre());
            txtPedidoPF.setEnabled(false);
            txtClientePF.setEnabled(false);
            EditarPedido.setVisible(true);
        }
        else
            JOptionPane.showMessageDialog(this,"El pedido ya esta cancelado");
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Seleccione una fila de la tabla");
        }
    }//GEN-LAST:event_btnPosponerActionPerformed

    private void txtClientePFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClientePFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClientePFActionPerformed

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
            java.util.logging.Logger.getLogger(InterfazInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InterfazInventario dialog = new InterfazInventario(new javax.swing.JFrame(), true, "");
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
    private javax.swing.JDialog AgregarProducto;
    private javax.swing.JDialog AgregarStock;
    private javax.swing.JDialog Confirmacion;
    private javax.swing.JDialog EditarPedido;
    private javax.swing.JDialog EditarProducto;
    private com.toedter.calendar.JCalendar JCalendarF;
    private javax.swing.JButton btnAceptarC;
    private javax.swing.JButton btnAgregarProductoInv;
    private javax.swing.JButton btnAgregarStock;
    private javax.swing.JButton btnAumentarSP;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscarPP;
    private javax.swing.JButton btnBuscarSP;
    private javax.swing.JButton btnCancelarAgregarProductoInv;
    private javax.swing.JButton btnCancelarAgregarStock;
    private javax.swing.JButton btnCancelarC;
    private javax.swing.JButton btnCancelarEP;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarPP;
    private javax.swing.JButton btnEliminarSP;
    private javax.swing.JButton btnModigicarEP;
    private javax.swing.JButton btnPosponer;
    private javax.swing.JButton btnR;
    private javax.swing.JComboBox<String> cbmColorAP;
    private javax.swing.JComboBox<String> cbmColumn;
    private javax.swing.JComboBox<String> cbmFiltroA;
    private javax.swing.JComboBox<String> cbmFiltroP;
    private javax.swing.JComboBox<String> cbmTallaAP;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
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
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable linventarios;
    private javax.swing.JTable linventariosBS;
    private javax.swing.JTable lpedidos;
    private javax.swing.JTable lproductos;
    private javax.swing.JPanel menuInventario;
    private javax.swing.JPanel menuPedido;
    private javax.swing.JPanel menuProducto;
    private javax.swing.JPanel menuStock;
    private javax.swing.JScrollPane scrollProductos;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JTextField txtAgregarAS;
    private javax.swing.JButton txtAgregarSP;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtBuscarA;
    private javax.swing.JTextField txtBuscarP;
    private javax.swing.JTextField txtCantAAP;
    private javax.swing.JTextField txtCantMAP;
    private javax.swing.JTextField txtClientePF;
    private javax.swing.JTextField txtColor;
    private javax.swing.JTextField txtColorEP;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextArea txtDescripcionEP;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNombreAS;
    private javax.swing.JTextField txtNombrePAP;
    private javax.swing.JTextArea txtObservacionesAP;
    private javax.swing.JTextField txtPedidoPF;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtPrecioEP;
    private javax.swing.JTextField txtProductoAP;
    private javax.swing.JTextField txtProductoEP;
    private javax.swing.JTextField txtStockAP;
    private javax.swing.JTextField txtTalla;
    private javax.swing.JTextField txtTallaEP;
    private javax.swing.JTextField txtUbicacionAPP;
    // End of variables declaration//GEN-END:variables
}
