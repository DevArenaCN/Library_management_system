/*
*	This program implements the CSC540 Project Database Books.
* 	This program will create tables, insert data, and test query.
* 	by Shijie Li
* 	Use functions to implement UI
*	Assume faculty status for loginID.
* 	Check due date and send notifications
*/
import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.Date;
import java.text.*;

public class Project_DB_Notifications_faculty {
	// Database connection setup
	private static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";  // URL address of project jdbc
	//private String user = "sli41"; // User name in the database
	//private String passwd = "200110528"; // Password in the database
	private String user = "ychen71";    // For example, "jsmith"
    private String passwd = "200099159";    // Your 9 digit student ID number
	private Connection conn = null;  // SQL connection
	private Statement stmt = null;  // SQL statement
	private String loginID; // login id of the faculty or student

	// UI global variables
	//Project_DB_Publications_interactive01 pdb_pub_itv = null;
	private boolean UI_status; 
	private static final boolean RUN = true;
	private static final boolean STOP = false;
	private Scanner user_input = new Scanner(System.in);

	// UI format definition
	private static final String FIVE_SPACES = "     ";
	private static final String TEN_SPACES = "           ";
	private static final String H_LINE = "********************"; // 20 *
	private static final long DAY_IN_MS = 24 * 60 * 60 * 1000; // in milliseconds

