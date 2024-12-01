package automatization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {

	public static void main (String[] args) throws Exception {
		Dotenv env = Dotenv.load();
	    String url = "jdbc:mysql://localhost/" + env.get("MYSQL_DATABASE");
	    String user = env.get("MYSQL_USER");
	    String password = env.get("MYSQL_PASSWORD");

	    ArrayList<Assignment> data = new ArrayList<>();
	    ScheduleReader getterFromExcel = new ScheduleReader(args[0]);

	    Map<String, List<Assignment>> timetable = getterFromExcel.readSchedule();

	    DatabaseHandler dbHandler = new DatabaseHandler(url, user, password);
	    dbHandler.insertTimetableIntoDatabase(timetable);

	  }

}
