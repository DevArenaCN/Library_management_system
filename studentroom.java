import java.sql.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

public class  studentroom{

    static final String jdbcURL 
	= "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

    static Scanner stdin = new Scanner(System.in);
    static String starttime, endtime, peoplenum, location;

    static String sid = null;
    static Connection conn = null;

    public static void studroom(String UserID) {
    	int func;
   
            // Load the driver. This creates an instance of the driver
	    	// and calls the registerDriver method to make Oracle Thin
	    	// driver available to clients.

	    	String user = "ychen71";	// For example, "jsmith"
	    	String passwd = "200099159";	// Your 9 digit student ID number
	    	sid = UserID;
         

        try {

		// Get a connection from the first driver in the
		// DriverManager list that recognizes the URL jdbcURL
        Class.forName("oracle.jdbc.driver.OracleDriver");
		conn = DriverManager.getConnection(jdbcURL, user, passwd);

		
		 do {
                System.out.println();
                
                System.out.println("0. Search for studyroom");
                System.out.println("1. Search for conferenceroom");
                System.out.println("2. Reserve a room");
                System.out.println("3. check In");
                System.out.println("4. check Out");
                System.out.println("5. Quit");
                System.out.print("Command >> ");
                func = stdin.nextInt();
                stdin.nextLine();
                switch (func) {
                case 0:
                	function0_search_for_studyroom();
                    break;
                case 1:
                	function1_search_for_confroom();
                    break;
                 case 2:
                    function2_reserve();
                    break;
               case 3:
                	function3_checkin();
                    break;
                case 4:
                	function4_checkout();
                    break;
                case 5: 
                    break;  
                default:
                    System.out.println("Wrong input. Try again!");
                }
            } while (func != 5);
   
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }
    }


        private static void input() {

            System.out.println("*******************Catalog Explorer - Search for studyroom**********************");

            System.out.print("Starttime (format: mm/dd/yy hh24:mi; Example: 10/17/15 12:00)>> ");
            starttime= stdin.nextLine();

            System.out.print("Endtime (format: mm/dd/yy hh24:mi; Example: 10/17/15 12:00)>> ");
            endtime= stdin.nextLine();

            System.out.print("max number of people >> ");
            peoplenum=stdin.nextLine();

            System.out.print("Location >> ");
            location= stdin.nextLine();
        }//input


		private static void function0_search_for_studyroom() {
        // TODO 
        int  roomfloor ;
        String room_num;
       
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exist = false;

        input();
        
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm");  

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(starttime);
            d2 = format.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }    
        //check the time interval no more than 3*60*60 seconds
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000; 
        if ( diffSeconds>3*60*60 ) {
            System.out.println("------------------------------------------------------------------");
            System.out.println("The time interval can not exceed 3 hours!!!! Try again ");
            System.out.println("------------------------------------------------------------------");
            input();
        }

        System.out.println();
        System.out.printf("%-4s  %-4s   %-4s   %-6s  %n", "Room_Num", "Floor", "Capacity", "Location");
        System.out.println("------------------------------------------------------------------");

