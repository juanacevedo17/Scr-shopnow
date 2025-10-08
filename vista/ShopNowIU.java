package vista;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import model.Pedido;
import model.Producto;
import model.digitalWallet;
import model.CargaDeProductos;
import model.Cliente;
import model.PaymentMethod;
import model.Card;
import model.bankTransfer;

public class ShopNowIU extends JFrame {

    private JList<String> productoList;
    private DefaultListModel<String> productoModel;
    private JTextArea carArea;
    private JLabel totalLabel;
    private JButton btnTerminarCompra;
    private Pedido pedido;
    private List<Producto> products;
    private Cliente cliente;

    public ShopNowIU() {
        setTitle("Shopnow - Carrito de compras");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        products = CargaDeProductos.CargaDeProducto("Catalogo.txt");

        // Inicializar pedido sin método de pago (se asignará después)
        pedido = new Pedido(1);

        productoModel = new DefaultListModel<>();
        for (Producto producto : products) {
            productoModel.addElement(
                producto.getId() + " - " +
                producto.getNombre() + " -$ " +
                producto.getPrecio()
            );
        }

        productoList = new JList<>(productoModel);
        JScrollPane scrollCatalogo = new JScrollPane(productoList);

        JButton btnAgregar = new JButton("Agregar al carrito");
        btnAgregar.addActionListener(e -> {
            int selectedIndex = productoList.getSelectedIndex();
            if (selectedIndex != -1) {
                Producto seleccionado = products.get(selectedIndex);
                pedido.agregarProducto(seleccionado); 
                carArea.append(seleccionado.getNombre() + " -$ " + seleccionado.getPrecio() + "\n");
                actualizarTotal();
            }
        });

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(new JLabel("Catálogo de Productos"), BorderLayout.NORTH);
        panelIzquierdo.add(scrollCatalogo, BorderLayout.CENTER);
        panelIzquierdo.add(btnAgregar, BorderLayout.SOUTH);

        carArea = new JTextArea();
        carArea.setEditable(false);
        JScrollPane scrollCarrito = new JScrollPane(carArea);

        totalLabel = new JLabel("Total: $0.0");

        btnTerminarCompra = new JButton("Terminar compra");
        btnTerminarCompra.addActionListener(e -> terminarCompra());

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(totalLabel, BorderLayout.CENTER);
        panelInferior.add(btnTerminarCompra, BorderLayout.EAST);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(scrollCarrito, BorderLayout.CENTER);
        panelDerecho.add(panelInferior, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setDividerLocation(400);

        add(splitPane, BorderLayout.CENTER);
    }

    private void actualizarTotal() {
        double total = 0.0;
        for (Producto p : pedido.getProductos()) { 
            total += p.getPrecio();
        }
        totalLabel.setText("Total: $" + total);
    }

    private void terminarCompra() {
        if (pedido.getProductos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío. Agregue productos antes de finalizar la compra.");
            return;
        }

        // Solicitar datos del cliente
        String nombreCliente = JOptionPane.showInputDialog(this, "Ingrese su nombre:");
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }

        String emailCliente = JOptionPane.showInputDialog(this, "Ingrese su email:");
        if (emailCliente == null || emailCliente.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El email es obligatorio.");
            return;
        }

        cliente = new Cliente(nombreCliente, emailCliente);

        // Seleccionar método de pago
        String[] opcionesPago = {"Tarjeta de Crédito", "Billetera Digital", "Transferencia Bancaria"};
        String metodoSeleccionado = (String) JOptionPane.showInputDialog(
            this,
            "Seleccione método de pago:",
            "Método de Pago",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcionesPago,
            opcionesPago[0]
        );

        if (metodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un método de pago.");
            return;
        }

        PaymentMethod metodoPago = null;
        double total = pedido.calcularTotal();

        try {
            switch (metodoSeleccionado) {
                case "Tarjeta de Crédito":
                    metodoPago = crearMetodoTarjeta(nombreCliente, total);
                    break;
                case "Billetera Digital":
                    metodoPago = crearMetodoBilletera(nombreCliente, total);
                    break;
                case "Transferencia Bancaria":
                    metodoPago = crearMetodoTransferencia(nombreCliente, total);
                    break;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en los datos ingresados. Verifique los números.");
            return;
        }

        if (metodoPago != null) {
            pedido.setMetodoPago(metodoPago);
            procesarPago();
        }
    }

    private Card crearMetodoTarjeta(String nombreCliente, double total) {
        String cvvStr = JOptionPane.showInputDialog(this, "Ingrese CVV (3 dígitos):");
        if (cvvStr == null || cvvStr.length() != 3) {
            throw new NumberFormatException("CVV inválido");
        }
        short cvv = Short.parseShort(cvvStr);

        String numeroTarjeta = JOptionPane.showInputDialog(this, "Ingrese número de tarjeta (16 dígitos):");
        if (numeroTarjeta == null || numeroTarjeta.length() != 16) {
            throw new NumberFormatException("Número de tarjeta inválido");
        }
        int cardNumber = Integer.parseInt(numeroTarjeta);

        String fechaExpiracion = JOptionPane.showInputDialog(this, "Ingrese fecha de expiración (YYYY-MM-DD):");
        if (fechaExpiracion == null || fechaExpiracion.isEmpty()) {
            throw new NumberFormatException("Fecha de expiración inválida");
        }
        java.sql.Date expirationDate = java.sql.Date.valueOf(fechaExpiracion);

        return new Card(nombreCliente, total, cvv, cardNumber, expirationDate);
    }

    private digitalWallet crearMetodoBilletera(String nombreCliente, double total) {
        String phoneNumber = JOptionPane.showInputDialog(this, "Ingrese número de teléfono:");
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new NumberFormatException("Número de teléfono inválido");
        }

        String provider = JOptionPane.showInputDialog(this, "Ingrese proveedor (Ej: PayPal, Mercado Pago):");
        if (provider == null || provider.trim().isEmpty()) {
            throw new NumberFormatException("Proveedor inválido");
        }

        return new digitalWallet(nombreCliente, total, phoneNumber, provider);
    }

