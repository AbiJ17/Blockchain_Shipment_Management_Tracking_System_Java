package external;

import model.Document;

public class PaymentService {

    public int transactionID; 
    public double amount;
    public String status; 


    public boolean processPayment(double amount) {
        // Simulate payment processing logic
        if (amount > 0) {
            this.amount = amount;
            this.status = "Processed";
            return true;
        } else {
            this.status = "Failed";
            return false;
        }
    }

    public void triggerAutoPayment(int transactionID) {
        // Simulate auto-payment logic
        this.transactionID = transactionID;
        this.amount = 100.0; // Example amount
        this.status = "Auto-Payment Triggered";
    }

    public Document generateReciept(int transactionID) {
        // Simulate receipt generation logic
        this.transactionID = transactionID;
        Document receipt = new Document();
        // receipt.content = "Receipt for Transaction ID: " + transactionID + ", Amount: " + this.amount + ", Status: " + this.status;
        return receipt;
    }

    public int getTransactionID() {
        return transactionID;
    }
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    
    
}
