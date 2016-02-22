/*
*	This program implements the CSC540 Project Database Books.
* 	This program will create tables, insert data, and test query.
* 	by Shijie Li
* 	Use functions to implement UI
*	Faculty priority not considered here.
*/
import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.Date;
import java.text.*;

public class Project_DB_Publications_faculty {
	
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
	public Project_DB_Publications_faculty(String loginID)
	{
		// constructor to initialize the database system
		try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				System.out.println("Welcome to the Publication section!");
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

	/* insert data into database
	*	@param tname, values for tablename and value inserted;
	*/
	public void insertData(String tname, String values, String conditions)
	{
		try {
				stmt.executeUpdate("INSERT INTO " + tname + " VALUES (" + values + ")");
				System.out.println("Data insertion complete!");
		} catch (Throwable ex) {
			ex.printStackTrace();
		}	
	}

	/*
	*	query search in table Books with conditions
	*	@param conditions;
	*/
	public ResultSet searchBooks(String conditions)
	{
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("SELECT * FROM Books WHERE " + conditions);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return rs;
	}	

	/*
	*	query search in table BookReserve with conditions
	*	@param conditions;
	*/
	public ResultSet searchBookReserve(String conditions)
	{
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("SELECT * FROM BookReserve WHERE " + conditions);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return rs;
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

	/* Initialize the UI */
	public void init_UI()
	{
		// Update the due date info by turning on otmark if necessary
		try {
			Date curDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
			ResultSet rs_fbookborrow_due = stmt.executeQuery("SELECT * FROM fBookBorrow WHERE etime < to_date('" + sdf.format(curDate) + "', 'mm/dd/yy hh24:mi')");
			ArrayList<Integer> show_fb_bid = new ArrayList<Integer>();
			while (rs_fbookborrow_due.next()) 
			{
				show_fb_bid.add(rs_fbookborrow_due.getInt("fb_bid"));
			}
			rs_fbookborrow_due.close();
			for (int i=0; i<show_fb_bid.size(); i++)
			{
				stmt.executeUpdate("UPDATE fBookBorrow SET otmark = 1 WHERE fb_bid = " + show_fb_bid.get(i));
			}
			// check if it pass 90 days over due
			Date before90Days = new Date(curDate.getTime() - 90*DAY_IN_MS);
			rs_fbookborrow_due = stmt.executeQuery("SELECT * FROM fBookBorrow WHERE etime < to_date('" + sdf.format(before90Days) + "', 'mm/dd/yy hh24:mi')");
			HashSet<String> show_fid = new HashSet<String>();
			while (rs_fbookborrow_due.next()) 
			{
				show_fid.add(rs_fbookborrow_due.getString("fid"));
			}
			rs_fbookborrow_due.close();
			for (String fid: show_fid)
			{
				stmt.executeUpdate("UPDATE fAccount SET otmark = 1 WHERE fid = '" + fid + "'");
			}
			outPrintln("Data Updated!");
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		// display UI menu 
		UI_status = RUN;
		newLine();
		newLine();
		newLine();
		outPrintln(H_LINE + H_LINE + " Publications " + H_LINE + H_LINE);
		newLine();
		outPrintln("1. Books");
		outPrintln("2. eBooks");
		outPrintln("3. Journals");
		outPrintln("4. Conference Papers");
		outPrintln("0. Cancel");
		newLine();
		outPrintln(H_LINE + H_LINE + makeStars(" Publications ".length()) + H_LINE + H_LINE);
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
			init_UI(); // main menu for Books
			newLine();
			outPrint("Input the selection: ");
			int choice = user_input.nextInt();
			switch(choice)
			{
				case 1 :
					outPrintln("Books");
					ui_listBooks();
					break;
				case 2 :
					outPrintln("eBooks");
					ui_listEBooks();
					break;
				case 3 :
					outPrintln("Journals");
					ui_listJournal();
					break;
				case 4 :
					outPrintln("Conference Papers");
					ui_listConference();
					break;
				case 0 :
					outPrintln("Cancel");
					newLine();
				default: 
					UI_status = STOP;
			} // end switch
		} // end while loop
		deinit_UI(); // shut down UI with cleaning
	}

	/* List all the regular books for checkout */
	public void ui_listBooks()
	{
		// listen to user selection
		boolean UI_listBooks_status = RUN;
		while (UI_listBooks_status)
		{
			// display all physical books as menu
			newLine();
			newLine();
			newLine();
			outPrintln(H_LINE + H_LINE + " Books " + H_LINE + H_LINE);
				// Search and list all books in database
			int countIndex = 0; // index marker
			try {
				ResultSet rs_books = searchBooks("btype = \'HARDCOPY\'");
				while (rs_books.next())
				{
					countIndex++;
					String show_btitle = rs_books.getString("btitle");
					outPrintln(countIndex + ". " + show_btitle);
				}
				rs_books.close();
			} catch (Throwable ex) {
				ex.printStackTrace();
			} // end outer try
			outPrintln("0. Cancel");
			newLine();
			outPrintln(H_LINE + H_LINE + makeStars(" Books ".length()) + H_LINE + H_LINE);
			outPrint("Input the selection: ");
			int choice = user_input.nextInt(); // select the book index in the list
			switch(choice)
			{	
				case 0 :
					outPrintln("Cancel");
					newLine();
					UI_listBooks_status = STOP;
					break;
				default: 
					if (choice > countIndex)
					{
						outPrintln("Invalid selection!");
					}
					else
					{
						ui_bookDetail(choice);
					}
			} // end switch
		} // end while loop 
	}

	/* display the detail of selected book */
	public void ui_bookDetail(int bookIndex)
	{
		// listen to the user selection
		boolean UI_bookDetail_status = RUN;
		try {
			while (UI_bookDetail_status)
			{
				// display book info 
				newLine();
				outPrintln(H_LINE + H_LINE + " Book Info " + H_LINE + H_LINE);
				ResultSet rs_books = searchBooks("btype = \'HARDCOPY\'");
				rs_books.absolute(bookIndex); // move cursor in database
				int show_bid = rs_books.getInt("bid");
				String show_bisbn = rs_books.getString("bisbn");
				String show_btitle = rs_books.getString("btitle");
				int show_bedition = rs_books.getInt("bedition");
				String show_bauthor = rs_books.getString("bauthor");
				int  show_byear = rs_books.getInt("bpubyear");
				String show_bpublisher = rs_books.getString("bpub");
				int show_bquantity = rs_books.getInt("bquantity");
				String show_blocation = rs_books.getString("blocation");
				rs_books.close(); // close and clean
				// print book info			
				newLine();
				outPrintln("Title: " + show_btitle + FIVE_SPACES + "(" + show_byear + ")");
				outPrintln("ISBN: " + show_bisbn + "");
				outPrintln("Edition: " + show_bedition);
				outPrintln("Author: " + show_bauthor);
				outPrintln("Published by " + show_bpublisher);
				outPrintln("Quantity: " + show_bquantity);
				outPrintln("Location: " + show_blocation);
				newLine();
				outPrintln("1. Request");
				outPrintln("2. Reserve");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Book Info ".length()) + H_LINE + H_LINE);
				outPrint("Input the selection: ");
				int choice = user_input.nextInt();
				switch(choice)
				{
					case 0 : 
						outPrintln("Cancel");
						newLine();
						UI_bookDetail_status = STOP;
						break;
					case 1 :
						boolean requestResult = ui_bookRequest(show_bid);
						if (requestResult)
						{
							outPrintln("Request Successful!");
						}
						else 
						{
							outPrintln("Request Failed!");
						}
						break;
					case 2 : 
						boolean reserveResult = ui_bookReserve(show_bid);
						if (reserveResult)
						{
							outPrintln("Reserve Successful!");
						} 
						else 
						{
							outPrintln("Reserve failed!");
						}
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while loop
		} catch (Throwable ex) {
			ex.printStackTrace();	
		} 
	}

	/* Book request or renewal with user info */
	public boolean ui_bookRequest(int bookID)
	{
		boolean requestResult = false;
		try {
			// listen to user selection
			boolean UI_bookRequest_status = RUN;
			while (UI_bookRequest_status)
			{
				newLine();
				outPrintln(H_LINE + H_LINE + " Book Request " + H_LINE + H_LINE);
				boolean available = checkAvailability(bookID); // check if book reserved or currently available
				if (available)
				{
					outPrintln("Item is currently available.");
					outPrintln("You can check it out!");
					outPrintln("1. Request");
					outPrintln("0. Cancel");
					newLine();
					outPrintln(H_LINE + H_LINE + makeStars(" Book Request ".length()) + H_LINE + H_LINE);
					newLine();
					outPrint("Input the selection: ");
					int choice = user_input.nextInt();
					switch (choice) 
					{
						case 0 : 
							outPrintln("Cancel");
							newLine();
							UI_bookRequest_status = STOP;
							break;
						case 1 :
							outPrintln("Request!");
							newLine();
							requestResult = registerRequest(bookID);
							UI_bookRequest_status = STOP;
							break;
						default:
							outPrintln("Invalid selection!");
					} // end switch
				}
				else 
				{
					outPrintln("Item is reserved and not available currently.");
					outPrintln("0. Cancel");
					newLine();
					outPrintln(H_LINE + H_LINE + makeStars(" Book Request ".length()) + H_LINE + H_LINE);
					newLine();
					outPrint("Input the selection: ");
					int choice = user_input.nextInt();
					switch (choice) 
					{
						case 0 : 
							outPrintln("Cancel");
							newLine();
							UI_bookRequest_status = STOP;
							break;
						default:
							outPrintln("Invalid selection!");
					} // end switch
				}
			} // end while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}	
		return requestResult;
	}

	/* check if this book is reserved for course */
	public boolean checkAvailability(int bookID)
	{
		boolean checkResult = false;
		// check if it is in BookReserve
		try {
			ResultSet rs_bookreserve = stmt.executeQuery("SELECT BR.* from BookReserve BR, Books B WHERE BR.BID = B.BID AND B.BID = " + bookID);
			if (!rs_bookreserve.next())
			{
				checkResult = true;
			} 
			else 
			{
				outPrintln("The book is reserved.");
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		} // end try
		return checkResult;
	}

	/* register info for book borrow */
	public boolean registerRequest(int bookID)
	{
		boolean registerResult = false;
		boolean UI_registerRequest_status = RUN;
		try {
			while (UI_registerRequest_status)
			{
				newLine();
				outPrintln(H_LINE + H_LINE + " Register Request " + H_LINE + H_LINE);
				outPrintln("1. Input Register Info");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Register Request ".length()) + H_LINE + H_LINE);
				newLine();
				outPrint("Input the selection: ");
				int choice = user_input.nextInt();
				switch (choice)
				{
					case 1 :
						int in_fb_bid = 1;
						ResultSet rs_fbookborrow = stmt.executeQuery("SELECT * FROM fbookborrow");
						while (rs_fbookborrow.next())
						{
							in_fb_bid++; // count rows in fbookborrow
						}
						rs_fbookborrow.close();
						user_input = new Scanner(System.in);
						outPrintln("Input the checkout and return date. ");
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
						outPrintln("check out date(mm/dd/yy HH:mi): ");
						Date in_stime = sdf.parse(user_input.next());
						outPrintln("return date(mm/dd/yy HH:mi): ");
						Date in_etime = sdf.parse(user_input.next());
						// check date availability in fbookborrow
						rs_fbookborrow = stmt.executeQuery("SELECT * FROM fbookborrow WHERE (stime <= to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi') AND etime > to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi')) OR (stime <= to_date('" + sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi') AND etime > to_date('" + sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi'))");
						// check date availability in sbookborrow
						ResultSet rs_sbookborrow = stmt.executeQuery("SELECT * FROM sbookborrow WHERE (stime <= to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi') AND etime > to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi')) OR (stime <= to_date('" + sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi') AND etime > to_date('" + sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi'))");
						// check availability of the book quantity
						ResultSet rs_books = stmt.executeQuery("SELECT * FROM books WHERE bid = " + bookID);
						int numOfBooks = 0;
						if (rs_books.next())
						{
							numOfBooks = rs_books.getInt("bquantity");
						}
						double diffDays = (double)(in_etime.getTime() - in_stime.getTime()) / (60.0 * 60.0 * 1000 * 24);
						// decision of book request
						if (rs_fbookborrow.next() || rs_sbookborrow.next() || (numOfBooks<1))
						{
							outPrintln("Date not available!");
							outPrintln("Date not available!");
							ResultSet rs_fbookwl = stmt.executeQuery("SELECT * FROM fbookwl");
							int in_fbwl_id = 1;
							while (rs_fbookwl.next())
							{
								in_fbwl_id++;
							}
							rs_fbookwl.close();
							stmt.executeUpdate("INSERT INTO fbookwl VALUES (" + in_fbwl_id + ", '" + loginID + "', " + bookID + ")");
							outPrintln("You are added to the waitlist!");
							registerResult = true;
						}
						else if (diffDays > 30)
						{
							outPrintln("Date not available!");
							outPrintln("Duration should be within 1 month, and not overlap with other dates.");
						}
						else
						{
							String in_blocation = "N/A";
							stmt.executeUpdate("INSERT INTO fBookBorrow VALUES (" + in_fb_bid + ", '" + loginID + "', " + bookID + ", '" + in_blocation + "', to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi'), to_date('"+ sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi'), " + "0)");
							numOfBooks--;
							stmt.executeUpdate("UPDATE books SET bquantity = " + numOfBooks + " WHERE bid = " + bookID);
							registerResult = true;
							UI_registerRequest_status = STOP;
						}
						rs_books.close();
						rs_sbookborrow.close();
						rs_fbookborrow.close();
						break;
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_registerRequest_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return registerResult;
	}

	/* Book reserve for a course */
	public boolean ui_bookReserve(int bookID)
	{
		boolean reserveResult = false;
		try {
			// listen to user selection
			boolean UI_bookReserve_status = RUN;
			while (UI_bookReserve_status)
			{
				newLine();
				outPrintln(H_LINE + H_LINE + " Book Reserve " + H_LINE + H_LINE);
				boolean available = checkAvailability(bookID); // check if book reserved or currently available
				if (available)
				{
					outPrintln("Item is currently available.");
					outPrintln("You can reserve it!");
					outPrintln("1. Reserve");
					outPrintln("0. Cancel");
					newLine();
					outPrintln(H_LINE + H_LINE + makeStars(" Book Reserve ".length()) + H_LINE + H_LINE);
					newLine();
					outPrint("Input the selection: ");
					int choice = user_input.nextInt();
					switch (choice) 
					{
						case 0 : 
							outPrintln("Cancel");
							newLine();
							UI_bookReserve_status = STOP;
							break;
						case 1 :
							outPrintln("Reserve!");
							newLine();
							reserveResult = registerReserve(bookID);;
							UI_bookReserve_status = STOP;
							break;
						default:
							outPrintln("Invalid selection!");
					} // end switch
				}
				else 
				{
					outPrintln("Item is not available currently.");
					//outPrintln("You will be added to Waitlist once admitted!");
					outPrintln("You can not reserve it!");
					//outPrintln("1. Reserve");
					outPrintln("0. Cancel");
					newLine();
					outPrintln(H_LINE + H_LINE + makeStars(" Book Reserve ".length()) + H_LINE + H_LINE);
					newLine();
					outPrint("Input the selection: ");
					int choice = user_input.nextInt();
					switch (choice) 
					{
						case 0 : 
							outPrintln("Cancel");
							newLine();
							UI_bookReserve_status = STOP;
							break;
						default:
							outPrintln("Invalid selection!");
					} // end switch
				}
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return reserveResult;
	}

	/* register info for book reserve */
	public boolean registerReserve(int bookID)
	{
		boolean registerResult = false;
		boolean UI_registerReserve_status = RUN;
		try {
			while (UI_registerReserve_status)
			{	
				newLine();
				outPrintln(H_LINE + H_LINE + " Register Reserve " + H_LINE + H_LINE);
				outPrintln("1. Input Register Info");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Register Reserve ".length()) + H_LINE + H_LINE);
				newLine();
				outPrint("Input the selection: ");
				int choice = user_input.nextInt();
				switch (choice)
				{
					case 1 :
						int in_br_id = 1;
						ResultSet rs_bookreserve = stmt.executeQuery("SELECT * FROM BookReserve");
						while (rs_bookreserve.next())
						{
							in_br_id++;
						}
						rs_bookreserve.close();
						outPrintln("Input the course ID to reserve the book: ");
						String in_cid = user_input.next();
						// check if the info is authentic
						ResultSet rs_enrollment = stmt.executeQuery("SELECT * FROM Enrollment WHERE fid = '" + loginID + "' AND cid = '" + in_cid + "'");
						if (rs_enrollment.next())
						{
							stmt.executeUpdate("INSERT INTO BookReserve VALUES (" + in_br_id + ", '" + in_cid + "', " + bookID + ")");
							registerResult = true;
							UI_registerReserve_status = STOP;
						}
						else
						{
							outPrintln("You are not in that course. Reservation unavailable.");
						}
						rs_enrollment.close();
						break;
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_registerReserve_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return registerResult;
	}

	/* List all the ebook */
	public void ui_listEBooks()
	{
		// listen to user selection
		boolean UI_listEBooks_status = RUN;
		while (UI_listEBooks_status)
		{
			// display all e-books as menu
			newLine();
			newLine();
			newLine();
			outPrintln(H_LINE + H_LINE + " E-Books " + H_LINE + H_LINE);
				// Search and list all e-books in database
			int countIndex = 0; // index marker
			try {
				ResultSet rs_Ebooks = searchBooks("btype = \'E-BOOK\'");
				while (rs_Ebooks.next())
				{
					countIndex++;
					String show_btitle = rs_Ebooks.getString("btitle");
					outPrintln(countIndex + ". " + show_btitle);
				} // end inner while
				rs_Ebooks.close();
			} catch (Throwable ex) {
				ex.printStackTrace();
			} // end outer try
			outPrintln("0. Cancel");
			newLine();
			outPrintln(H_LINE + H_LINE + makeStars(" E-Books ".length()) + H_LINE + H_LINE);
			outPrint("Input the selection: ");
			int choice = user_input.nextInt(); // select the e-book index in the list
			switch(choice)
			{	

				case 0 :
					outPrintln("Cancel");
					newLine();
					UI_listEBooks_status = STOP;
					break;
				default: 
					if (choice > countIndex)
					{
						outPrintln("Invalid selection!");
					}
					else
					{
						ui_EbookDetail(choice);
					}
			} // end switch
		} // end while loop 
	}

	/* display the detail of selected e-book */
	public void ui_EbookDetail(int bookIndex)
	{
		// listen to the user selection
		boolean UI_EbookDetail_status = RUN;
		try {
			while (UI_EbookDetail_status)
			{
				// display book info
				newLine();
				outPrintln(H_LINE + H_LINE + " E-Books Info " + H_LINE + H_LINE);
				ResultSet rs_Ebooks = searchBooks("btype = \'E-BOOK\'");
				rs_Ebooks.absolute(bookIndex);
				int show_bid = rs_Ebooks.getInt("bid");
				String show_bisbn = rs_Ebooks.getString("bisbn");
				String show_btitle = rs_Ebooks.getString("btitle");
				int show_bedition = rs_Ebooks.getInt("bedition");
				String show_bauthor = rs_Ebooks.getString("bauthor");
				int  show_byear = rs_Ebooks.getInt("bpubyear");
				String show_bpublisher = rs_Ebooks.getString("bpub");
				int show_bquantity = rs_Ebooks.getInt("bquantity");
				String show_blocation = rs_Ebooks.getString("blocation");
				rs_Ebooks.close(); // close and clean
				// print book info			
				newLine();
				outPrintln("Title: " + show_btitle + FIVE_SPACES + "(" + show_byear + ")");
				outPrintln("ISBN: " + show_bisbn + "");
				outPrintln("Edition: " + show_bedition);
				outPrintln("Author: " + show_bauthor);
				outPrintln("Published by " + show_bpublisher);
				outPrintln("Quantity: " + show_bquantity);
				outPrintln("Location: " + show_blocation);
				newLine();
				outPrintln("1. Request");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" E-Books Info ".length()) + H_LINE + H_LINE);
				outPrint("Input the selection: ");
				int choice = user_input.nextInt(); // select the e-book index in the list
				switch(choice)
				{
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_EbookDetail_status = STOP;
						break;
					case 1 :
						boolean requestResult = ui_EbookRequest(show_bid);
						if (requestResult)
						{
							outPrintln("Request Successful!");
						}
						else 
						{
							outPrintln("Request Failed!");
						}
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/* e-book request as a confirmation */
	public boolean ui_EbookRequest(int bookID)
	{
		boolean requestResult = false;
		try {
			boolean UI_EbookRequest_status = RUN;
			while (UI_EbookRequest_status)
			{
				newLine();
				outPrintln(H_LINE + H_LINE + " E-Book Request " + H_LINE + H_LINE);
				outPrintln("1. Request");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Book Request ".length()) + H_LINE + H_LINE);
				newLine();
				outPrint("Input the selection: ");
				int choice = user_input.nextInt();
				switch (choice) 
				{
					case 0 : 
						outPrintln("Cancel");
						newLine();
						UI_EbookRequest_status = STOP;
						break;
					case 1 :
						outPrintln("Request!");
						newLine();
						int in_fb_bid = 1;
						ResultSet rs_fbookborrow = stmt.executeQuery("SELECT * FROM fbookborrow");
						while (rs_fbookborrow.next())
						{
							in_fb_bid++; // count rows in fbookborrow
						}
						rs_fbookborrow.close();
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
						Date in_stime = new Date();
						String in_blocation = "N/A";
						stmt.executeUpdate("INSERT INTO fBookBorrow VALUES (" + in_fb_bid + ", '" + loginID + "', " + bookID + ", '" + in_blocation + "', to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi'), NULL, " + "0)");
						requestResult = true;
						UI_EbookRequest_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return requestResult;
	}

	/* list all the journal */
	public void ui_listJournal()
	{
		boolean UI_listJournal_status = RUN;
		while (UI_listJournal_status)
		{
			// display all journals as menu
			newLine();
			newLine();
			newLine();
			outPrintln(H_LINE + H_LINE + " Journal List " + H_LINE + H_LINE);
			// search and list all books in database
			int countIndex = 0; // index marker
			try {
				ResultSet rs_journal = stmt.executeQuery("SELECT * FROM journal");
				while (rs_journal.next())
				{
					countIndex++;
					String show_jtitle = rs_journal.getString("jtitle");
					outPrintln(countIndex + ". " + show_jtitle);
				} // end inner while
				rs_journal.close();
			} catch (Throwable ex) {
				ex.printStackTrace();
			} 
			outPrintln("0. Cancel");
			newLine();
			outPrintln(H_LINE + H_LINE + makeStars(" Journal List  ".length()) + H_LINE + H_LINE);
			outPrintln("Input the selection: ");
			int choice = user_input.nextInt(); // select journal index
			switch (choice)
			{
				case 0 :
					outPrintln("Cancel");
					newLine();
					UI_listJournal_status = STOP;
					break;
				default: 
					if (choice > countIndex)
					{
						outPrintln("Invalid selection!");
					}
					else
					{
						ui_journalDetail(choice);
					} 
			} // end switch
		} // end outer while 
	}

	/* display journal detail */ 
	public void ui_journalDetail(int journalIndex)
	{
		boolean UI_journalDetail_status = RUN;
		try {
			while (UI_journalDetail_status)
			{
				// display journal info 
				newLine();
				outPrintln(H_LINE + H_LINE + " Journal Info " + H_LINE + H_LINE);
				ResultSet rs_journal = stmt.executeQuery("SELECT * FROM journal");
				rs_journal.absolute(journalIndex); // move cursor
				int show_jid = rs_journal.getInt("jid");
				String show_jissn = rs_journal.getString("jissn"); 
				String show_jtitle = rs_journal.getString("jtitle");
				String show_jauthor = rs_journal.getString("jauthor");
				int show_jpubyear = rs_journal.getInt("jpubyear");
				rs_journal.close();
				// print journal info
				newLine();
				outPrintln("Title: " + show_jtitle + FIVE_SPACES + "(" + show_jpubyear + ")");
				outPrintln("ISSN: " + show_jissn);
				outPrintln("Author: " + show_jauthor);
				newLine();
				outPrintln("1. Request");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Journal Info  ".length()) + H_LINE + H_LINE);
				outPrintln("Input the selection: ");
				int choice = user_input.nextInt(); // select journal index
				switch (choice)
				{
					case 1 :
						boolean requestResult = ui_journalRequest(show_jid);
						if (requestResult)
						{
							outPrintln("Request Successful!");
						}
						else
						{
							outPrintln("Request Failed!");
						}
						break;
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_journalDetail_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/* register journal request */
	public boolean ui_journalRequest(int journalID)
	{
		boolean requestResult = false;
		try {
			// listen to user selection
			boolean UI_journalRequest_status = RUN;
			while (UI_journalRequest_status)
			{
				newLine();
				outPrintln(H_LINE + H_LINE + " Journal Request " + H_LINE + H_LINE);
				outPrintln("1. Request");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Journal Request  ".length()) + H_LINE + H_LINE);
				outPrintln("Input the selection: ");
				int choice = user_input.nextInt(); // select journal index
				switch (choice)
				{
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_journalRequest_status = STOP;
						break;
					case 1 :
						outPrintln("Request");
						newLine();
						requestResult = registerJournalRequest(journalID);
						UI_journalRequest_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return requestResult;
	}

	/* register info for journal borrow */
	public boolean registerJournalRequest(int journalID)
	{
		boolean registerResult = false;
		boolean UI_registerJournalRequest_status = RUN;
		try {
			while (UI_registerJournalRequest_status)
			{
				newLine();
				outPrintln(H_LINE + H_LINE + " Register Request " + H_LINE + H_LINE);
				outPrintln("1. Input Register Info");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Register Request ".length()) + H_LINE + H_LINE);
				newLine();
				outPrint("Input the selection: ");
				int choice = user_input.nextInt();
				switch (choice)
				{
					case 1 : 
						int in_fj_jid = 1;
						ResultSet rs_fjournalborrow = stmt.executeQuery("SELECT * FROM fjournalborrow");
						while (rs_fjournalborrow.next())
						{
							in_fj_jid++; // count rows in fjournalborrow
						}
						rs_fjournalborrow.close();
						user_input = new Scanner(System.in);
						outPrintln("Input the checkout and return date. ");
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
						outPrintln("check out date(mm/dd/yy HH:mi): ");
						Date in_stime = sdf.parse(user_input.nextLine());
						outPrintln("return date(mm/dd/yy HH:mi): ");
						Date in_etime = sdf.parse(user_input.nextLine());
						double diffHours = (double)(in_etime.getTime() - in_stime.getTime()) / (60.0 * 60.0 * 1000);
						// check date availability in fjournalborrow
						rs_fjournalborrow = stmt.executeQuery("SELECT * FROM fjournalborrow WHERE (stime <= to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi') AND etime > to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi')) OR (stime <= to_date('" + sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi') AND etime > to_date('" + sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi'))");
						if (rs_fjournalborrow.next() || (diffHours > 12))
						{
							outPrintln("Date not available!");
							outPrintln("Duration should be within 12 hours, and not overlap with other dates.");
						}
						else
						{
							String in_blocation = "N/A";
							stmt.executeUpdate("INSERT INTO fJournalBorrow VALUES (" + in_fj_jid + ", '" + loginID + "', " + journalID + ", '" + in_blocation + "', to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi'), to_date('"+ sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi'), " + "0)");
							registerResult = true;
							UI_registerJournalRequest_status = STOP;
						}
						break;
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_registerJournalRequest_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return registerResult;
	}

	/* list all the conference paper */
	public void ui_listConference()
	{
		boolean UI_listConference_status = RUN;
		while (UI_listConference_status)
		{
			// display all conference paper as menu
			newLine();
			newLine();
			newLine();
			outPrintln(H_LINE + H_LINE + " Conference Proceedings List " + H_LINE + H_LINE);
			// search and list all books in database
			int countIndex = 0; // index marker
			try {
				ResultSet rs_conference = stmt.executeQuery("SELECT * FROM conference");
				while (rs_conference.next())
				{
					countIndex++;
					String show_confname = rs_conference.getString("confname");
					outPrintln(countIndex + ". " + show_confname);
				} // end inner while
				rs_conference.close();
			} catch (Throwable ex) {
				ex.printStackTrace();
			} 
			outPrintln("0. Cancel");
			newLine();
			outPrintln(H_LINE + H_LINE + makeStars(" Conference Proceedings List  ".length()) + H_LINE + H_LINE);
			outPrintln("Input the selection: ");
			int choice = user_input.nextInt(); // select journal index
			switch (choice)
			{
				case 0 :
					outPrintln("Cancel");
					newLine();
					UI_listConference_status = STOP;
					break;
				default: 
					if (choice > countIndex)
					{
						outPrintln("Invalid selection!");
					}
					else
					{
						ui_conferenceDetail(choice);
					} 
			} // end switch
		} // end outer while 
	}

	/* display conference paper detail */ 
	public void ui_conferenceDetail(int conferenceIndex)
	{
		boolean UI_conferenceDetail_status = RUN;
		try {
			while (UI_conferenceDetail_status)
			{
				// display journal info 
				newLine();
				outPrintln(H_LINE + H_LINE + " Conference Proceedings Info " + H_LINE + H_LINE);
				ResultSet rs_conference = stmt.executeQuery("SELECT * FROM conference");
				rs_conference.absolute(conferenceIndex); // move cursor
				int show_cfid = rs_conference.getInt("cfid");
				String show_confnum = rs_conference.getString("confnum"); 
				String show_conftitle = rs_conference.getString("conftitle");
				String show_confname = rs_conference.getString("confname");
				String show_confauthor = rs_conference.getString("confauthor");
				int show_confpubyear = rs_conference.getInt("confpubyear");
				rs_conference.close();
				// print journal info
				newLine();
				outPrintln("NAME: " + show_confname + FIVE_SPACES + "(" + show_confpubyear + ")");
				outPrintln("Title: " + show_conftitle);
				outPrintln("CONFERENCE NUMBER: " + show_confnum);
				outPrintln("Author: " + show_confauthor);
				newLine();
				outPrintln("1. Request");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Conference Proceedings Info  ".length()) + H_LINE + H_LINE);
				outPrintln("Input the selection: ");
				int choice = user_input.nextInt(); // select journal index
				switch (choice)
				{
					case 1 :
						boolean requestResult = ui_conferenceRequest(show_cfid);
						if (requestResult)
						{
							outPrintln("Request Successful!");
						}
						else
						{
							outPrintln("Request Failed!");
						}
						break;
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_conferenceDetail_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/* register conference proceeding request */
	public boolean ui_conferenceRequest(int conferenceID)
	{
		boolean requestResult = false;
		try {
			// listen to user selection
			boolean UI_conferenceRequest_status = RUN;
			while (UI_conferenceRequest_status)
			{
				newLine();
				outPrintln(H_LINE + H_LINE + " Conference Proceeding Request " + H_LINE + H_LINE);
				outPrintln("1. Request");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Conference Proceeding Request  ".length()) + H_LINE + H_LINE);
				outPrintln("Input the selection: ");
				int choice = user_input.nextInt(); // select journal index
				switch (choice)
				{
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_conferenceRequest_status = STOP;
						break;
					case 1 :
						outPrintln("Request");
						newLine();
						requestResult = registerConferenceRequest(conferenceID);
						UI_conferenceRequest_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return requestResult;
	}

	/* register info for conference proceeding borrow */
	public boolean registerConferenceRequest(int conferenceID)
	{
		boolean registerResult = false;
		boolean UI_registerConferenceRequest_status = RUN;
		try {
			while (UI_registerConferenceRequest_status)
			{
				newLine();
				outPrintln(H_LINE + H_LINE + " Register Request " + H_LINE + H_LINE);
				outPrintln("1. Input Register Info");
				outPrintln("0. Cancel");
				newLine();
				outPrintln(H_LINE + H_LINE + makeStars(" Register Request ".length()) + H_LINE + H_LINE);
				newLine();
				outPrint("Input the selection: ");
				int choice = user_input.nextInt();
				switch (choice)
				{
					case 1 : 
						int in_fc_bid = 1;
						ResultSet rs_fconfborrow = stmt.executeQuery("SELECT * FROM fconfborrow");
						while (rs_fconfborrow.next())
						{
							in_fc_bid++; // count rows in fconfborrow
						}
						rs_fconfborrow.close();
						user_input = new Scanner(System.in);
						outPrintln("Input the checkout and return date. ");
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
						outPrintln("check out date(mm/dd/yy HH:mi): ");
						Date in_stime = sdf.parse(user_input.nextLine());
						outPrintln("return date(mm/dd/yy HH:mi): ");
						Date in_etime = sdf.parse(user_input.nextLine());
						double diffHours = (double)(in_etime.getTime() - in_stime.getTime()) / (60.0 * 60.0 * 1000);
						// check date availability in fconfborrow
						rs_fconfborrow = stmt.executeQuery("SELECT * FROM fconfborrow WHERE (stime <= to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi') AND etime > to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi')) OR (stime <= to_date('" + sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi') AND etime > to_date('" + sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi'))");
						if (rs_fconfborrow.next() || (diffHours > 12))
						{
							outPrintln("Date not available!");
							outPrintln("Duration should be within 12 hours, and not overlap with other dates.");
						}
						else
						{
							String in_blocation = "N/A";
							stmt.executeUpdate("INSERT INTO fConfBorrow VALUES (" + in_fc_bid + ", '" + loginID + "', " + conferenceID + ", '" + in_blocation + "', to_date('" + sdf.format(in_stime) + "', 'mm/dd/yy hh24:mi'), to_date('"+ sdf.format(in_etime) + "', 'mm/dd/yy hh24:mi'), " + "0)");
							registerResult = true;
							UI_registerConferenceRequest_status = STOP;
						}
						break;
					case 0 :
						outPrintln("Cancel");
						newLine();
						UI_registerConferenceRequest_status = STOP;
						break;
					default:
						outPrintln("Invalid selection!");
				} // end switch
			} // end outer while
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return registerResult;
	}

	/* run UI in main */
	public static void main(String[] args)
	{
		Project_DB_Publications_faculty pdb_pub_itv = new Project_DB_Publications_faculty("F1");
	}
}