        try {
            stmt = conn.prepareStatement(" select * from StudyRoom SR where not exists( select * from SRBorrow where ((to_date('"+ starttime +"','mm/dd/yy hh24:mi') between SRBorrow.stime and SRBorrow.etime) or (to_date('"+ endtime +"','mm/dd/yy hh24:mi') between SRBorrow.stime and SRBorrow.etime)) and SRBorrow.otmark=0 and SR.srNum = SRBorrow.srNum) and SR.srLocation like '%" + location + "%' and SR.srCapacity>= '"+ peoplenum +"'");
                                           
            rs = stmt.executeQuery();

            while (rs.next()) {               
                room_num = rs.getString("srNum");
                roomfloor = rs.getInt("srFl");
                peoplenum = rs.getString("srCapacity");
                location = rs.getString("srLocation");
                System.out.printf("%-6s  %-4s   %-10s   %-12s  %n", room_num, roomfloor, peoplenum, location);
                exist = true;
            }
            
            if (!exist) 
                System.out.println("There is no result.");
        } 
        catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {}
        }
    }//function0_search_for_studyroom





    private static void function1_search_for_confroom(){

            System.out.println("------------------------------------------------------------------");
            System.out.println("Students have no access to Conference room!");
            System.out.println("------------------------------------------------------------------");
    }//function1_search_for_confroom





    private static void function2_reserve(){

        System.out.println("*******************Catalog Explorer - Reserve a room**********************");
        
        String room_num;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

            try {
               
                System.out.print("Please enter the Room Number that you want to reserve: ");
                room_num = stdin.nextLine();

                pstmt = conn.prepareStatement(" select * from StudyRoom where srNum='"+ room_num +"'");
                rs = pstmt.executeQuery();
                
                if (!rs.next())
                    System.out.println("Not a valid Room Number.");

                else {
                    pstmt = conn.prepareStatement(" select * from SRBorrow where SRBorrow.stime = to_date('"+ starttime +"','mm/dd/yy hh24:mi') and SRBorrow.etime = to_date('"+ endtime +"','mm/dd/yy hh24:mi') and SRBorrow.srNum ='"+ room_num +"'");
                    rs = pstmt.executeQuery();
                    
                    if (rs.next())
                        System.out.println("The room is not available in the time you requested.");
                    else {
                        try {
                            conn.setAutoCommit(false);
                            System.out.printf("%-4s  %-4s   %-2s   %-6s  %-6s  %n", "Room_ID", "Floor", "Capacity","Location","Duration");
                            System.out.println("-----------------------------------------------------------------------------");
                            pstmt = conn.prepareStatement(" select * from StudyRoom where srNum='"+ room_num +"'");
                            rs = pstmt.executeQuery();
                            while (rs.next()) {                
                                room_num = rs.getString("srNum");
                                int roomfloor = rs.getInt("srFl");
                                peoplenum = rs.getString("srCapacity");
                                location = rs.getString("srLocation");
                                System.out.printf("%-2s  %-4s   %-4s   %-6s  %-12s  %n", room_num, roomfloor, peoplenum, location, starttime+"--"+endtime);
                            }
                            System.out.println();
                            System.out.println("Are you sure to reserve the room above? 1.yes 2.no");
                            int flag = stdin.nextInt();
                            stdin.nextLine();

                            if( flag == 1){

                                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
                                Date d1 = df.parse(starttime);
                                Calendar checkin=Calendar.getInstance();
                                checkin.setTime(d1);
                                checkin.add(Calendar.HOUR_OF_DAY, 1);

                               
                                pstmt = conn.prepareStatement("INSERT INTO SRBorrow VALUES('"+ sid +"','"+ room_num +"',to_date('"+ starttime +"','mm/dd/yy hh24:mi'),to_date('"+ endtime +"','mm/dd/yy hh24:mi'),?)");
                                pstmt.setInt(1,0); //Ot mark
                                pstmt.executeUpdate();
                                System.out.println("Success! Please check in before "+df.format(checkin.getTime()));
                            }
                            else {System.out.println(" You did not make a reservation.");}
                            conn.commit();
                            
                        } catch (Exception e){ 
                            conn.rollback();
                        }
                    }
                }
                
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (pstmt != null)
                       pstmt.close();
                } catch (Exception e) {
                }
            }
    }//function2_reserve




    private static void function3_checkin(){

        PreparedStatement pre = null;
        ResultSet rs = null;
        String sdate,edate, startdate; //Used to get the time from sql
        String room_num;
        Date d1 = null;
        Date d2 = null;

        try {
      
            System.out.println("*******************Catalog Explorer - Check in**********************");
            System.out.print("Please enter the Room Number that you want to check in>> ");
            room_num=stdin.nextLine();
            
            System.out.print("Please enter the starttime of your reservation: (format: mm/dd/yy hh24:mi; Example: 10/17/15 12:00)>> ");
            startdate=stdin.nextLine();

                                
            pre = conn.prepareStatement(" select * from SRBorrow where SRBorrow.fsid = '"+sid+"' and SRBorrow.srNum = '"+room_num+"' and SRBorrow.stime = to_date('"+ startdate +"','mm/dd/yy hh24:mi') ");
            
            rs = pre.executeQuery();
            
            
            if (!rs.next())
                    System.out.println("No result is founded.");
            else{
                System.out.println("Wait a moment......................");
               
                do{   
                    sid= rs.getString("fsid");        
                    room_num = rs.getString("srNum");
                    d1 = rs.getTimestamp("stime");
                    d2 = rs.getTimestamp("etime");
                }while(rs.next());//while

                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
                String currentime=df.format(new Date());
                System.out.println("Current time:"+currentime+".");
                sdate = df.format(d1);
                edate = df.format(d2);
                
                Date datenow=df.parse(currentime);

                System.out.printf("%-2s %-4s %-14s  %-14s  %n", "Student ID", "Room_Num", "Duration","Check-in Time");
                System.out.printf("%-2s %-4s %-12s  %-8s  %n", sid, room_num, sdate+"--"+edate, currentime);
             
                System.out.println();

                Calendar checkin=Calendar.getInstance();
                checkin.setTime(d1);
                checkin.add(Calendar.HOUR_OF_DAY, 1);

                if ( datenow.getTime() < d1.getTime() )
                    System.out.println("Can not check in now!");
                else if ( d1.getTime() <= datenow.getTime() && datenow.getTime()<= (d1.getTime()+60*60*1000) ){

                    System.out.println("You have successfully checked in the room");

                    System.out.println(">>>>>>>>>>>>You can use the room before "+edate+" >>>>>>>>>>>>>");
                    }
                else if (datenow.getTime() > (d1.getTime()+60*60*1000)){
                    pre = conn.prepareStatement("UPDATE SRBorrow set otmark=1 where SRBorrow.srNum = '"+room_num+"' and SRBorrow.stime = to_date('"+ startdate +"','mm/dd/yy hh24:mi')");
                    pre.executeUpdate();

                    System.out.println("Sorry! You failed to check in by "+df.format(checkin.getTime()) );
                    System.out.println("Your reservation of the room "+room_num+" has already been canclled!");
                }

            }//else 

        }//try
         catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (pre != null)
                       pre.close();
                } catch (Exception e) {
                }
            }

    }//function3_checkout




    private static void function4_checkout(){

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String room_num, startdate;

        try {

            System.out.println("*******************Catalog Explorer - Check Out**********************");
            System.out.print("Please enter the Room Number that you want to check out>> ");
            room_num=stdin.nextLine();
            System.out.print("Please enter the starttime of your reservation: (format: mm/dd/yy hh24:mi; Example: 10/17/15 12:00)>> ");
            startdate=stdin.nextLine();

            pstmt = conn.prepareStatement(" select * from SRBorrow where SRBorrow.fsid = '"+sid+"' and SRBorrow.stime = to_date('"+ startdate +"','mm/dd/yy hh24:mi') and SRBorrow.srNum = '"+ room_num +"'");
            rs = pstmt.executeQuery();
            String sdate, edate;
            Date d1 = null;
            Date d2 = null;
            Date datenow = null;

            if (!rs.next())
                    System.out.println("No result is founded.");
            else{

                do{ 
                    sid= rs.getString("fsid");      
                    room_num = rs.getString("srNum");
                    d1 = rs.getTimestamp("stime");
                    d2 = rs.getTimestamp("etime");
                } while (rs.next());//while

                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
                String currentime=df.format(new Date());
                sdate = df.format(d1);
                edate = df.format(d2);
                datenow=df.parse(currentime);

                System.out.printf("%-2s %-4s %-14s  %-14s  %n", "Student Id", "Room_Num", "Duration","Check-out Time");
                System.out.printf("%-2s %-4s %-12s  %-8s  %n", sid, room_num, sdate+"--"+edate, currentime);
                System.out.println();
                if ( datenow.getTime() < d2.getTime() )
                {
                    pstmt = conn.prepareStatement("UPDATE SRBorrow set otmark=2 where srNum='"+ room_num +"' and stime= to_date('"+ sdate +"','mm/dd/yy hh24:mi') and etime= to_date('"+ edate +"','mm/dd/yy hh24:mi') ");
                    
                    pstmt.executeUpdate();

                    System.out.println("Congratulations! Your reservation of the room "+room_num+", which starting from "+sdate+ " to "+edate+" has successfully been checked in!");
                }
                else
                    System.out.println("There is no need to check in the room any more.");

            }//else
        }//try
         catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (pstmt != null)
                       pstmt.close();
                } catch (Exception e) {
                }
            }


    }//function4_checkin




}//class 



