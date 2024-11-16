package automatization;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTRowImpl;
import org.apache.poi.xssf.usermodel.TextDirection;

public class ScheduleForSKS{
  static final String[] Alldays = {"Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця"};
  static final String[] lecturers = {"Романчук О.О.", "Дубовик Т.М.", "Хорошилов С.В.", "Ревякіна А.О.", "Гузь Г.М.", "Величко Т.В."};
  public static void main(String[] args) throws IOException{
    try(Workbook wb = new XSSFWorkbook()){
      Sheet sh = wb.createSheet("Лист 1");
      Row r1 = sh.createRow(0);
      Cell days = r1.createCell(0);
      Cell lessons = r1.createCell(1);
      CellStyle cellStyle = wb.createCellStyle();
      cellStyle.setRotation((short) 90);
      cellStyle.setAlignment(HorizontalAlignment.CENTER);
      days.setCellStyle(cellStyle);
      days.setCellValue(new XSSFRichTextString("Дні"));
      lessons.setCellStyle(cellStyle);
      lessons.setCellValue("Пари");
      sh.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
      sh.addMergedRegion(new CellRangeAddress(0, 2, 1, 1));
      for(int i=1; i <= Alldays.length; i++){
        Row r = sh.createRow(3+14*(i-1));
        Cell xcell = r.createCell(0);
        xcell.setCellStyle(cellStyle);
        xcell.setCellValue(new XSSFRichTextString(Alldays[i-1]));
        sh.addMergedRegion(new CellRangeAddress(3+14*(i-1), 3+14*(i-1)+13, 0, 0));
      }
      try(FileOutputStream fout = new FileOutputStream("Schedule.xlsx")){
        wb.write(fout); 
      }
    }
  }
}
