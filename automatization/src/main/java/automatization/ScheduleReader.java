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

import automatization.enums.DayOfWeek;

public class ScheduleReader {
    private final String pathToFile;
    private final Map<String, List<Assignment>> scheduleByGroup;

    public ScheduleReader(String pathToFile) throws IOException {
        this.pathToFile = pathToFile;
        this.scheduleByGroup = new HashMap<>();
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

    private void parseSheet(Sheet sheet, DataFormatter formatter) throws IOException {
    	parseSheet(sheet, formatter, 8, 78);
    }
    
    private void parseSheet(Sheet sheet, DataFormatter formatter, int startingRow, int endRow) throws IOException {
        Row headerRow = sheet.getRow(startingRow-3);
        if (headerRow == null) return;
        
        DayOfWeek currentDay = DayOfWeek.NONE;
        
        for (int rowIndex = startingRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            currentDay = updateDay(row, formatter, currentDay);

            Cell timeCell = row.getCell(1);
            if (timeCell == null || formatter.formatCellValue(timeCell).isEmpty()) {
                timeCell = getMergedCellForColumn(sheet, rowIndex, 1);
            }

            String time = formatter.formatCellValue(timeCell);
            processAssignmentsForRow(row, headerRow, time, currentDay, formatter);
        }
    }

    private Cell getMergedCellForColumn(Sheet sheet, int rowIndex, int colIndex) {
        DataFormatter formatter = new DataFormatter();

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            if (mergedRegion.isInRange(rowIndex, colIndex)) {
                int firstRow = mergedRegion.getFirstRow();
                int firstCol = mergedRegion.getFirstColumn();
                Row mergedRow = sheet.getRow(firstRow);

                if (mergedRow != null) {
                    Cell mergedCell = mergedRow.getCell(firstCol);
                    if (mergedCell != null && !formatter.formatCellValue(mergedCell).isBlank()) {
                        return mergedCell;
                    }
                }
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
        		case ("четвер"):
        			return DayOfWeek.S;
        		case ("п'ятниця"):
        			return DayOfWeek.F;	
        	}
        }
        return currentDay;
    }

    private void processAssignmentsForRow(Row row, Row headerRow, String time, DayOfWeek currentDay, DataFormatter formatter) throws IOException {
        Sheet sheet = row.getSheet();

        for (int colIndex = 2; colIndex < row.getLastCellNum(); colIndex++) {
            String groupName = getGroupName(headerRow, colIndex);
            if (groupName == null || groupName.isEmpty()) continue;

            Cell cell = row.getCell(colIndex);
            if (cell == null || formatter.formatCellValue(cell).isEmpty()) {
                cell = getMergedCellForColumn(sheet, row.getRowNum(), colIndex);
            }

            if (cell == null || formatter.formatCellValue(cell).isEmpty()) continue;
            
            Numerator numerator = (row.getRowNum() % 2 == 0) ? Numerator.Chyselnik : Numerator.Znamennyk;
            CellParser cellParser = new CellParser(cell, formatter, currentDay, time, groupName, numerator);
            
            if (cellParser.getAssignment() == null) {
            	continue;
            }
            
            Assignment lesson = cellParser.getAssignment();

            if (lesson.getSubject().isBlank()) {
                cellParser.parseLowerCell();
            }
            storeAssignment(cellParser.getAssignment());

        }
    }

    private void storeAssignment(Assignment assignment) {
        scheduleByGroup.computeIfAbsent(assignment.getGroup(), k -> new ArrayList<>()).add(assignment);
    }

    public String getGroupName(Row headerRow, int columnIndex) {
        Cell headerCell = headerRow.getCell(columnIndex);
        DataFormatter formatter = new DataFormatter();
		String groupName = formatter.formatCellValue(headerCell).trim();
        return groupName.replaceAll("\\s+", "-");
    }
    


}