	/* Initialize the database connection */
	public Project_DB_Notifications_faculty(String loginID)
	{
		// constructor to initialize the database system
		try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				System.out.println("Welcome to the Notifications section!");
			/*
				Load the driver. This creates an instance of the driver 
				and calls the registerDriver method to make Oracle Thin 
				driver available to clients. 
			*/ 
				System.out.println("Connecting the Database ...");
				conn = DriverManager.getConnection(jdbcURL, user, passwd);
				System.out.println("Connection succeed!");

				// create a statement object that will be sending your SQL
				// statements to the DBMS
				System.out.println("Creating a statement object ...");
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY);
				System.out.println("Statement initialized!");
				// Trigger the UI
				this.loginID = new String(loginID);
				UI_status = RUN;
				ui_event_loop();
			} catch (Throwable oops) {
				oops.printStackTrace();
			}
	}

	/* close all connections */
	public void databaseClose()
	{
		if (conn != null)
		{
			try {
				conn.close();
			} catch (Throwable whatever) {
				whatever.printStackTrace();
			}
		}

		if (stmt != null)
		{
			try {
				stmt.close();
			} catch (Throwable whatever) {
				whatever.printStackTrace();
			}
		}
	}

	/* print a new line */
	private void newLine()
	{
		System.out.println();
	}

	/* print a string */
	private void outPrint(String msg)
	{
		System.out.print(msg);
	}

	/* print a string and new line */
	private void outPrintln(String msg)
	{
		System.out.println(msg);
	}

	private String makeStars(int number)
	{	
		StringBuilder result = new StringBuilder();
		if (number > 0)
		{
			for (int i=0; i<number; i++)
			{
				result.append('*');
			}
		}
		return result.toString();
	}

	/* Deconstruct the UI */
	public void deinit_UI()
	{
		databaseClose();
	}

	/* UI event listener */
	public void ui_event_loop()
	{
		while (UI_status)
		{
			// display notification
			newLine();
			newLine();
			newLine();
			outPrintln(H_LINE + H_LINE + " Notifications " + H_LINE + H_LINE);
			newLine();
			// list items near due date
			ui_listDueItems();
			newLine();
			outPrintln("0. Cancel");
			newLine();
			outPrintln(H_LINE + H_LINE + makeStars(" Notifications ".length()) + H_LINE + H_LINE);
			outPrint("Input the selection: ");
			int choice = user_input.nextInt();
			switch(choice)
			{
				case 0 :
					outPrintln("Cancel");
					newLine();
					UI_status = STOP;
					break;
				default:
						outPrintln("Invalid selection!");
			} // end switch
		} // end while
		deinit_UI(); // shut down UI with cleaning
	}

	/* list items near due date */
	public void ui_listDueItems()
	{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
			SimpleDateFormat sdf_b = new SimpleDateFormat("MM/dd/yy");
			Date curDate = new Date();
			Date after3Days = new Date(curDate.getTime() + 3*DAY_IN_MS);
			Date after2Days = new Date(curDate.getTime() + 2*DAY_IN_MS);
			Date after1Days = new Date(curDate.getTime() + 1*DAY_IN_MS);
			Date before30Days = new Date(curDate.getTime() - 30*DAY_IN_MS);
			Date before60Days = new Date(curDate.getTime() - 60*DAY_IN_MS);
			Date before90Days = new Date(curDate.getTime() - 90*DAY_IN_MS);
			outPrintln("Items Due in 3 Days: ");
			newLine();
			ResultSet rs_fbookborrow = stmt.executeQuery("SELECT * FROM books B, fbookborrow BR WHERE BR.etime <=  to_date('" + sdf.format(after3Days) + "', 'mm/dd/yy hh24:mi') AND BR.etime > to_date('" + sdf.format(after2Days) + "', 'mm/dd/yy hh24:mi') AND BR.bid  = B.bid AND BR.fid = '" + loginID + "'");
			if (!rs_fbookborrow.next())
			{
				outPrintln(FIVE_SPACES + "No items!");
			}
			rs_fbookborrow.previous();
			int countIndex = 1;
			while (rs_fbookborrow.next())
			{
				String show_btitle = rs_fbookborrow.getString("btitle");
				Date dueDate = rs_fbookborrow.getTimestamp("etime");
				outPrintln(FIVE_SPACES + countIndex + " - " + show_btitle);
				outPrintln(TEN_SPACES + "Due Time: " + sdf_b.format(dueDate));
				countIndex++;
			}
			newLine();
			rs_fbookborrow.close();
			outPrintln("Items Due in 1 Days: ");
			newLine();
			rs_fbookborrow = stmt.executeQuery("SELECT * FROM books B, fbookborrow BR WHERE BR.etime <=  to_date('" + sdf.format(after1Days) + "', 'mm/dd/yy hh24:mi') AND BR.etime > to_date('" + sdf.format(curDate) + "', 'mm/dd/yy hh24:mi') AND BR.bid  = B.bid AND BR.fid = '" + loginID + "'");
			if (!rs_fbookborrow.next())
			{
				outPrintln(FIVE_SPACES + "No items!");
			}
			rs_fbookborrow.previous();
			countIndex = 1;
			while (rs_fbookborrow.next())
			{
				String show_btitle = rs_fbookborrow.getString("btitle");
				Date dueDate = rs_fbookborrow.getTimestamp("etime");
				outPrintln(FIVE_SPACES + countIndex + " - " + show_btitle);
				outPrintln(TEN_SPACES + "Due Time: " + sdf_b.format(dueDate));
				countIndex++;
			}
			newLine();
			rs_fbookborrow.close();
			outPrintln("Items Past Due for 30 Days: ");
			newLine();
			rs_fbookborrow = stmt.executeQuery("SELECT * FROM books B, fbookborrow BR WHERE BR.etime <=  to_date('" + sdf.format(before30Days) + "', 'mm/dd/yy hh24:mi') AND BR.etime > to_date('" + sdf.format(before60Days) + "', 'mm/dd/yy hh24:mi') AND BR.bid  = B.bid AND BR.fid = '" + loginID + "'");
			if (!rs_fbookborrow.next())
			{
				outPrintln(FIVE_SPACES + "No items!");
			} 
			rs_fbookborrow.previous(); 
			countIndex = 1;
			while (rs_fbookborrow.next())
			{
				String show_btitle = rs_fbookborrow.getString("btitle");
				Date dueDate = rs_fbookborrow.getTimestamp("etime");
				outPrintln(FIVE_SPACES + countIndex + " - " + show_btitle);
				outPrintln(TEN_SPACES + "Due Time: " + sdf_b.format(dueDate));
				countIndex++;
			}
			newLine();
			rs_fbookborrow.close();
			outPrintln("Items Past Due for 60 Days: ");
			newLine();
			rs_fbookborrow = stmt.executeQuery("SELECT * FROM books B, fbookborrow BR WHERE BR.etime <=  to_date('" + sdf.format(before60Days) + "', 'mm/dd/yy hh24:mi') AND BR.etime > to_date('" + sdf.format(before90Days) + "', 'mm/dd/yy hh24:mi') AND BR.bid  = B.bid AND BR.fid = '" + loginID + "'");
			if (!rs_fbookborrow.next())
			{
				outPrintln(FIVE_SPACES + "No items!");
			}
			rs_fbookborrow.previous();
			countIndex = 1;
			while (rs_fbookborrow.next())
			{
				String show_btitle = rs_fbookborrow.getString("btitle");
				Date dueDate = rs_fbookborrow.getTimestamp("etime");
				outPrintln(FIVE_SPACES + countIndex + " - " + show_btitle);
				outPrintln(TEN_SPACES + "Due Time: " + sdf_b.format(dueDate));
				countIndex++;
			}
			newLine();
			rs_fbookborrow.close();
			outPrintln("Items Past Due for 90 Days: ");
			newLine();
			rs_fbookborrow = stmt.executeQuery("SELECT * FROM books B, fbookborrow BR WHERE BR.etime <=  to_date('" + sdf.format(before90Days) + "', 'mm/dd/yy hh24:mi') AND BR.bid  = B.bid AND BR.fid = '" + loginID + "'");
			if (!rs_fbookborrow.next())
			{
				outPrintln(FIVE_SPACES + "No items!");
			}
			rs_fbookborrow.previous();
			countIndex = 1;
			while (rs_fbookborrow.next())
			{
				String show_btitle = rs_fbookborrow.getString("btitle");
				Date dueDate = rs_fbookborrow.getTimestamp("etime");
				outPrintln(FIVE_SPACES + countIndex + " - " + show_btitle);
				outPrintln(TEN_SPACES + "Due Time: " + sdf_b.format(dueDate));
				countIndex++;
			}
			rs_fbookborrow.close();
			newLine();
			outPrintln("Camera Not Returned: ");
			ResultSet rs_fcamborrow = stmt.executeQuery("SELECT * FROM fcamborrow WHERE etime <  to_date('" + sdf.format(curDate) + "', 'mm/dd/yy hh24:mi') AND fid = '" + loginID + "'");
			if (rs_fcamborrow.next())
			{
				newLine();
				outPrintln(FIVE_SPACES + "Cameras were not returned on due. You are charged by late penalty!");
			} 
			else
			{
				newLine();
				outPrintln(FIVE_SPACES + "No items!");
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		Project_DB_Notifications_faculty pdb_noti_fct = new Project_DB_Notifications_faculty("F1");
	}
}