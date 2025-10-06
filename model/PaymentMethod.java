package model;

public abstract class PaymentMethod {
    
    private String nombreCliente;
    private double precio;

    public PaymentMethod(String nombreCliente, double precio) {
        this.nombreCliente = nombreCliente;
        this.precio = precio;
    
    }

    public String getNombre() {
        return nombreCliente;
    }

    public double getPrecio() {
        return precio;
    }

    public void processPayment() {
        System.out.println("Procesando pago de $" + precio + " para " + nombreCliente);
    }

}
