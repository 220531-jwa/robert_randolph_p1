package dev.randolph.model;

import java.util.List;

import dev.randolph.repo.MetaDAO;
import kotlin.Pair;

public class MetaData {
    
    private static MetaData metaData;
    private static MetaDAO metaDAO = new MetaDAO();
    
    private static List<GradeFormat> gradeFormats;
    private static List<Event> events;
    
    private MetaData() {
        refresh();
    }
    
    public static MetaData getMeta() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        
        return metaData;
    }
    
    // Calls the database to get-up-to-date meta information
    // Should almost never change
    public void refresh() {
        Pair<List<GradeFormat>, List<Event>> metaData = metaDAO.getMetaData();
        gradeFormats = metaData.getFirst();
        events = metaData.getSecond();
    }

    public List<GradeFormat> getGradeformats() {
        return gradeFormats;
    }

    public List<Event> getEvents() {
        return events;
    }
}
