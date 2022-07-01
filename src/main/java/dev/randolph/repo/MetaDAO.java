package dev.randolph.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.model.Event;
import dev.randolph.model.GradeFormat;
import dev.randolph.model.enums.EventType;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.util.ConnectionUtil;
import kotlin.Pair;

public class MetaDAO {
    
    private ConnectionUtil cu = ConnectionUtil.getConnectionUtil();
    private static Logger log = LogManager.getLogger(MetaDAO.class);
    
    /*
     * === READ ===
     */
    
    /**
     * Retrieves all the meta data used in the database.
     * @return A Pair with the event and gradeFormat meta data
     */
    public Pair<List<GradeFormat>, List<Event>> getMetaData() {
        log.debug("Getting meta data");
        return new Pair<>(getGradeFormatsMetaData(), getEventsMetaData());
    }
    
    /**
     * Retrieves all the event meta data used in the database.
     * @return A list of the event meta data.
     */
    public List<Event> getEventsMetaData() {
        log.debug("Getting Event Meta Data");
        String sql = "select * from events";
        List<Event> events = new ArrayList<Event>();
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                events.add(createEvent(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return events;
    }
    
    /**
     * Retrieves all the grade formats meta data used in the database.
     * @return A list of the grade format meta data.
     */
    public List<GradeFormat> getGradeFormatsMetaData() {
        log.debug("Getting GradeFormat Meta Data");
        String sql = "select * from grade_formats";
        List<GradeFormat> gradeFormats = new ArrayList<GradeFormat>();
        
        // Attempting to execute query
        try (Connection conn = cu.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                gradeFormats.add(createGradeFormat(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to execute query " + sql);
            e.printStackTrace();
        }
        
        return gradeFormats;
    }
    
    /*
     * === Utility ===
     */
    
    /**
     * Creates an Event object with the given result set.
     * Result set must have the actual data elements.
     * @param rs The result set that currently holds the data.
     * @return An Event Object
     * @throws SQLException
     */
    private Event createEvent(ResultSet rs) throws SQLException {
        Event event = new Event(
                EventType.valueOf(rs.getString("event_type")),
                rs.getDouble("reimbursement_percentage"));
        
        return event;
    }
    
    /**
     * Creates a GradeFormat object with the given result set.
     * Result set must have the actual data elements.
     * @param rs The result set that currently holds the data.
     * @return A GradeFormat Object
     * @throws SQLException
     */
    private GradeFormat createGradeFormat(ResultSet rs) throws SQLException {
        GradeFormat gradeFormat = new GradeFormat(
                GradeFormatType.valueOf(rs.getString("format")),
                rs.getString("default_cutoff"),
                rs.getBoolean("presentation_required"));
        
        return gradeFormat;
    }

}
