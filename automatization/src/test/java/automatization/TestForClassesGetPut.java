package automatization;

import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class TestForClassesGetPut{
  public static void main (String[] args) throws Exception{
	Dotenv env = Dotenv.load();
    ArrayList<String> data = new ArrayList<>();
    GetterFromFileOfShedule getterFromExcel = new GetterFromFileOfShedule(args[0], data);
    getterFromExcel.getFromCell();
    System.out.println(data);
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost/" + env.get("MYSQL_DATABASE"), env.get("MYSQL_USER"), env.get("MYSQL_PASSWORD"));
    Statement st = con.createStatement();
    st.executeUpdate("CREATE TABLE Testing (some_text VARCHAR(100))");
    st.executeUpdate("INSERT INTO Testing (some_text) VALUES ("+"'"+data.get(20)+"'"+")");
    st.close();
    con.close();
  }
}