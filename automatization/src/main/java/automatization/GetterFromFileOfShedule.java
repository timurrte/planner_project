package automatization;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GetterFromFileOfShedule {
    private final String pathToFile;
    private final Map<String, List<String>> teachers;

    public GetterFromFileOfShedule(String pathToFile) {
        this.pathToFile = pathToFile;
        this.teachers = new HashMap<>();
    }

    public Map<String, List<String>> getData() throws IOException {
        try (FileInputStream fin = new FileInputStream(pathToFile);
             Workbook workbook = new XSSFWorkbook(fin)) {

            DataFormatter formatter = new DataFormatter();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) continue;

                parseSheet(sheet, formatter);
            }
        }
        return teachers;
    }

    private void parseSheet(Sheet sheet, DataFormatter formatter) {

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return;

        for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            for (Cell cell : row) {
                if (cell.getColumnIndex() <= 1) continue;

                String cellValue = formatter.formatCellValue(cell);
                if (cellValue.isEmpty()) continue;

                String teacher = getTeacherName(headerRow, cell.getColumnIndex(), formatter);
                if (teacher != null && !teacher.isEmpty()) {
                    teachers.computeIfAbsent(teacher, k -> new ArrayList<>()).add(cellValue);
                }
            }
        }
    }

    private String getTeacherName(Row headerRow, int columnIndex, DataFormatter formatter) {
        Cell headerCell = headerRow.getCell(columnIndex);
        if (headerCell != null) {
            return formatter.formatCellValue(headerCell);
        }
        return "";
    }
}
