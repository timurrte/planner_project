package automatization;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ScheduleReader {
    private final String pathToFile;
    private final List<String> names;
    private final Map<String, List<Assignment>> scheduleByGroup;

    public ScheduleReader(String pathToFile) throws IOException {
        this.pathToFile = pathToFile;
        this.scheduleByGroup = new HashMap<>();
		this.names = this.loadNamesFromFile("names.txt");
    }

    public Map<String, List<Assignment>> readSchedule() throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(pathToFile);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            DataFormatter formatter = new DataFormatter();

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) continue;

                parseSheet(sheet, formatter);
            }
        }
        return scheduleByGroup;
    }

    private void parseSheet(Sheet sheet, DataFormatter formatter) {
    	parseSheet(sheet, formatter, 8, 78);
    }
    
    private void parseSheet(Sheet sheet, DataFormatter formatter, int startingRow, int endRow) {
        Row headerRow = sheet.getRow(startingRow-3);
        if (headerRow == null) return;

        String currentDay = "";

        for (int rowIndex = startingRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            currentDay = updateDay(row, formatter, currentDay);

            Cell timeCell = row.getCell(1);
            if (timeCell == null || formatter.formatCellValue(timeCell).isEmpty()) {
                timeCell = getMergedCellValue(sheet, rowIndex, 1);
            }

            String time = formatter.formatCellValue(timeCell);
            processAssignmentsForRow(row, headerRow, time, currentDay, formatter);
        }
    }

    private Cell getMergedCellValue(Sheet sheet, int rowIndex, int colIndex) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            if (mergedRegion.isInRange(rowIndex, colIndex)) {
                Row mergedRow = sheet.getRow(mergedRegion.getFirstRow());
                return mergedRow.getCell(mergedRegion.getFirstColumn());
            }
        }
        Row row = sheet.getRow(rowIndex);
        return row != null ? row.getCell(colIndex) : null;
    }


    private String updateDay(Row row, DataFormatter formatter, String currentDay) {
        Cell dayCell = row.getCell(0);
        if (dayCell != null && !formatter.formatCellValue(dayCell).isEmpty()) {
            return formatter.formatCellValue(dayCell);
        }
        return currentDay;
    }

    private void processAssignmentsForRow(Row row, Row headerRow, String time, String currentDay, DataFormatter formatter) {
        for (int colIndex = 2; colIndex < row.getLastCellNum(); colIndex++) {
            Cell cell = row.getCell(colIndex);
            if (cell == null || formatter.formatCellValue(cell).isEmpty()) continue;

            boolean hasHyperlink = cell.getHyperlink() != null;

            String cellValue = cleanClassName(formatter.formatCellValue(cell));
            String teacherName = containsNameFromList(cellValue);            
            
            if (!teacherName.isBlank() && hasHyperlink) {
            	cellValue = cellValue.replaceAll(teacherName, "");
            	cellValue = parseClassName(cellValue);
                createAndStoreAssignment(cellValue, currentDay, time, teacherName,  headerRow, colIndex, formatter, Numerator.Chyselnik);
            } else if (!teacherName.isBlank()) {
                Row nextRow = row.getSheet().getRow(row.getRowNum() + 1);
                if (nextRow != null) {
                    Cell lowerCell = nextRow.getCell(colIndex);
                    if (lowerCell != null && lowerCell.getHyperlink() != null) {
                    	String className = cleanClassName(formatter.formatCellValue(lowerCell));
                    	className = parseClassName(className);
                        createAndStoreAssignment(className, currentDay, time, teacherName.trim(), headerRow, colIndex, formatter, Numerator.Chyselnik);
                        continue;
                    }
                }
            }
        }
    }

    private void createAndStoreAssignment(String details, String currentDay, String time, String teacherName, Row headerRow, int colIndex, DataFormatter formatter, Numerator numerator) {
        String groupName = getGroupName(headerRow, colIndex, formatter);
        if (groupName == null || groupName.isEmpty()) return;

        Assignment assignment = new Assignment(currentDay, time, details, teacherName, groupName, numerator);
        scheduleByGroup.computeIfAbsent(groupName, k -> new ArrayList<>()).add(assignment);
    }

    
    private String containsNameFromList(String value) {
        for (String name : this.names) {
            if (value.contains(name)) {
                return name;
            }
        }
        return "";
    }
    
    private String parseClassName(String rawName) {
    	String phrase = "";
    	String chars = "";
    	for (int i = 0; i<rawName.length(); i++) {
    		char ch = rawName.charAt(i);
    		if (ch == ' ') continue;
    		if (Character.isDigit(ch)) continue;
    		chars += ch;
    		if (WordParser.isInList(chars)) {
    			phrase += chars +" ";
    			chars = "";
    		}
    	}
    	return phrase.trim();
    }

    private String cleanClassName(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return rawName;
        }
        rawName = rawName.replaceAll("\\s+", " ").trim();
        rawName = rawName.replaceAll("([а-яА-Яa-zA-Z])\\s+([а-яА-Яa-zA-Z])", "$1 $2");

        return rawName;
    }
    private String getGroupName(Row headerRow, int columnIndex, DataFormatter formatter) {
        Cell headerCell = headerRow.getCell(columnIndex);
        return headerCell != null ? formatter.formatCellValue(headerCell) : "";
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
