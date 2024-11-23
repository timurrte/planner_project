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
        
        DayOfWeek currentDay = DayOfWeek.NONE;
        
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


    private DayOfWeek updateDay(Row row, DataFormatter formatter, DayOfWeek currentDay) {
        Cell dayCell = row.getCell(0);
        if (dayCell != null && !formatter.formatCellValue(dayCell).isEmpty()) {
        	String day = formatter.formatCellValue(dayCell);
        	switch (day.toLowerCase()) {
        		case ("понеділок"):
        			return DayOfWeek.M;
        		case ("вівторок"):
        			return DayOfWeek.T;
        		case ("середа"):
        			return DayOfWeek.W;
        		case ("четверг"):
        			return DayOfWeek.S;
        		case ("п'ятниця"):
        			return DayOfWeek.F;
        			
        	}
        }
        return currentDay;
    }

    private void processAssignmentsForRow(Row row, Row headerRow, String time, DayOfWeek currentDay, DataFormatter formatter) {
        for (int colIndex = 2; colIndex < row.getLastCellNum(); colIndex++) {
            Cell cell = row.getCell(colIndex);
            if (cell == null || formatter.formatCellValue(cell).isEmpty()) continue;

            boolean hasHyperlink = cell.getHyperlink() != null;
            
            CellParser cellParser = new CellParser(cell, formatter);
            String teacher = cellParser.getTeacherInCell(names);
            
            if (!teacher.isBlank() && hasHyperlink) {
            	String classroom = cellParser.getClassroom();
            	String className = cellParser.getClassName();
                Numerator num = cell.getRowIndex() % 2 == 0 ? Numerator.Chyselnik : Numerator.Znamennyk;
                createAndStoreAssignment(className, currentDay, time, teacher, classroom,  headerRow, colIndex, formatter, num);
            } else if (!teacher.isBlank()) {
                Row nextRow = row.getSheet().getRow(row.getRowNum() + 1);
                if (nextRow != null) {
                    Cell lowerCell = nextRow.getCell(colIndex);
                    if (lowerCell != null && lowerCell.getHyperlink() != null) {
                    	CellParser lowerCellParser = new CellParser(lowerCell, formatter);
                    	String className = lowerCellParser.getClassName();
                    	String classroom = lowerCellParser.getClassroom();
                        createAndStoreAssignment(className, currentDay, time, teacher, classroom, headerRow, colIndex, formatter, Numerator.BOTH);
                        continue;
                    }
                }
            }
        }
    }

    private void createAndStoreAssignment(String subject, DayOfWeek currentDay, String time, String teacherName, String classroom, Row headerRow, int colIndex, DataFormatter formatter, Numerator numerator) {
        String groupName = getGroupName(headerRow, colIndex);
        if (groupName == null || groupName.isEmpty()) return;

        Assignment assignment = new Assignment(currentDay, time, subject, teacherName, classroom, groupName, numerator);
        scheduleByGroup.computeIfAbsent(groupName, k -> new ArrayList<>()).add(assignment);
    }

    public String getGroupName(Row headerRow, int columnIndex) {
        Cell headerCell = headerRow.getCell(columnIndex);
        DataFormatter formatter = new DataFormatter();
		String groupName = formatter.formatCellValue(headerCell).trim();
        return groupName.replaceAll("\\s+", "-");
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
