package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Pedido {
    private int id;
    private ArrayList<Producto> productos;
    private LocalDateTime fechaDeCompra;
    private PaymentMethod metodoPago;

    // Constructor sin método de pago
    public Pedido(int id) {
        this.id = id;
        this.fechaDeCompra = LocalDateTime.now(); 
        this.productos = new ArrayList<>();
    }

    // Constructor con método de pago (opcional)
    public Pedido(int id, PaymentMethod metodoPago) {
        this.id = id;
        this.fechaDeCompra = LocalDateTime.now(); 
        this.productos = new ArrayList<>();
        this.metodoPago = metodoPago;
    }

    public void setMetodoPago(PaymentMethod metodoPago) {
        this.metodoPago = metodoPago;
    }

    public PaymentMethod getMetodoPago() {
        return metodoPago;
    }

    public LocalDateTime getFechaDeCompra() {
        return fechaDeCompra;
    }

    public void setFechaDeCompra(LocalDateTime fechaDeCompra) {
        this.fechaDeCompra = fechaDeCompra;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public double calcularTotal() {
        double total = 0;
        for (Producto p : productos) {
            total += p.getPrecio();
        }
        return total;
    }

    public void showOrder() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy HH:mm");

        System.out.println("\n===  Resumen del Pedido ===");
        if (productos.isEmpty()) {
            System.out.println("No se agregaron productos al pedido.");
        } else {
            for (Producto p : productos) {
                p.showInfo();
            }
            System.out.println("\nTotal a pagar: $" + calcularTotal());
        }

        System.out.println("Fecha de compra: " + fechaDeCompra.format(formatter));
        System.out.println("Fecha máxima de pago: " + fechaDeCompra.plusHours(24).format(formatter));
    }

    public void processOrder() {
        if (metodoPago == null) {
            System.out.println("Error: No se ha establecido un método de pago para el pedido.");
            return;
        }
        
        System.out.println("\nProcesando pedido ID: " + id + " con fecha " + fechaDeCompra.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy HH:mm")));
        showOrder();
        metodoPago.processPayment();
        System.out.println("Pedido procesado exitosamente.");
    }

    public int getId() {
        return id;
    }
}