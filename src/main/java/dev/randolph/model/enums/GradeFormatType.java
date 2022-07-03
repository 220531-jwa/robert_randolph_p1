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
    
    /**
     * Determines if the given grade is an acceptable value for the given grade format.
     * @param gradeFormat The grade format.
     * @param grade The grade to test against the format.
     * @return True if the grade is an acceptable value, and false otherwise.
     */
    public static boolean validateGrade(GradeFormatType gradeFormat, String grade) {
        // Going through possible values for grade format
        for (String accGrade: GradeFormatType.getPossibleGradesFromType(gradeFormat)) {
            if (accGrade.equals(grade)) {
                return true;
            }
        }
        
        return false;
    }
}
