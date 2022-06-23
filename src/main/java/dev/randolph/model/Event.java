package dev.randolph.model;

public class Event {
    
    private int id;
    private EventType type;
    
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

    @Override
    public String toString() {
        return "Event [id=" + id + ", type=" + type + "]";
    };
}
