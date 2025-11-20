package gateway;

import external.OffChainStorage;
import model.Document;


public class OffChainStorageAdapter {

    public boolean connected;
    public int lastTransactionID;
    public OffChainStorage offChainStorage;

    public boolean connect() {
        return false;
    }

    public String uploadFile(Document document) { 
        return null;
    }

    public Document retrieveFile(String hash) {
        return null;
    }

    public boolean verifyIntegrity (Document document) {
        return false;
    }

    public void disconnect() {

    }
    
}
