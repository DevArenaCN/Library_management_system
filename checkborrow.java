//check all waiting list table


import java.sql.*;
import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.ParseException;

public class checkborrow {

    static final String jdbcURL 
	= "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

    public static void Checkborrow(String userId) {
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
        System.out.println("Borrowed Resources");
        //borrow book
		rs = stmt.executeQuery("SELECT * from sBookBorrow,Books where sBookBorrow.sid ='"+sid+"' and sBookBorrow.bid = Books.bid");
        System.out.println("-------------------Borrowed Books--------------------");
		while (rs.next()) 
        {
            String bid = rs.getString("bid");
            String btype = rs.getString("btype");
            String show_bisbn = rs.getString("bisbn");
			String show_btitle = rs.getString("btitle");
			int show_bedition = rs.getInt("bedition");
			String show_bauthor = rs.getString("bauthor");
			int  show_byear = rs.getInt("bpubyear");
			String show_bpublisher = rs.getString("bpub");
			String show_blocation = rs.getString("blocation");
            Date stime = rs.getDate("stime");
            Date etime = rs.getDate("etime");

            if (etime == null)
                System.out.println(sid+" "+bid+" "+btype+" "+show_bisbn+" "+show_btitle+" "+show_bedition+" "+show_bauthor+" "+show_bpublisher+" "+show_byear+" "+show_blocation+" "+stime.toString());
            else
                System.out.println(sid+" "+bid+" "+btype+" "+show_bisbn+" "+show_btitle+" "+show_bedition+" "+show_bauthor+" "+show_bpublisher+" "+show_byear+" "+show_blocation+" "+stime.toString()+" "+etime.toString());

		}
        //borrow journal
        rs = stmt.executeQuery("SELECT * from sJournalBorrow,Journal where sJournalBorrow.sid ='"+sid+"' and sJournalBorrow.jid = Journal.jid");
        System.out.println("-------------------Borrowed Journal--------------------");
        while (rs.next()) 
        {
            String jid = rs.getString("jid");
            String show_jisbn = rs.getString("jisbn");
            String show_jtitle = rs.getString("jtitle");
            //int show_bedition = rs.getInt("bedition");
            String show_jauthor = rs.getString("jauthor");
            int  show_jyear = rs.getInt("jpubyear");
            //String show_bpublisher = rs.getString("bpub");
            //String show_blocation = rs.getString("blocation");
            Date stime = rs.getDate("stime");
            Date etime = rs.getDate("etime");

            if (etime == null)
                System.out.println(sid+" "+jid+" "+show_jisbn+" "+show_jtitle+" "+show_jauthor+" "+show_jyear+" "+stime.toString());
            else
                System.out.println(sid+" "+jid+" "+show_jisbn+" "+show_jtitle+" "+show_jauthor+" "+show_jyear+" "+stime.toString()+" "+etime.toString());

        }

        //borrow conference
        rs = stmt.executeQuery("SELECT * from sConfBorrow,Conference where sConfBorrow.sid ='"+sid+"' and sConfBorrow.cfid = Conference.cfid");
        System.out.println("-------------------Borrowed Conference--------------------");
        while (rs.next()) 
        {
            String cfid = rs.getString("cfid");
            String show_cisbn = rs.getString("confNum");
            String show_cname = rs.getString("confName");
            String show_ctitle = rs.getString("confTitle");
            //int show_bedition = rs.getInt("bedition");
            String show_cauthor = rs.getString("confAuthor");
            int  show_cyear = rs.getInt("confPubyear");
            //String show_bpublisher = rs.getString("bpub");
            //String show_blocation = rs.getString("blocation");
            Date stime = rs.getDate("stime");
            Date etime = rs.getDate("etime");

            if (etime == null)
                System.out.println(sid+" "+cfid+" "+show_cisbn+" "+show_cname+" "+show_ctitle+" "+show_cauthor+" "+show_cyear+" "+stime.toString());
            else
                System.out.println(sid+" "+cfid+" "+show_cisbn+" "+show_cname+" "+show_ctitle+" "+show_cauthor+" "+show_cyear+" "+stime.toString()+" "+etime.toString());

        }



        //reserved camera
        rs = stmt.executeQuery("select * from Camera, sCamBorrow where sCamBorrow.sid ='"+sid+"' and sCamBorrow.cam_id=Camera.cam_id ");
        System.out.println("-------------------Borrowed Camera--------------------");
        while(rs.next())
        {
            String cam_id = rs.getString("cam_id");
            String make = rs.getString("make");
            String model = rs.getString("model");
            String lensConfig = rs.getString("lensConfig");
            String memory = rs.getString("memory");
            String camLocation = rs.getString("camLocation");
            Date stime = rs.getDate("stime");
            Date etime = rs.getDate("etime");
            System.out.println(sid+" "+cam_id+" "+make+" "+model+" "+lensConfig+" "+memory+" "+camLocation+" "+stime.toString()+" "+etime.toString());

        }
/*
        rs = stmt.executeQuery("select * from StudyRoom, SRBorrow where SRBorrow.fsid ='"+sid+"' and StudyRoom.srNum = SRBorrow.srNum ");
        while (rs.next()) 
        {
            String room_num = rs.getString("srNum");
            int roomfloor = rs.getInt("srFl");
            String peoplenum = rs.getString("srCapacity");
            String location = rs.getString("srLocation");
            Date starttime = getTime
            System.out.printf("%-2s  %-4s   %-4s   %-6s  %-12s  %n", room_num, roomfloor, peoplenum, location, starttime+"--"+endtime);
        } 
                                
*/







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

