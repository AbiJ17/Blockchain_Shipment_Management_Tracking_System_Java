package model;

public class Document {

    private final String documentId;
    private final String shipmentId;
    private final String name;
    private final String content; // for demo

    public Document(String documentId, String shipmentId, String name, String content) {
        this.documentId = documentId;
        this.shipmentId = shipmentId;
        this.name = name;
        this.content = content;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
