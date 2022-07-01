package dev.randolph.model.DTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.randolph.model.enums.EventType;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;
import kotlin.Pair;

public class MetaDTO {
    
    private static MetaDTO meta;
    
    private static RequestStatus[][] statuses = {RequestStatus.getPending(), RequestStatus.getFinished()};
    private static EventType[] events = EventType.values();
    private static List<Pair<GradeFormatType, List<String>>> gradeFormats;
    
    private MetaDTO() {
        gradeFormats = new ArrayList<Pair<GradeFormatType,List<String>>>();
        
        for (GradeFormatType gradeFormat: GradeFormatType.values()) {
            gradeFormats.add(new Pair<>(gradeFormat, GradeFormatType.getPossibleGradesFromType(gradeFormat)));
        }
    }
    
    public static synchronized MetaDTO getMetaDTO() {
        if (meta == null) {
            meta = new MetaDTO();
        }
        return meta;
    }

    public RequestStatus[][] getStatuses() {
        return statuses;
    }

    public EventType[] getEvents() {
        return events;
    }

    public List<Pair<GradeFormatType, List<String>>> getGradeFormats() {
        return gradeFormats;
    }

    @Override
    public String toString() {
        return "MetaDTO [getStatuses()=" + Arrays.toString(getStatuses()) + ", getEvents()="
                + Arrays.toString(getEvents()) + ", getGradeFormats()=" + getGradeFormats() + "]";
    }
    
}
