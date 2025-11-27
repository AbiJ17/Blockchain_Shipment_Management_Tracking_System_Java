package model;

public class CustomsOfficer extends User {

    private int officerID;
    private String agencyName;

    public CustomsOfficer() {
        setRole("CUSTOMS_OFFICER");
    }

    public CustomsOfficer(int userID,
            String username,
            String password,
            String email,
            int officerID,
            String agencyName) {

        super(userID, username, password, "CUSTOMS_OFFICER", email);
        this.officerID = officerID;
        this.agencyName = agencyName;
    }

    // ---- Domain behaviour ----

    public void recordClearanceDecision(Shipment shipment, String status) {
        if (shipment == null)
            return;
        shipment.setStatus(status);
        shipment.addHistoryEvent(
                "Customs clearance decision: " + status + " by officer " + officerID);
    }

    public void raiseAlert(Shipment shipment, String issue) {
        if (shipment == null)
            return;
        shipment.addHistoryEvent("Customs alert: " + issue);
    }

    // ---- Getters / setters ----

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
