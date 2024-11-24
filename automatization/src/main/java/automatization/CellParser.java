package automatization;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import automatization.enums.DayOfWeek;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellParser {
    private final Cell cell;
    private final DataFormatter formatter;
    private Assignment assignment;
    private final List<String> names;
    private String cellValue;
    private String classroom;
    private String className;
    private String teacher;
    private Sheet sh;
    private Cell lowerCell;

    public CellParser(Cell cell, DataFormatter formatter, DayOfWeek day, String time, String group, Numerator numerator) throws IOException {
        this.cell = cell;
        this.sh = cell.getSheet();
        this.formatter = formatter;
		this.names = this.loadNamesFromFile("names.txt");
        this.cellValue = cleanCell(formatter.formatCellValue(cell));
        this.classroom = extractClassroom(this.cellValue);
        this.teacher = extractTeacher(this.cellValue);
        this.className = extractClassName(this.cellValue);
        System.out.println(cellValue);
        System.out.println(classroom);
        System.out.println(teacher);
		parseData(day, time, group, numerator);
		
    }
   
    public Assignment getAssignment() {
    	return assignment;
    }
    
    private void parseData(DayOfWeek currentDay, String time, String groupName, Numerator numerator) {
        this.assignment = new Assignment(
            currentDay,
            time,
            getClassName(),
            getTeacher(),
            getClassroom(),
            groupName,
            numerator
        );
    }
    
    private String extractClassName(String rawName) {
        String rawValue = cleanCell(rawName);
        String className = WordParser.reconstructWord(rawValue);
        return className;
    }

    private String extractTeacher(String cellValue) {
        String name = containsNameFromList(this.names, cellValue);
        this.cellValue = cellValue.replace(name, "").trim();
        return name;
    }

    private String extractClassroom(String cellValue) {
        String regex = "\\b\\d{3}[а-яА-Яa-zA-Z]?";
        Matcher matcher = Pattern.compile(regex).matcher(cellValue);
        String match = matcher.find() ? matcher.group() : "online";
        this.cellValue = WordParser.reconstructWord(cellValue.replace(match, "").trim());
        
        return match;
    }
    
    public void parseLowerCell() {
    	if (!this.teacher.isBlank()) {
    		this.cell.getColumnIndex();
    		Row lowerRow = sh.getRow(this.cell.getRowIndex() + 1);
    		this.lowerCell = lowerRow.getCell(this.cell.getColumnIndex());
    		this.cellValue = cleanCell(this.formatter.formatCellValue(lowerCell));
    		this.parseClassroom();
    		this.classroom = this.getClassroom();
    		
    		this.assignment.setClassroom(classroom);
    		this.assignment.setSubject(cellValue);
    	}
    }

    public String getFormattedValue() {
        return cellValue;
    }

    public String getClassName() {
        return this.className;
    }
    
    public String getClassName(String rawData) {
        String rawValue = cleanCell(getFormattedValue());
        String match = WordParser.reconstructWord(rawValue);
        this.assignment.setSubject(rawValue.replaceFirst(match, ""));
        return match;
    }

    private String parseTeacher() {
        String value = getFormattedValue();
        String name = containsNameFromList(names, value);
        if (name.isBlank()) {
        	return "";
        }
        return name;
    }
    
    public String getTeacher() {
    	return this.teacher;
    }

    public String parseClassroom() {
        String regex = "\\b\\d{3}[а-яА-Яa-zA-Z]?";
        Matcher matcher = Pattern.compile(regex).matcher(getFormattedValue());
        String match = matcher.find() ? matcher.group() : "online";
        this.cellValue = cellValue.replaceAll(match, "");
        System.out.println(match);
        return match;
    }
    
    public String getClassroom() {
    	return this.classroom;
    }

    private String containsNameFromList(List<String> names, String value) {
        for (String name : names) {
            if (value.contains(name)) return name;
        }
        return "";
    }

    private String cleanCell(String rawName) {
        if (rawName == null || rawName.isBlank()) return rawName;
        return rawName.replaceAll("\\s+", " ").trim();
    }
    
    private List<String> loadNamesFromFile(String filePath) throws IOException {
        List<String> names = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                names.add(line.trim());
            }
        }
        return names;
    }
}
