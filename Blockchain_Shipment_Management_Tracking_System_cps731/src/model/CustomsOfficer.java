package model;

public class CustomsOfficer extends User {

    public int officerID; 
    public String agencyName;

    // public boolean verifyDocuments(Document doc) {
    //     // Implementation for inspecting a shipment
    //     return false; // Placeholder return value
    // }

    public void recordClearanceDecision(int shipmentID, String status) {
       
    }

    public void raiseAlert(int shipmentID) {
        
    }

    public int getOfficerID() {
        return officerID;
    }

    public void setOfficerID(int officerID) {
        this.officerID = officerID;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }
    
}
