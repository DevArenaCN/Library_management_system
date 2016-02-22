import java.util.Scanner;

public class db {
	public static void main(String args[]){
		
		Scanner typein = new Scanner(System.in);
		
		int LoginSucc = 0;
		int AccountType = 0;
		int Otmark[] = new int[1];
		String userID = null;
		
		while(LoginSucc == 0)
		{
			System.out.println("1. Login");
			System.out.println("2. Exit");
			int Log_input = typein.nextInt();
			if(Log_input == 1)
			{
				//Enter Login part
				login lg = new login();
				userID = lg.login(Otmark);
				//System.out.println("LoginID: " + userID);
				//System.out.println("Otmark: " + Otmark[0]);
				
				//Login successful, Enter Main Interface
				if(userID != null)
				{
					if(userID.charAt(0) == 'S')
						AccountType = 1;
					else
						AccountType = 2;
					//System.out.println("LoginType: " + AccountType);
					
					int Main_Interface = 0;
					while(Main_Interface == 0)
					{
						System.out.println("1. Profile");
						System.out.println("2. Resources");
						System.out.println("3. Check-out Resources");
						System.out.println("4. Resource Request");
						System.out.println("5. Notifications");
						System.out.println("6. Due-Balance");
						System.out.println("7. Logout");
						
						int Main_input = typein.nextInt();
						
						// 1. Profile
						if(Main_input == 1)
						{
							if(AccountType == 1)
							{
								userInfo UserProfile = new userInfo();
								UserProfile.Profile(userID);
							}
							else
							{
								fuserInfo UserProfile = new fuserInfo();
								UserProfile.Profile(userID);
							}
								
						}
						
						//2. Resources
						else if(Main_input == 2)
						{
							if(Otmark[0] == 1)
							{
								System.out.println("You have resources overdue 90 days, please check!");
								continue;
							}
							System.out.println("1. Publication");
							System.out.println("2. Conference/Study rooms");
							System.out.println("3. Camera");
							
							int Resources_Type = typein.nextInt();
							
							//Camera
							if(Resources_Type == 3)
							{
								try
								{
									if(AccountType == 1)
									{
										camera ca = new camera();
										ca.Camera(userID);
									}
									else
									{
										fcamera fca = new fcamera();
										fca.fcamer(userID);
									}
								}
								catch(Exception e)
								{
								}
							}
							
							// Publication
							else if(Resources_Type == 1)
							{
								if(AccountType == 1)
								{
									Project_DB_Publications_student publiStu = new Project_DB_Publications_student(userID);
								}
								else
								{
									Project_DB_Publications_faculty publiFac = new Project_DB_Publications_faculty(userID);
								}
							}
							
							// Conference/Study rooms
							else if(Resources_Type == 2)
							{
								if(AccountType == 1)
								{
									studentroom student_room = new studentroom();
									student_room.studroom(userID);
								}
								else
								{
									facultyroom facu_room = new facultyroom();
									facu_room.faculty_room(userID);
								}
							}
											
						}
						
						//3. Check-out Resources
						else if(Main_input == 3)
						{
							if(AccountType == 1)
							{
								checkborrow cb = new checkborrow();
								cb.Checkborrow(userID);
							}
							else
							{
								fcheckborrow fc = new fcheckborrow();
								fc.fcheckbor(userID);
							}
							showroomborrow srb = new showroomborrow();
							srb.showrm(userID);
				
						}
						
						//4. Resource Request
						else if(Main_input == 4)
						{
							if(AccountType == 1)
							{
								checkwl cw = new checkwl();
								cw.checkwait(userID);
							}
							else
							{
								fcheckwl fcw = new fcheckwl();
								fcw.fcheckwait(userID);
							}
						}
						
						//5. Notifications
						else if(Main_input == 5)
						{
							if(AccountType == 1)
							{
								Project_DB_Notifications_student NotiStu = new Project_DB_Notifications_student(userID);
							}
							else
							{
								Project_DB_Notifications_faculty NotiFau = new Project_DB_Notifications_faculty(userID);
							}
						}
						
						//6. Due-Balance
						else if(Main_input == 6)
						{
							try
							{
								if(AccountType == 1)
								{
									fine Due_bal = new fine();
									Due_bal.start(userID);
								}
								else
								{
									ffine Due_fbal = new ffine();
									Due_fbal.ffin(userID);
								}
							}
							catch(Exception e)
							{
							}

						}
						
						//7. Logout
						else if(Main_input == 7)
						{
							System.out.println("Input: 7");	
							Main_Interface = 1;
						}
						
					}
				}
						
			}
			
			//Exit
			else if(Log_input == 2)
				return;
		}		
	}
}
