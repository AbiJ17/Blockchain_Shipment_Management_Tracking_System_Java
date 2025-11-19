package model;

public class Warehouse {

    public int warehouseID;
    public String address; 
    public int capacity;

    public void recordReciept(int shipmentID) {}
    public void logStorageDetails() {}
    public void confirmDispatch(int shipmentID) {}

    public int getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
}
