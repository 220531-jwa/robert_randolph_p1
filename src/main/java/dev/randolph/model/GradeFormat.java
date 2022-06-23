package dev.randolph.model;

public class GradeFormat {
    
    private int id;
    private GradeFormatType type;
    private String cutoff;
    private boolean presentationRequired;
    
    public GradeFormat() {}

    public GradeFormat(int id, GradeFormatType type, String cutoff, boolean presentationRequired) {
        super();
        this.id = id;
        this.type = type;
        this.cutoff = cutoff;
        this.presentationRequired = presentationRequired;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isPresentationRequired() {
        return presentationRequired;
    }

    public void setPresentationRequired(boolean presentationRequired) {
        this.presentationRequired = presentationRequired;
    }

    @Override
    public String toString() {
        return "GradeFormat [id=" + id + ", type=" + type + ", cutoff=" + cutoff + ", presentationRequired="
                + presentationRequired + "]";
    }
}
