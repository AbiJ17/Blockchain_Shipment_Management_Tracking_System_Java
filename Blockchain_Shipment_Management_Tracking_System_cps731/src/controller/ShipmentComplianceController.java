package controller;

import external.BlockchainNetwork;

import java.util.List;
import model.Auditor;
import model.CustomsOfficer;

public class ShipmentComplianceController {

    public BlockchainNetwork blockchain;
    // public SmartContract smartContract;
    public Auditor auditor;
    public CustomsOfficer customsOfficer;
    // public List<Document> documents;

    public BlockchainNetwork getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(BlockchainNetwork blockchain) {
        this.blockchain = blockchain;
    }
    
    // public SmartContract getSmartContract() {
    //     return smartContract;
    // }

    // public void setSmartContract(SmartContract smartContract) {
    //     this.smartContract = smartContract;
    // }

    public Auditor getAuditor() {
        return auditor;
    }

    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }

    public CustomsOfficer getCustomsOfficer() {
        return customsOfficer;
    }

    public void setCustomsOfficer(CustomsOfficer customsOfficer) {
        this.customsOfficer = customsOfficer;
    }

    // public List<Document> getDocuments() {
    //     return documents;
    // }

    // public void setDocuments(List<Document> documents) {
    //     this.documents = documents;
    // }
    
    
}
