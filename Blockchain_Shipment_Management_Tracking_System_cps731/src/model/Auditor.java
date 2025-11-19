package model;

public class Auditor extends User {

    public int auditID; 
    public String organization; 

    // public Report generateComplianceReport() {
        
    // }

    // public Report generateAuditTrail() {
        
    // }

    public boolean verifyRecords() {
        return false; 
    }

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
