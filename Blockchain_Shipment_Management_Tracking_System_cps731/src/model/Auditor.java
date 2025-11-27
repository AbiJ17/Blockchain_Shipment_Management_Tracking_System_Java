package model;

public class Auditor extends User {

    private int auditID;
    private String organization;

    public Auditor() {
        setRole("AUDITOR");
    }

    public Auditor(int userID,
            String username,
            String password,
            String email,
            int auditID,
            String organization) {

        super(userID, username, password, "AUDITOR", email);
        this.auditID = auditID;
        this.organization = organization;
    }

    // ---- Domain behaviour ----

    public Report generateComplianceReport() {
        // In a full system, this would inspect many shipments.
        Report r = new Report("Compliance Report", "Auto-generated compliance summary.");
        return r;
    }

    public Report generateAuditTrail() {
        Report r = new Report("Audit Trail", "Auto-generated audit trail.");
        return r;
    }

    // ---- Getters / setters ----

    public int getAuditID() {
        return auditID;
    }

    public void setAuditID(int auditID) {
        this.auditID = auditID;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
