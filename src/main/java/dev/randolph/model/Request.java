package dev.randolph.model;

import java.time.LocalDateTime;

import dev.randolph.model.enums.GradeFormatType;

public class Request {
    
    private int id;
    private int employeeId;
    private int eventId;
    private Status status;
    private double cost;
    private double reimAmount;
    private String grade;
    private GradeFormatType gradeFormat;
    private String passCutoff;
    private String justification;
    private LocalDateTime startDate;
    private LocalDateTime submissionDate;
    private String eventLocation;
    private String eventDescription;
    private boolean isUrgent;
    private boolean exceedsFunds;
    private String reason;
    
    public Request() {}

    public Request(int id, int employeeId, int eventId, Status status, double cost, double reimAmount, String grade,
            GradeFormatType gradeFormat, String passCutoff, String justification, LocalDateTime startDate,
            LocalDateTime submissionDate, String eventLocation, String eventDescription, boolean isUrgent,
            boolean exceedsFunds, String reason) {
        super();
        this.id = id;
        this.employeeId = employeeId;
        this.eventId = eventId;
        this.status = status;
        this.cost = cost;
        this.reimAmount = reimAmount;
        this.grade = grade;
        this.gradeFormat = gradeFormat;
        this.passCutoff = passCutoff;
        this.justification = justification;
        this.startDate = startDate;
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

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
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
        return "Request [id=" + id + ", employeeId=" + employeeId + ", eventId=" + eventId + ", status=" + status
                + ", cost=" + cost + ", reimAmount=" + reimAmount + ", grade=" + grade + ", gradeFormat=" + gradeFormat
                + ", passCutoff=" + passCutoff + ", justification=" + justification + ", startDate=" + startDate
                + ", submissionDate=" + submissionDate + ", eventLocation=" + eventLocation + ", eventDescription="
                + eventDescription + ", isUrgent=" + isUrgent + ", exceedsFunds=" + exceedsFunds + ", reason=" + reason
                + "]";
    }
}