    private bankTransfer crearMetodoTransferencia(String nombreCliente, double total) {
        String bankName = JOptionPane.showInputDialog(this, "Ingrese nombre del banco:");
        if (bankName == null || bankName.trim().isEmpty()) {
            throw new NumberFormatException("Nombre del banco inválido");
        }

        String accountNumberStr = JOptionPane.showInputDialog(this, "Ingrese número de cuenta:");
        if (accountNumberStr == null || accountNumberStr.trim().isEmpty()) {
            throw new NumberFormatException("Número de cuenta inválido");
        }
        int accountNumber = Integer.parseInt(accountNumberStr);

        return new bankTransfer(nombreCliente, total, bankName, accountNumber);
    }

    private void procesarPago() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm:ss");
        String fechaHora = ahora.format(formatter);

        StringBuilder resumen = new StringBuilder();
        resumen.append("¡Gracias por su compra!\n\n");
        resumen.append("Cliente: ").append(cliente.getNombre()).append("\n");
        resumen.append("Email: ").append(cliente.getEmail()).append("\n\n");
        resumen.append("Productos comprados:\n");
        
        for (Producto p : pedido.getProductos()) {
            resumen.append(" - ").append(p.getNombre()).append(" - $").append(p.getPrecio()).append("\n");
        }
        
        resumen.append("\n").append(totalLabel.getText()).append("\n");
        resumen.append("Fecha y hora: ").append(fechaHora).append("\n\n");
        
        // Procesar el pago
        pedido.processOrder();

        JOptionPane.showMessageDialog(this, resumen.toString());

        // Limpiar carrito después de la compra
        pedido = new Pedido(pedido.getId() + 1);
        carArea.setText("");
        actualizarTotal();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ShopNowIU().setVisible(true);
        });
    }
}