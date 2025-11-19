package model;

public class LogisticsProvider extends User{

    public int vehicleID; 
    public String routeInfo;

    public void recordPickup(int shipmentID) {}
    public void updateTransitStatus(int shipmentID, String status) {}
    public void confirmDelivery(int shipmentID) {}

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getRouteInfo() {
        return routeInfo;
    }

    public void setRouteInfo(String routeInfo) {
        this.routeInfo = routeInfo;
    }
    
}
