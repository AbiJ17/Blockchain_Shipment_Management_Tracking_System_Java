package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Shipment {

    private final String shipmentId;
    private String origin;
    private String destination;
    private String description;
    private ShipmentStatus status;
    private final LocalDateTime createdAt;
    private final List<Event> events = new ArrayList<>();

    public Shipment(String shipmentId, String origin, String destination, String description) {
        this.shipmentId = shipmentId;
        this.origin = origin;
        this.destination = destination;
        this.description = description;
        this.status = ShipmentStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }
}
