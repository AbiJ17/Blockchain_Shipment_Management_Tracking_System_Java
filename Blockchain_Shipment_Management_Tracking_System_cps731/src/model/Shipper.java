package model;

public class Shipper extends User {

    private String companyName;
    private String address;

    // DEFAULT CONSTRUCTOR (required for LoginFrame seedDemoUsers)
    public Shipper() {

        setRole("SHIPPER");
    }

    // FULL CONSTRUCTOR
    public Shipper(int userID,
            String username,
            String password,
            String email,
            String companyName,
            String address) {

        super(userID, username, password, "SHIPPER", email);
        this.companyName = companyName;
        this.address = address;
    }

    // METHODS USED IN CONTROLLERS (no change needed)
    public Shipment createShipment(String shipmentId,
            String origin,
            String destination,
            String description) {

        Shipment shipment = new Shipment(shipmentId, origin, destination, description);
        shipment.addHistoryEvent("Shipment created by shipper " + getUsername());
        return shipment;
    }

    public void updateShipmentStatus(Shipment shipment, String newStatus) {
        if (shipment != null) {
            shipment.setStatus(newStatus);
        }
    }

    // GETTERS/SETTERS
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
