package external;

import java.util.List;

import model.Document;

public class OffChainStorage {

    public boolean avaliable;
    public List <Document> documents; 

    public boolean isAvaliable() {
        return avaliable;
    }

    public void setAvaliable(boolean avaliable) {
        this.avaliable = avaliable;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    
}
