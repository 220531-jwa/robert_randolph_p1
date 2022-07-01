package dev.randolph.model;

import dev.randolph.model.enums.EventType;

public class Event {
    
    private EventType type;
    private Double reimPercent;
    
    public Event() {}

    public Event(EventType type, Double reimPercent) {
        super();
        this.type = type;
        this.reimPercent = reimPercent;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Double getReimPercent() {
        return reimPercent;
    }

    public void setReimPercent(Double reimPercent) {
        this.reimPercent = reimPercent;
    }

    @Override
    public String toString() {
        return "Event [type=" + type + ", reimPercent=" + reimPercent + "]";
    }
}
