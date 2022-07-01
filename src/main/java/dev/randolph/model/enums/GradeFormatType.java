package dev.randolph.model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum GradeFormatType {
    LETTER,
    PASSFAIL;
    
    public static List<String> getPossibleGradesFromType(GradeFormatType gradeFormat) {
        ArrayList<String> grades = new ArrayList<String>();
        if (gradeFormat == LETTER) {
            grades.addAll(Arrays.asList("A", "B", "C", "D", "F"));
        }
        else {
            grades.addAll(Arrays.asList("P", "F"));
        }
        
        return grades;
    }
}
