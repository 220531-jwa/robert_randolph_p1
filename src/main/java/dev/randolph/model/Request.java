package dev.randolph.model;

import java.sql.Timestamp;

import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;

public class Request {
    
    private int id;
    private String employeeUsername;
    private String eventType;
    private RequestStatus status;
    private double cost;
    private double reimAmount;
    private String grade;
    private GradeFormatType gradeFormat;
    private String passCutoff;
    private String justification;
    private Timestamp startDate;
    private Timestamp submissionDate;
    private String eventLocation;
    private String eventDescription;
    private boolean isUrgent;
    private boolean exceedsFunds;
    private String reason;
    
    public Request() {}

    public Request(int id, String employeeUsername, String eventType, RequestStatus status, double cost,
            double reimAmount, String grade, GradeFormatType gradeFormat, String passCutoff, String justification,
            Timestamp timestamp, Timestamp submissionDate, String eventLocation, String eventDescription,
            boolean isUrgent, boolean exceedsFunds, String reason) {
        super();
        this.id = id;
        this.employeeUsername = employeeUsername;
        this.eventType = eventType;
        this.status = status;
        this.cost = cost;
        this.reimAmount = reimAmount;
        this.grade = grade;
        this.gradeFormat = gradeFormat;
        this.passCutoff = passCutoff;
        this.justification = justification;
        this.startDate = timestamp;
        this.submissionDate = submissionDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.isUrgent = isUrgent;
        this.exceedsFunds = exceedsFunds;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getReimAmount() {
        return reimAmount;
    }

    public void setReimAmount(double reimAmount) {
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

    public String getPassCutoff() {
        return passCutoff;
    }

    public void setPassCutoff(String passCutoff) {
        this.passCutoff = passCutoff;
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

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public boolean isExceedsFunds() {
        return exceedsFunds;
    }

    public void setExceedsFunds(boolean exceedsFunds) {
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
                + gradeFormat + ", passCutoff=" + passCutoff + ", justification=" + justification + ", startDate="
                + startDate + ", submissionDate=" + submissionDate + ", eventLocation=" + eventLocation
                + ", eventDescription=" + eventDescription + ", isUrgent=" + isUrgent + ", exceedsFunds=" + exceedsFunds
                + ", reason=" + reason + "]";
    }
}
