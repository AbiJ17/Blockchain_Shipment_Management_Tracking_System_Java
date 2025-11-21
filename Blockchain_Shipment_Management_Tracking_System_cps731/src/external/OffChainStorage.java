package external;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulated off-chain storage for documents.
 * Stores document content keyed by a document ID.
 */
public class OffChainStorage {

    private final Map<String, String> storage = new HashMap<>();

    public boolean storeDocument(String documentId, String content) {
        if (documentId == null || content == null) {
            return false;
        }
        storage.put(documentId, content);
        System.out.println("[OffChainStorage] Stored document " + documentId);
        return true;
    }

    public String fetchDocument(String documentId) {
        return storage.get(documentId);
    }

    public boolean documentExists(String documentId) {
        return storage.containsKey(documentId);
    }
}
