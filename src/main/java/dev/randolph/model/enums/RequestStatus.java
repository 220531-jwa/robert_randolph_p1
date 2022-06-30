package dev.randolph.model.enums;

public enum RequestStatus {
    PENDING_REVIEW,
    PENDING_GRADE,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    CANCELLED;
    
    public static RequestStatus[] getPending() {
        RequestStatus[] pending = {PENDING_REVIEW, PENDING_GRADE, PENDING_APPROVAL};
        return pending;
    }
    
    public static RequestStatus[] getFinished() {
        RequestStatus[] finished = {APPROVED, REJECTED, CANCELLED};
        return finished;
    }
}