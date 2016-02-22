//check all waiting list table


import java.sql.*;

public class checkwl {

    static final String jdbcURL 
	= "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

    public static void checkwait(String userId) {
        try {

            // Load the driver. This creates an instance of the driver
	    // and calls the registerDriver method to make Oracle Thin
	    // driver available to clients.

            Class.forName("oracle.jdbc.driver.OracleDriver");

	    String user = "ychen71";	// For example, "jsmith"
	    String passwd = "200099159";	// Your 9 digit student ID number


            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;

            try {

		// Get a connection from the first driver in the
		// DriverManager list that recognizes the URL jdbcURL

		conn = DriverManager.getConnection(jdbcURL, user, passwd);

		// Create a statement object that will be sending your
		// SQL statements to the DBMS

		stmt = conn.createStatement();
		String sid = userId;
        System.out.println("Reserved Resources");
        //reserved book
		rs = stmt.executeQuery("SELECT * from sBookWL,Books where sBookWL.sid ='"+sid+"' and sBookWL.sb_bid = Books.bid");
        System.out.println("-------------------Reserved Books--------------------");
		while (rs.next()) {
            String sb_bid = rs.getString("sb_bid");
            String show_bisbn = rs.getString("bisbn");
			String show_btitle = rs.getString("btitle");
			int show_bedition = rs.getInt("bedition");
			String show_bauthor = rs.getString("bauthor");
			int  show_byear = rs.getInt("bpubyear");
			String show_bpublisher = rs.getString("bpub");
			String show_blocation = rs.getString("blocation");
            System.out.println(sid+" "+sb_bid+" "+show_bisbn+" "+show_btitle+" "+show_bedition+" "+show_bauthor+" "+show_bpublisher+" "+show_byear+" "+show_blocation);

		}

        //reserved camera
        rs = stmt.executeQuery("select * from Camera, sCamWL where sCamWL.sid ='"+sid+"' and sCamWL.cam_id=Camera.cam_id ");
        System.out.println("-------------------Reserved Camera--------------------");
         while(rs.next())
        {
            String cam_id = rs.getString("cam_id");
            String make = rs.getString("make");
            String model = rs.getString("model");
            String lensConfig = rs.getString("lensConfig");
            String memory = rs.getString("memory");
            String camLocation = rs.getString("camLocation");
            System.out.println(sid+" "+cam_id+" "+make+" "+model+" "+lensConfig+" "+memory+" "+camLocation);

        }








            } finally {
                close(rs);
                close(stmt);
                close(conn);
            }
        } catch(Throwable oops) {
            oops.printStackTrace();
        }
    }

    static void close(Connection conn) {
        if(conn != null) {
            try { conn.close(); } catch(Throwable whatever) {}
        }
    }

    static void close(Statement st) {
        if(st != null) {
            try { st.close(); } catch(Throwable whatever) {}
        }
    }

    static void close(ResultSet rs) {
        if(rs != null) {
            try { rs.close(); } catch(Throwable whatever) {}
        }
    }
}

