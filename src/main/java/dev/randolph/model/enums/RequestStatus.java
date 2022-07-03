package dev.randolph.model.enums;

public enum RequestStatus {
    PENDING_REVIEW,
    PENDING_GRADE,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    CANCELLED;
    
    public static RequestStatus[] getPending() {
        return new RequestStatus[]{PENDING_REVIEW, PENDING_GRADE, PENDING_APPROVAL};
    }
    
    public static RequestStatus[] getFinished() {
        return new RequestStatus[]{APPROVED, REJECTED, CANCELLED};
    }
    
    /**
     * Retrieves the filters based on the given string.
     * If input is null, or doesn't match, returns all the filters.
     * @param statusFilter The statuses to filter by.
     * @return An array of desired statuses to filter by.
     */
    public static RequestStatus[] getFilters(String statusFilter) {
        RequestStatus[] filter = null;
        if (statusFilter != null) {
            if (statusFilter.equalsIgnoreCase("PENDING")) {
                filter = RequestStatus.getPending();
            }
            else if (statusFilter.equalsIgnoreCase("FINISHED")) {
                filter = RequestStatus.getFinished();
            }
            else {
                filter = RequestStatus.values();
            }
        }
        else {
            filter = RequestStatus.values();
        }
        return filter;
    }
}