package dev.randolph.model.DTO;

import java.util.Arrays;

import dev.randolph.model.enums.EventType;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;

public class MetaDTO {
    
    private static MetaDTO meta;
    
    private static RequestStatus[][] statuses = {RequestStatus.getPending(), RequestStatus.getFinished()};
    private static EventType[] events = EventType.values();
    private static GradeFormatType[] gradeFormats = GradeFormatType.values();
    
    private MetaDTO() {}
    
    public static synchronized MetaDTO getMetaDTO() {
        if (meta == null) {
            meta = new MetaDTO();
        }
        return meta;
    }

    public RequestStatus[][] getStatuses() {
        return statuses;
    }

    public static EventType[] getEvents() {
        return events;
    }

    public GradeFormatType[] getGradeFormats() {
        return gradeFormats;
    }

    @Override
    public String toString() {
        return "MetaDTO [statuses=" + Arrays.toString(statuses) + ", gradeFormats=" + Arrays.toString(gradeFormats)
                + "]";
    }
}
