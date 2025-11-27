package external;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Very simple in-memory blockchain stub.
 * It just keeps a list of String "entries" that represent transactions/blocks.
 * This is enough to support the controllers + gateway and to demonstrate the
 * design patterns in your project.
 */
public class BlockchainNetwork {

    private final List<String> ledger = new ArrayList<>();
    private boolean connected = false;

    /** Connect to the (simulated) blockchain network. */
    public boolean connect() {
        connected = true;
        return true;
    }

    /** Disconnect from the (simulated) blockchain network. */
    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    /**
     * Store a new transaction/block entry on the ledger.
     * In reality this would include consensus, validation, etc.
     */
    public boolean storeTransaction(String data) {
        if (!connected) {
            return false;
        }
        ledger.add(data);
        return true;
    }

    /**
     * Basic "block validation" stub.
     * For now we just check that we're connected and the hash string is not empty.
     */
    public boolean validateBlock(String blockHash) {
        if (!connected) {
            return false;
        }
        return blockHash != null && !blockHash.trim().isEmpty();
    }

    /**
     * Helper used by the gateway / controllers:
     * return all ledger entries that contain the given shipmentId.
     */
    public List<String> queryLedger(String shipmentId) {
        if (!connected) {
            return Collections.emptyList();
        }
        if (shipmentId == null || shipmentId.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> matches = new ArrayList<>();
        for (String entry : ledger) {
            if (entry != null && entry.contains(shipmentId)) {
                matches.add(entry);
            }
        }
        return matches;
    }

    /** Expose a copy of the whole ledger (read-only). */
    public List<String> getLedgerSnapshot() {
        return new ArrayList<>(ledger);
    }
}
