package gateway;

import external.BlockchainNetwork;
import model.Event;
import model.Shipment;

import java.util.List;
import java.util.Map;

/**
 * Gateway that the application uses to talk to the BlockchainNetwork.
 * This is your GRASP Indirection layer.
 */
public class BlockchainNetworkGateway {

    private final BlockchainNetwork blockchainNetwork;

    public BlockchainNetworkGateway(BlockchainNetwork blockchainNetwork) {
        this.blockchainNetwork = blockchainNetwork;
    }

    public String writeShipment(Shipment shipment) {
        blockchainNetwork.storeShipment(shipment);
        return shipment.getShipmentId();
    }

    public void recordEvent(Event event) {
        blockchainNetwork.storeEvent(event);
    }

    public Shipment getShipment(String shipmentId) {
        return blockchainNetwork.findShipmentById(shipmentId);
    }

    public List<Event> getEvents(String shipmentId) {
        return blockchainNetwork.findEventsByShipmentId(shipmentId);
    }

    public Map<String, Shipment> getAllShipments() {
        return blockchainNetwork.getAllShipments();
    }
}
