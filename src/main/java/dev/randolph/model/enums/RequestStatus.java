package dev.randolph.model.enums;

public enum RequestStatus {
    AWAITING_APPROVAL,
    PENDING_COMPLETION,
    APPROVED,
    REJECTED,
    CANCELLED;
    
    public static RequestStatus[] getPending() {
        RequestStatus[] pending = {AWAITING_APPROVAL, PENDING_COMPLETION};
        return pending;
    }
    
    public static RequestStatus[] getFinished() {
        RequestStatus[] finished = {APPROVED, REJECTED, CANCELLED};
        return finished;
    }
}
