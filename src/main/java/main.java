import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import com.mysql.cj.x.protobuf.MysqlxExpr.Expr;

import net.proteanit.sql.DbUtils;

public class main {

	public static void main(String[] args) {
		login();
	}
	
	public static void login() {
		final JFrame f = new JFrame("Login");
		
		JLabel l1, l2;
		l1 = new JLabel("Username");
		l1.setBounds(25, 10, 75, 25);
		l2 = new JLabel("Password");
		l2.setBounds(25, 50, 75, 25);
		
		final JTextField fUser = new JTextField();
		fUser.setBounds(100, 10, 150, 25);
		final JPasswordField fPass = new JPasswordField();
		fPass.setBounds(100, 50, 150, 25);
		
		JButton loginBut = new JButton("Login");
		loginBut.setBounds(100, 90, 75, 25);
		
		loginBut.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String username = fUser.getText();
				String password = String.valueOf(fPass.getPassword());
			
				if(username.equals("")) {
					JOptionPane.showMessageDialog(null, "Please Enter Username");
				}
				else if(password.equals("")) {
					JOptionPane.showMessageDialog(null, "Please Enter Password");
				}
				else {
					Connection con = connect();
					try {
						Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
						stmt.executeUpdate("USE LIBRARY");
						String query = ("SELECT * FROM USERS WHERE USERNAME='" + username + "' AND PASSWORD='" + password + "'");
						ResultSet rs = stmt.executeQuery(query);
						if(rs.next() == false) {
							System.out.print("No User Found!");
							JOptionPane.showMessageDialog(null, "Wrong Username/Password!");
						}
						else {
							f.dispose();
							rs.beforeFirst();
							while(rs.next()) {
								String admin = rs.getString("ADMIN");
								String UID = rs.getString("UID");
								if(admin.equals("1")) {
									admin_menu();
								}
								else {
									user_menu(UID);
								}
							}
						}
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
				}	
			}
		});
		
		f.add(l1);
		f.add(fUser);
		f.add(l2);
		f.add(fPass);
		f.add(loginBut);
		
	    f.setSize(400, 200);
	    f.setLayout(null);
	    f.setVisible(true);
	    f.setLocationRelativeTo(null);
	}
	
	public static Connection connect() {
		try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        System.out.println("Loaded Driver");
	        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", " ");
	        System.out.println("Connected to MySQL");
	        return con;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static void user_menu(final String UID) {
		JFrame f = new JFrame("User Options");
		
		JButton viewBut = new JButton("View Books");
		viewBut.setBounds(25, 25, 100, 25);
		viewBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("Books Available");
	            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            Connection con = connect();
	            String query="SELECT * FROM BOOKS";
	            try {
	            	Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
	                stmt.executeUpdate("USE LIBRARY");
	                stmt=con.createStatement();
	                ResultSet rs=stmt.executeQuery(query);
	                JTable bookList= new JTable();
	                bookList.setModel(DbUtils.resultSetToTableModel(rs)); 
	                JScrollPane scrollBar = new JScrollPane(bookList);
	                
	                f.add(scrollBar);
	                f.setSize(800, 400);
	                f.setVisible(true);
	                f.setLocationRelativeTo(null);
	            }
	            catch (SQLException ex) {
	                JOptionPane.showMessageDialog(null, ex);
	            }
			}
		});
		
		
		JButton myBook = new JButton("My Books");
		myBook.setBounds(150, 25, 100, 25);
		myBook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("My Books");
	            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				int intUID = Integer.parseInt(UID);
				Connection con = connect();
	            String query="SELECT DISTINCT issued.*, books.bname, books.genre, books.price FROM issued, books " + "WHERE ((issued.uid=" + intUID + ") AND (books.bid IN (SELECT bid FROM issued WHERE issued.uid=" + intUID + "))) GROUP BY iid";
	            try {
	                Statement stmt = con.createStatement();
	                stmt.executeUpdate("USE LIBRARY");
	                stmt=con.createStatement();
	                ArrayList books_list = new ArrayList();
	                ResultSet rs=stmt.executeQuery(query);
	                JTable bookList= new JTable();
	                bookList.setModel(DbUtils.resultSetToTableModel(rs)); 
	                JScrollPane scrollBar = new JScrollPane(bookList);
	 
	                f.add(scrollBar);
	                f.setSize(800, 400);
	                f.setVisible(true);
	                f.setLocationRelativeTo(null);
	            } 
	            catch (SQLException ex) {
	                JOptionPane.showMessageDialog(null, ex);
	            }  
			}
		});
		
		f.add(myBook);
	    f.add(viewBut);
	    f.setSize(300,150);
	    f.setLayout(null);
	    f.setVisible(true);
	    f.setLocationRelativeTo(null);
	}

	public static void admin_menu() {
		
		JFrame f=new JFrame("Admin Options");
	    //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
		
		JButton viewBut=new JButton("View Books");
	    viewBut.setBounds(20,20,120,25);
	    viewBut.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e){
	            JFrame f = new JFrame("Books Available"); 
	            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            
	            Connection con = connect();
	            String query = "SELECT * FROM books";
	            try {
	                Statement stmt = con.createStatement();
	                stmt.executeUpdate("USE LIBRARY");
	                stmt=con.createStatement();
	                ResultSet rs=stmt.executeQuery(query);
	                JTable bookList= new JTable();
	                bookList.setModel(DbUtils.resultSetToTableModel(rs)); 
	                JScrollPane scrollBar = new JScrollPane(bookList); 
	 
	                f.add(scrollBar);
	                f.setSize(800, 400);
	                f.setVisible(true);
	                f.setLocationRelativeTo(null);
	            }
	            catch (SQLException ex) {
	                 JOptionPane.showMessageDialog(null, ex);
	            }
	        }
	    });
		
	    JButton usersBut=new JButton("View Users");
	    usersBut.setBounds(150,20,120,25);
	    usersBut.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e){ 
	        	JFrame f = new JFrame("Users List");
	            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	                 
	            Connection con = connect();
	            String query = "select * from users"; //retrieve all users
	            try { 
	            	Statement stmt = con.createStatement();
	                stmt.executeUpdate("USE LIBRARY"); //use database
	                stmt=con.createStatement();
	                ResultSet rs=stmt.executeQuery(query);
	                JTable bookList= new JTable();
	                bookList.setModel(DbUtils.resultSetToTableModel(rs)); 
	                JScrollPane scrollBar = new JScrollPane(bookList);
	 
	                f.add(scrollBar);
	                f.setSize(800, 400);
	                f.setVisible(true);
	                f.setLocationRelativeTo(null);
	            }
	            catch (SQLException ex) {
	            	JOptionPane.showMessageDialog(null, ex);
	            }                
	        }
	    }); 
	    
	    JButton addUser=new JButton("Add User");
	    addUser.setBounds(20,60,120,25);
	    addUser.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){         
	    		final JFrame g = new JFrame("Enter User Details");
	    		//g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    		JLabel l1,l2;  
	    		l1=new JLabel("Username");
	    		l1.setBounds(30,15, 100,30);
	    		l2=new JLabel("Password");
	    		l2.setBounds(30,50, 100,30); 
	                 
	    		final JTextField fUser = new JTextField();
	    		fUser.setBounds(110, 15, 200, 30);
	    		final JPasswordField fPass=new JPasswordField();
	    		fPass.setBounds(110, 50, 200, 30);
	            
	    		final JRadioButton a1 = new JRadioButton("Admin");
	    		a1.setBounds(55, 80, 200,30);
	    		JRadioButton a2 = new JRadioButton("User");
	    		a2.setBounds(130, 80, 200,30);
	   
	    		ButtonGroup bg=new ButtonGroup();    
	    		bg.add(a1);
	    		bg.add(a2);  
	                                      
	    		JButton createBut=new JButton("Create");
	    		createBut.setBounds(130,130,80,25);
	    		createBut.addActionListener(new ActionListener() {
	    			public void actionPerformed(ActionEvent e){ 
	    				String username = fUser.getText();
	    				String password = String.valueOf(fPass.getPassword());
	                    Boolean admin = false;
	                    if(a1.isSelected()) {
	                        admin=true;
	                    }  
	                    Connection con = connect();
	                    try {
	                    	Statement stmt = con.createStatement();
	                    	stmt.executeUpdate("USE LIBRARY");
	                    	stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES ('" + username + "','" + password + "'," + admin + ")");
	                    	JOptionPane.showMessageDialog(null, "User added!");
	                    	g.dispose();  
	                    }
	                    catch (SQLException ex) {
	                         JOptionPane.showMessageDialog(null, ex);
	                    }   
	    			}     
	    		});

	    		g.add(createBut);
	    		g.add(a2);
	    		g.add(a1);
	    		g.add(l1);
	    		g.add(l2);
	    		g.add(fUser);
	    		g.add(fPass);
	    		g.setSize(350,200);
	    		g.setLayout(null);  
	    		g.setVisible(true);
	    		g.setLocationRelativeTo(null);    
	    	}
	    });
	    
	    JButton issuedBut=new JButton("View Issued Books");
	    issuedBut.setBounds(280,20,160,25);
	    issuedBut.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){
	    		JFrame f = new JFrame("Users List");
	    		//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     
	      
	    		Connection con = connect();
	    		String query="SELECT * FROM issued";
	    		try {
	    			Statement stmt = con.createStatement();
	    			stmt.executeUpdate("USE LIBRARY");
	    			stmt=con.createStatement();
	    			ResultSet rs=stmt.executeQuery(query);
	    			JTable bookList= new JTable();
	    			bookList.setModel(DbUtils.resultSetToTableModel(rs)); 
	    			JScrollPane scrollBar = new JScrollPane(bookList);
	    			f.add(scrollBar);
	    			f.setSize(800, 400);
	    			f.setVisible(true);
	    			f.setLocationRelativeTo(null);
	    		}
	    		catch (SQLException ex) {
	    			JOptionPane.showMessageDialog(null, ex);
	    		}      
	    	}
	    });
	    
	    JButton addBook = new JButton("Add Book");
	    addBook.setBounds(150,60,120,25); 
	    addBook.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){
	    		final JFrame g = new JFrame("Enter Book Details");
	    		//g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		
	    		JLabel l1,l2,l3;  
	    		l1 = new JLabel("Book Name");
	    		l1.setBounds(30,15, 100,30); 
	    		l2 = new JLabel("Genre");
	    		l2.setBounds(30,53, 100,30);
	    		l3 = new JLabel("Price");
	    		l3.setBounds(30,90, 100,30); 
	                 
	    		final JTextField fBname = new JTextField();
	    		fBname.setBounds(110, 15, 200, 30);
	    		final JTextField fGenre=new JTextField();
	    		fGenre.setBounds(110, 53, 200, 30);
	    		final JTextField fPrice=new JTextField();
	    		fPrice.setBounds(110, 90, 200, 30);
	                        
	    		JButton createBut=new JButton("Submit");  
	    		createBut.setBounds(130,130,80,25);
	    		createBut.addActionListener(new ActionListener() {
	    			public void actionPerformed(ActionEvent e){
	    				String bname = fBname.getText();
	    				String genre = fGenre.getText();
	    				String price = fPrice.getText();
	                    int price_int = Integer.parseInt(price);	                     
	                    Connection connection = connect();
	                    try {
	                    	Statement stmt = connection.createStatement();
	                    	stmt.executeUpdate("USE LIBRARY");
	                    	stmt.executeUpdate("INSERT INTO BOOKS(BNAME,GENRE,PRICE) VALUES ('" + bname + "','" + genre + "'," + price_int + ")");
	                    	JOptionPane.showMessageDialog(null,"Book added!");
	                    	g.dispose();
	                    }
	                    catch (SQLException ex) {
	                         JOptionPane.showMessageDialog(null, ex);
	                    }        
	    			}
	    		});
	                                 
	    		g.add(l3);
	    		g.add(createBut);
	    		g.add(l1);
	    		g.add(l2);
	    		g.add(fBname);
	    		g.add(fGenre);
	    		g.add(fPrice);
	    		g.setSize(350,200);
	    		g.setLayout(null);  
	    		g.setVisible(true); 
	    		g.setLocationRelativeTo(null);                  
	    	}
	    });
	    
	    JButton issueBook=new JButton("Issue Book");
	    issueBook.setBounds(450,20,120,25);
	    issueBook.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){
	    		final JFrame g = new JFrame("Enter Details");
	    		//g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		
	    		JLabel l1,l2,l3,l4;  
	    		l1=new JLabel("Book ID(BID)");
	    		l1.setBounds(30,15, 100,30);      
	    		l2=new JLabel("User ID(UID)");
	    		l2.setBounds(30,53, 100,30);
	    		l3=new JLabel("Period(days)");
	    		l3.setBounds(30,90, 100,30);     
	    		l4=new JLabel("Issued Date(DD-MM-YYYY)");
	    		l4.setBounds(30,127, 150,30); 
	    		
	    		final JTextField fBid = new JTextField();
	    		fBid.setBounds(110, 15, 200, 30);
	    		final JTextField fUID = new JTextField();
	    		fUID.setBounds(110, 53, 200, 30);
	    		final JTextField fPeriod = new JTextField();
	    		fPeriod.setBounds(110, 90, 200, 30);    
	    		final JTextField fIssue = new JTextField();
	    		fIssue.setBounds(180, 130, 130, 30);   
	    		
	    		JButton createBut=new JButton("Submit");
	    		createBut.setBounds(130,170,80,25);
	    		createBut.addActionListener(new ActionListener() { 	
	    			public void actionPerformed(ActionEvent e){ 
	    				String uid = fUID.getText();
	                    String bid = fBid.getText();
	                    String period = fPeriod.getText();
	                    String issued_date = fIssue.getText();
	                    int period_int = Integer.parseInt(period);
	                    Connection con = connect();
	                    try {
	                    	Statement stmt = con.createStatement();
	                    	stmt.executeUpdate("USE LIBRARY");
	                    	stmt.executeUpdate("INSERT INTO ISSUED(UID,BID,ISSUED_DATE,PERIOD) VALUES ('" + uid + "','" + bid + "','" + issued_date + "'," + period_int + ")");
	                    	JOptionPane.showMessageDialog(null,"Book Issued!");
	                    	g.dispose();
	                    }
	                    catch (SQLException ex) {
	                         JOptionPane.showMessageDialog(null, ex);
	                    }             
	    			}
	    		});
	                     
	    		g.add(l3);
	    		g.add(l4);
	    		g.add(createBut);
	    		g.add(l1);
	    		g.add(l2);
	    		g.add(fUID);
	    		g.add(fBid);
	    		g.add(fPeriod);	
	    		g.add(fIssue);
	    		g.setSize(350,250); 
	    		g.setLayout(null);
	    		g.setVisible(true);
	    		g.setLocationRelativeTo(null);     
	    	}
	    });
	    
	    
	    JButton returnBook=new JButton("Return Book");
	    returnBook.setBounds(280,60,160,25); 
	    returnBook.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){
	                 
	    		final JFrame g = new JFrame("Enter Details");
	    		//g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    		JLabel l1, l2, l3, l4;  
	    		l1 = new JLabel("Issue ID(IID)"); 
	    		l1.setBounds(30,15, 100,30);    
	    		l4 = new JLabel("Return Date(DD-MM-YYYY)");  
	    		l4.setBounds(30,50, 150,30); 
	                
	    		final JTextField fIid = new JTextField();
	    		fIid.setBounds(110, 15, 200, 30);     
	    		final JTextField fReturn=new JTextField();
	    		fReturn.setBounds(180, 50, 130, 30); 
	 
	    		JButton createBut = new JButton("Return");
	    		createBut.setBounds(130,170,80,25);
	    		createBut.addActionListener(new ActionListener() {         
	    			public void actionPerformed(ActionEvent e){   
	    				String iid = fIid.getText();
	    				String returnDate = fReturn.getText();
	    				Connection con = connect();
	                    try {
	                    	Statement stmt = con.createStatement();
	                    	stmt.executeUpdate("USE LIBRARY");
	                    	String date1 = null;
	                    	String date2 = returnDate;
	                    	int days = 0;
	                    	ResultSet rs = stmt.executeQuery("SELECT ISSUED_DATE FROM ISSUED WHERE IID=" + iid);
	                    	while (rs.next()) {
	                    		date1 = rs.getString(1);
	                    	}
	                    	try {
	                    		Date date_1 = (Date) new SimpleDateFormat("dd-MM-yyyy").parse(date1);
	                    		Date date_2 = (Date) new SimpleDateFormat("dd-MM-yyyy").parse(date2);

	                    		long diff = date_2.getTime() - date_1.getTime();
	                            days = (int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));	                             
	                        } 
	                    	catch (ParseException ex) {
	                            ex.printStackTrace();
	                    	}	                     
	                    	stmt.executeUpdate("UPDATE ISSUED SET RETURN_DATE='" + returnDate + "' WHERE IID=" + iid);
	                    	g.dispose();
	                
	                    	Connection con1 = connect();
	                    	Statement stmt1 = con1.createStatement();
	                    	stmt1.executeUpdate("USE LIBRARY");                
	                    	ResultSet rs1 = stmt1.executeQuery("SELECT PERIOD FROM ISSUED WHERE IID=" + iid);
	                    	String diff = null; 
	                    	while (rs1.next()) {
	                    		diff = rs1.getString(1);  
	                    	}
	                    	int diffInt = Integer.parseInt(diff);
	                    	if(days > diffInt) {
	                    		int fine = (days-diffInt)*10; 
	                    		stmt1.executeUpdate("UPDATE ISSUED SET FINE=" + fine + " WHERE IID=" + iid);  
	                    		String fineStr = "Fine: Rs. " + fine;
	                    		JOptionPane.showMessageDialog(null, fineStr); 
	                    	}
	                    	JOptionPane.showMessageDialog(null, "Book Returned!");
	                    }
	                    catch (SQLException ex) {
	                    	JOptionPane.showMessageDialog(null, ex);
	                    }    
	    			}         
	    		}); 
	    		g.add(l4);
	    		g.add(createBut);
	    		g.add(l1);
	    		g.add(fIid);
	    		g.add(fReturn);
	    		g.setSize(350,250); 
	    		g.setLayout(null);  
	    		g.setVisible(true);
	    		g.setLocationRelativeTo(null);              
	    	}
	    });
	    
	    f.add(returnBook);
	    f.add(issueBook);
	    f.add(addBook);
	    f.add(issuedBut);
	    f.add(usersBut);
	    f.add(viewBut);
	    f.add(addUser);
	    f.setSize(600,200);
	    f.setLayout(null);
	    f.setVisible(true);
	    f.setLocationRelativeTo(null);
	}
}
