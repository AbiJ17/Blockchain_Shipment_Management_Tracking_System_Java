package gateway;

import external.OffChainStorage;
import model.Document;

/**
 * Adapter between our domain Document and the external OffChainStorage.
 */
public class OffChainStorageAdapter {

    private final OffChainStorage storage;

    public OffChainStorageAdapter(OffChainStorage storage) {
        this.storage = storage;
    }

    public void saveDocument(Document document) {
        storage.storeDocument(document.getDocumentId(), document.getContent());
    }

    public String loadDocumentContent(String documentId) {
        return storage.fetchDocument(documentId);
    }

    public boolean documentExists(String documentId) {
        return storage.documentExists(documentId);
    }
}
