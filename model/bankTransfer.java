package model;

public class bankTransfer extends PaymentMethod {
    private String bankName;
    private int accountNumber;


    public bankTransfer(String nombreCliente, double precio, String bankName, int accountNumber) {
        super(nombreCliente, precio);
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    @Override
    public void processPayment() {
        String accountNumberString = String.valueOf(accountNumber);
        System.out.println("Procesando pago mediante transferencia bancaria a la cuenta: @@@@" + accountNumberString.substring(accountNumberString.length() - 4));
    }
    
}
