package model;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);

        // Crear pedido sin método de pago inicialmente
        Pedido pedido = new Pedido(1);

        // Lista de productos
        List<Producto> productos = CargaDeProductos.CargaDeProducto("Catalogo.txt");

        System.out.println("=== Catálogo de productos ===");
        for (int i = 0; i < productos.size(); i++) {
            System.out.println((i + 1) + ". " + productos.get(i).getNombre() + " - $" + productos.get(i).getPrecio());
        }
        System.out.println("0. Finalizar compra");

        int opcion;
        do {
            System.out.print("\nSeleccione un producto (0 para finalizar): ");
            opcion = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            if (opcion == 0) {
                System.out.println("\nPedido finalizado.");
            } else if (opcion >= 1 && opcion <= productos.size()) {
                // Corregir el índice (restar 1 porque la lista empieza en 0)
                Producto productoSeleccionado = productos.get(opcion - 1);
                pedido.agregarProducto(productoSeleccionado);
                System.out.println(" " + productoSeleccionado.getNombre() + " agregado al carrito.");
            } else {
                System.out.println(" Opción inválida. Intente de nuevo.");
            }

        } while (opcion != 0);

        // Verificar si hay productos en el pedido
        if (pedido.getProductos().isEmpty()) {
            System.out.println("No hay productos en el pedido. Saliendo...");
            sc.close();
            return;
        }

        System.out.println("\n=== Productos en el pedido ===");
        for (Producto p : pedido.getProductos()) {
            p.showInfo();
        }

        System.out.println("\n=== Total del pedido ===");
        double total = pedido.calcularTotal();
        System.out.println("Total a pagar: $" + total);

        // Solicitar datos del cliente
        System.out.println("\n=== Datos del Cliente ===");
        System.out.print("Ingrese su nombre: ");
        String nombreCliente = sc.nextLine();
        
        System.out.print("Ingrese su email: ");
        String emailCliente = sc.nextLine();

        Cliente cliente = new Cliente(nombreCliente, emailCliente);
        cliente.showInfo();

        // Seleccionar método de pago
        System.out.println("\n=== Método de Pago ===");
        System.out.println("1. Tarjeta de Crédito");
        System.out.println("2. Billetera Digital");
        System.out.println("3. Transferencia Bancaria");
        System.out.print("Seleccione método de pago (1-3): ");
        
        int metodoPago = sc.nextInt();
        sc.nextLine(); // Limpiar buffer

        PaymentMethod paymentMethod = null;

        switch (metodoPago) {
            case 1:
                paymentMethod = crearTarjetaCredito(sc, nombreCliente, total);
                break;
            case 2:
                paymentMethod = crearBilleteraDigital(sc, nombreCliente, total);
                break;
            case 3:
                paymentMethod = crearTransferenciaBancaria(sc, nombreCliente, total);
                break;
            default:
                System.out.println("Método de pago no válido.");
                sc.close();
                return;
        }

        // Asignar método de pago al pedido
        pedido.setMetodoPago(paymentMethod);

        // Mostrar información completa del pedido
        System.out.println("\n=== Resumen Final del Pedido ===");
        pedido.showOrder();
        
        // Procesar el pedido
        System.out.println("\n=== Procesando Pago ===");
        pedido.processOrder();

        // Mostrar fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy HH:mm", new Locale("es", "ES"));
        System.out.println("\nFecha de compra: " + pedido.getFechaDeCompra().format(formatter));
        System.out.println("Fecha máxima de pago: " + pedido.getFechaDeCompra().plusHours(24).format(formatter));

        System.out.println("\n¡Gracias por su compra!");
        sc.close();
    }

    private static Card crearTarjetaCredito(Scanner sc, String nombreCliente, double total) {
        System.out.println("\n--- Datos de Tarjeta de Crédito ---");
        
        System.out.print("Ingrese CVV (3 dígitos): ");
        short cvv = sc.nextShort();
        sc.nextLine();
        
        System.out.print("Ingrese número de tarjeta (16 dígitos): ");
        int cardNumber = sc.nextInt();
        sc.nextLine();
        
        System.out.print("Ingrese fecha de expiración (YYYY-MM-DD): ");
        String fechaStr = sc.nextLine();
        java.sql.Date expirationDate = java.sql.Date.valueOf(fechaStr);

        return new Card(nombreCliente, total, cvv, cardNumber, expirationDate);
    }

    private static digitalWallet crearBilleteraDigital(Scanner sc, String nombreCliente, double total) {
        System.out.println("\n--- Datos de Billetera Digital ---");
        
        System.out.print("Ingrese número de teléfono: ");
        String phoneNumber = sc.nextLine();
        
        System.out.print("Ingrese proveedor (Ej: PayPal, Mercado Pago): ");
        String provider = sc.nextLine();

        return new digitalWallet(nombreCliente, total, phoneNumber, provider);
    }

    private static bankTransfer crearTransferenciaBancaria(Scanner sc, String nombreCliente, double total) {
        System.out.println("\n--- Datos de Transferencia Bancaria ---");
        
        System.out.print("Ingrese nombre del banco: ");
        String bankName = sc.nextLine();
        
        System.out.print("Ingrese número de cuenta: ");
        int accountNumber = sc.nextInt();
        sc.nextLine();

        return new bankTransfer(nombreCliente, total, bankName, accountNumber);
    }
}