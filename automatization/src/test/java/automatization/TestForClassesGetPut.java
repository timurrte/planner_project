package automatization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import automatization.DatabaseHandler;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class TestForClassesGetPut {
  public static void main (String[] args) throws Exception {
	Dotenv env = Dotenv.load();
    String url = "jdbc:mysql://localhost/" + env.get("MYSQL_DATABASE");
    String user = env.get("MYSQL_USER");
    String password = env.get("MYSQL_PASSWORD");

    ArrayList<Assignment> data = new ArrayList<>();
    ScheduleReader getterFromExcel = new ScheduleReader(args[0]);

    Map<String, List<Assignment>> timetable = getterFromExcel.readSchedule();

    for (String key : timetable.keySet()) {
    	for (Assignment val : timetable.get(key)) {
    		System.out.println(val);
    	}
    }
    
    DatabaseHandler dbHandler = new DatabaseHandler(url, user, password);
    dbHandler.insertTimetableIntoDatabase(timetable);


  }
}