package dev.randolph.model;

import java.sql.Timestamp;

import dev.randolph.model.enums.EventType;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;

public class Request {
    
    private Integer id;                 // Provided by database
    private String employeeUsername;    // Provided by user
    private EventType eventType;        // Provided by user
    private RequestStatus status;       // Automatic -> Updated
    private Double cost;                // Provided by user
    private Double reimAmount;          // Automatic -> Updated Manager after creation
    private String grade;               // Provided by User -> After creation
    private GradeFormatType gradeFormat;// Provided by User
    private String cutoff;              // Provided by User
    private String justification;       // Provided by User
    private Timestamp startDate;        // Provided by User
    private Timestamp submissionDate;   // Automatic
    private String eventLocation;       // Provided by User
    private String eventDescription;    // Provided by User
    private Boolean isUrgent;           // Automatic
    private Boolean exceedsFunds;       // Automatic
    private String reason;              // Provided by User -> Manager after creation
    
    public Request() {}

    public Request(Integer id, String employeeUsername, EventType eventType, RequestStatus status, Double cost,
            Double reimAmount, String grade, GradeFormatType gradeFormat, String cutoff, String justification,
            Timestamp startDate, Timestamp submissionDate, String eventLocation, String eventDescription,
            Boolean isUrgent, Boolean exceedsFunds, String reason) {
        super();
        this.id = id;
        this.employeeUsername = employeeUsername;
        this.eventType = eventType;
        this.status = status;
        this.cost = cost;
        this.reimAmount = reimAmount;
        this.grade = grade;
        this.gradeFormat = gradeFormat;
        this.cutoff = cutoff;
        this.justification = justification;
        this.startDate = startDate;
        this.submissionDate = submissionDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.isUrgent = isUrgent;
        this.exceedsFunds = exceedsFunds;
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getReimAmount() {
        return reimAmount;
    }

    public void setReimAmount(Double reimAmount) {
        this.reimAmount = reimAmount;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public GradeFormatType getGradeFormat() {
        return gradeFormat;
    }

    public void setGradeFormat(GradeFormatType gradeFormat) {
        this.gradeFormat = gradeFormat;
    }

    public String getCutoff() {
        return cutoff;
    }

    public void setCutoff(String cutoff) {
        this.cutoff = cutoff;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Timestamp submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public Boolean getExceedsFunds() {
        return exceedsFunds;
    }

    public void setExceedsFunds(Boolean exceedsFunds) {
        this.exceedsFunds = exceedsFunds;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Request [id=" + id + ", employeeUsername=" + employeeUsername + ", eventType=" + eventType + ", status="
                + status + ", cost=" + cost + ", reimAmount=" + reimAmount + ", grade=" + grade + ", gradeFormat="
                + gradeFormat + ", cutoff=" + cutoff + ", justification=" + justification + ", startDate=" + startDate
                + ", submissionDate=" + submissionDate + ", eventLocation=" + eventLocation + ", eventDescription="
                + eventDescription + ", isUrgent=" + isUrgent + ", exceedsFunds=" + exceedsFunds + ", reason=" + reason
                + "]";
    }
}
