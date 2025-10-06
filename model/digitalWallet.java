package model;

public class digitalWallet extends PaymentMethod {
    private String phoneNumber;
    private String provider;

    public digitalWallet(String nombreCliente, double precio, String phoneNumber, String provider) {
        super(nombreCliente, precio);
        this.phoneNumber = phoneNumber;
        this.provider = provider;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProvider() {
        return provider;
    }

    @Override
    public void processPayment() {
        String phoneNumberString = phoneNumber;
        System.out.println("Procesando pago mediante billetera digital (" + provider + ") al número: @@@@" + phoneNumberString.substring(phoneNumberString.length() - 4));
    }
}