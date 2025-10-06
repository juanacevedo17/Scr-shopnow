package model;

import java.sql.Date;

public class Card extends PaymentMethod {
    private short cvv;
    private int cardNumber;
    private Date expirationDate;
    
public Card(String nombreCliente, double precio, short cvv, int cardNumber, Date expirationDate) {
        super(nombreCliente, precio);
        this.cvv = cvv;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
    }
    public short getCvv() {
        return cvv;
    }
    public int getCardNumber() {
        return cardNumber;
    }
    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    public void processPayment() {
    String cardNumberString = String.valueOf(cardNumber);
    System.out.println("Procesando pago con tarjeta:   @@@" + cardNumberString.substring(cardNumberString.length() - 4));
}
}
