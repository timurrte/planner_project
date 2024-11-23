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
//    	System.out.println(formatter.formatCellValue(cell));
        String rawName = formatter.formatCellValue(cell).trim();
        if (rawName == null || rawName.isBlank()) return rawName;
        String reconstructedName = WordParser.reconstructWord(rawName);
        return cleanClassName(reconstructedName);
    }

    
    public String getTeacherInCell(List<String> names) {
    	String cellValue =  cleanClassName(this.formatter.formatCellValue(this.cell));
    	String name = containsNameFromList(names, cellValue);
    	this.cell.setCellValue(this.formatter.formatCellValue(this.cell).replaceAll(name, ""));
    	return name;
    }

    public String getClassroom() {
        String regex = "\\b\\d{3}[а-яА-Яa-zA-Z]?";
        String cellValue = cleanClassName(this.formatter.formatCellValue(cell)).trim();
        Matcher matcher = Pattern.compile(regex).matcher(cellValue);     
        String match;
        if (matcher.find()) {
            match = matcher.group();
            cell.setCellValue(cellValue.replaceAll(match, "").trim());
        } else {
            match = "online";
            cell.setCellValue(cellValue.replaceAll(match, "").trim());
        }

        return match;
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
