package automatization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {

	public static void main(String[] args) throws Exception {
		Dotenv env = Dotenv.load();
		System.out.println(env.get("MYSQL_USER"));
		ArrayList<String> data = new ArrayList<>();
	    GetterFromFileOfShedule getterFromExcel = new GetterFromFileOfShedule(args[0]);
	    getterFromExcel.getData();
	    System.out.println(data);
	    Connection con = DriverManager.getConnection("jdbc:mysql://localhost/" + env.get("MYSQL_DATABASE"), env.get("MYSQL_USER"), env.get("MYSQL_PASSWORD"));
	    Statement st = con.createStatement();
	    st.close();
	    con.close();
	}

}
