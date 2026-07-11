package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
    
    public static boolean isValidDate(String s) {
        if (isEmpty(s) || s.trim().equalsIgnoreCase("YYYY-MM-DD")) {
            return true;
        }
        try {
            LocalDate.parse(s.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
