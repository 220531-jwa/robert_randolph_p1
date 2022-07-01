package dev.randolph.model;

import dev.randolph.model.enums.GradeFormatType;

public class GradeFormat {
    
    private GradeFormatType type;
    private String cutoff;
    private Boolean presentationRequired;
    
    public GradeFormat() {}

    public GradeFormat(GradeFormatType type, String cutoff, Boolean presentationRequired) {
        super();
        this.type = type;
        this.cutoff = cutoff;
        this.presentationRequired = presentationRequired;
    }

    public GradeFormatType getType() {
        return type;
    }

    public void setType(GradeFormatType type) {
        this.type = type;
    }

    public String getCutoff() {
        return cutoff;
    }

    public void setCutoff(String cutoff) {
        this.cutoff = cutoff;
    }

    public Boolean getPresentationRequired() {
        return presentationRequired;
    }

    public void setPresentationRequired(Boolean presentationRequired) {
        this.presentationRequired = presentationRequired;
    }

    @Override
    public String toString() {
        return "GradeFormat [type=" + type + ", cutoff=" + cutoff + ", presentationRequired=" + presentationRequired
                + "]";
    }
}
