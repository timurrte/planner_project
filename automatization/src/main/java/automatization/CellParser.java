package automatization;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellParser {
    private final DataFormatter formatter;
    private final Cell cell;
    
    public CellParser(Cell cell, DataFormatter formatter) {
        this.formatter = formatter;
        this.cell = cell;
    }

    public String cleanClassName(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return rawName;
        }
        rawName = rawName.replaceAll("\\s+", " ").trim();
        rawName = rawName.replaceAll("([а-яА-Яa-zA-Z])\\s+([а-яА-Яa-zA-Z])", "$1 $2");
        return rawName;
    }

    public String getClassName() {
    	String rawName = this.formatter.formatCellValue(this.cell);
        if (rawName == null || rawName.isBlank()) return rawName;
        String className = cleanClassName(rawName);
        return WordParser.reconstructWord(className);
    }
    
    public String getTeacherInCell(List<String> names) {
    	String cellValue =  cleanClassName(this.formatter.formatCellValue(this.cell));
    	return containsNameFromList(names, cellValue);
    }

    public String getClassroom() {
        String regex = "\\b\\d{3}[а-яА-Яa-zA-Z]?";
        Matcher matcher = Pattern.compile(regex).matcher(this.formatter.formatCellValue(cell));
        return matcher.find() ? matcher.group() : "online";
    }
    
    private String containsNameFromList(List<String> names ,String value) {
        for (String name : names) {
            if (value.contains(name)) {
                return name;
            }
        }
        return "";
    }

}
