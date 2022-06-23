package dev.randolph.model;

import dev.randolph.model.enums.EventType;

public class Event {
    
    private int id;
    private EventType type;
    private double reimPercent;
    
    public Event() {}

    public Event(int id, EventType type) {
        super();
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public double getReimPercent() {
        return reimPercent;
    }

    public void setReimPercent(double reimPercent) {
        this.reimPercent = reimPercent;
    }

    @Override
    public String toString() {
        return "Event [id=" + id + ", type=" + type + ", reimPercent=" + reimPercent + "]";
    };
}
