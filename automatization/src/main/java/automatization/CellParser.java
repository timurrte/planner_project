package automatization;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import automatization.enums.DayOfWeek;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private String subject;
    private DayOfWeek day;
    private Numerator numerator;
    private String teacher;
    private Sheet sh;
    private Cell lowerCell;
    private String time;

    public CellParser(Cell cell, DataFormatter formatter, DayOfWeek day, String time, String group, Numerator numerator) throws IOException {
        this.cell = cell;
        this.sh = cell.getSheet();
        this.formatter = formatter;
		this.names = this.loadNamesFromFile("names.txt");
        this.cellValue = cleanCell(formatter.formatCellValue(cell)).trim();
        this.teacher = extractTeacher(this.cellValue);
        if (this.teacher.isBlank()) {
        	return;
        }
        if (this.cellValue.isBlank()) {
        	parseLowerCell();
        }
		this.classroom = extractClassroom(this.cellValue);
		this.subject = extractSubject(this.cellValue);
		this.day = day;
		this.numerator = numerator;
		this.time = time;
		setAssignment(group);
    }
   
    public Assignment getAssignment() {
    	return assignment;
    }
    
    private void setAssignment(String groupName) {
        this.assignment = new Assignment(
            this.day,
            this.time,
            this.subject,
            this.teacher,
            this.classroom,
            groupName,
            this.numerator
        );
    }
    
    private String extractSubject(String rawName) {
        String rawValue = cleanCell(rawName).trim();
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
//        this.cellValue = WordParser.reconstructWord(cellValue.replace(match, "").trim());
        this.cellValue = this.cellValue.replace(match, "");
        return match;
    }
    
    public void parseLowerCell() {
    		this.cell.getColumnIndex();
    		Row lowerRow = sh.getRow(this.cell.getRowIndex() + 1);
    		this.lowerCell = lowerRow.getCell(this.cell.getColumnIndex());
    		this.cellValue = cleanCell(this.formatter.formatCellValue(lowerCell));
    	}

    public String getFormattedValue() {
        return cellValue;
    }

    public String getClassName() {
        return this.subject;
    }
    
    public String getClassName(String rawData) {
        String rawValue = cleanCell(getFormattedValue());
        String match = WordParser.reconstructWord(rawValue);
        this.assignment.setSubject(rawValue.replaceFirst(match, ""));
        return match;
    }
    
    public String getTeacher() {
    	return this.teacher;
    }

    public String parseClassroom() {
        String regex = "\\b\\d{3}[а-яА-Яa-zA-Z]?";
        Matcher matcher = Pattern.compile(regex).matcher(getFormattedValue());
        String match = matcher.find() ? matcher.group() : "online";
        this.cellValue = cellValue.replaceAll(match, "");
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
    
    private List<String> loadNamesFromFile(String fileName) throws IOException {
        List<String> names = new ArrayList<>();
        
        try (InputStream inputStream = getClass().getResourceAsStream("/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                names.add(line.trim());
            }
        }
        return names;
    }

}
