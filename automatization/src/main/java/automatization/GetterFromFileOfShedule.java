package automatization;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GetterFromFileOfShedule {
  String pathToFile;
  ArrayList<String> dataOfSchedule;
  public GetterFromFileOfShedule(String pathToFile, ArrayList<String> dataOfSchedule){
    this.pathToFile=pathToFile;
    this.dataOfSchedule=dataOfSchedule;
  }
  public void getFromCell() throws IOException {
    try(FileInputStream fin = new FileInputStream(pathToFile); Workbook wb = new XSSFWorkbook(fin)){
       DataFormatter formatter = new DataFormatter();
      for(int i = 0; i<wb.getNumberOfSheets(); i++){
        Sheet sh = wb.getSheetAt(i);
          for(Row r : sh){
            for(Cell c : r){
              dataOfSchedule.add(formatter.formatCellValue(c));
            }
          }
      }
    }
  }
}