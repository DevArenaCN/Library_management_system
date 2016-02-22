import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class fuserInfo {
    static final String jdbcURL 
    = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

    public static void Profile(String UserID) {
        Scanner menuSelect = new Scanner(System.in);
        Boolean b= true;

        try {

            // Load the driver. This creates an instance of the driver
        // and calls the registerDriver method to make Oracle Thin
        // driver available to clients.

            Class.forName("oracle.jdbc.driver.OracleDriver");

        String user = "ychen71";    // For example, "jsmith"
        String passwd = "200099159";    // Your 9 digit student ID number
         Statement searchUser = null;
         String userID = UserID;
         ResultSet rs = null;
         String searchUserQuery = "SELECT * FROM Faculty WHERE Faculty.fid =\'"+userID+"\'";  //search for user info query
         Connection conn = null; 
         Integer menuChose = 0;     //first menu input
         Integer menu_2chose = 0;     //second menu input
        
            try {

        // Get a connection from the first driver in the
        // DriverManager list that recognizes the URL jdbcURL

        conn = DriverManager.getConnection(jdbcURL, user, passwd);

       //Display User info

        searchUser = conn.createStatement();
        rs = searchUser.executeQuery(searchUserQuery);
        while(rs.next()){
            String fid = rs.getString("fid");    //get fid
            String fname = rs.getString ("fname");    //get fname
            String fnation = rs.getString("fnation");
            String fCat = rs.getString("fcategory");
            String fdepart = rs.getString("fdepart");
            System.out.println("FID:"+fid+"\n"+"Faculty Name:"+fname+"\n"+"Faculty Nationality:"+fnation+"\n"+"Faculty Category:"+fCat+"\n"+"Faculty Department:"+fdepart); 
        }
         System.out.println("please choose:\n1.modify user info\n2.back");   //1st menu
         while(b){
             menuChose = menuSelect.nextInt();
             b=false;
         }
            if(menuChose == 1){
            	
            boolean b1 = true;
            while(b1){
                System.out.println("Please choose which one you want to modify:\n1.Change Name\n2.Change Nationality\n3.Change Faculty Category\n4.Change Department\n0.Back");
                menu_2chose = menuSelect.nextInt();
                System.out.println(menu_2chose);

            //change faculty name mode
            if(menu_2chose == 1)  
            {
                System.out.println("Change faculty name mode");
                menuSelect.nextLine();
                System.out.println("Please input changed name: \n");
                String updateName = menuSelect.nextLine();
                String updateQuery = "UPDATE Faculty SET fname= \'"+updateName+"\' WHERE fid=\'"+userID+"\'";
                searchUser.executeUpdate(updateQuery);
            }

            //change faculty nationality mode
            else if(menu_2chose == 2)
            {
                System.out.println("Change faculty nationality mode");
                System.out.println("Please input changed faculty nationality: \n");
                String updateFnation = menuSelect.next();
                String updateQuery = "UPDATE Faculty SET fnation= \'"+updateFnation+"\' WHERE fid=\'"+userID+"\'";
                searchUser.executeUpdate(updateQuery);

            }

            //change Faculty Category mode
            else if(menu_2chose == 3){
                System.out.println("Change faculty category mode");
                System.out.println("Please input changed faculty category: \n");
                String updateFcat = menuSelect.next();
                String updateQuery = "UPDATE Faculty SET fcategory= \'"+updateFcat+"\' WHERE fid=\'"+userID+"\'";
                searchUser.executeUpdate(updateQuery);

            }

            //change faculty department mode
            else if(menu_2chose == 4){
                System.out.println("Change faculty department mode");
                System.out.println("Please input changed faculty department: \n");
                String updateFdepart = menuSelect.next();
                String updateQuery = "UPDATE Faculty SET fdepart= \'"+updateFdepart+"\' WHERE fid=\'"+userID+"\'";
                searchUser.executeUpdate(updateQuery);

            }

          
            
            //back
            else if(menu_2chose == 0){
            	System.out.println("back");
            	b1=false;
            }
            }
            }


            } finally {
                close(rs);
                close(searchUser);
                close(conn);
            }
        }
        catch(Throwable oops){
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
