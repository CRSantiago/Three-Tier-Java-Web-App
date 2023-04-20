/* Name: Christopher Santiago
 Course: CNT 4714 – Spring 2023 – Project Four
 Assignment title: A Three-Tier Distributed Web-Based Application
 Date: April 23, 2023
*/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class ShipmentInsertServlet  extends HttpServlet {
	private int numRowsUpdated;
	private boolean businesLogicDetected;
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException  {
		String inBoundButton = request.getParameter("action").trim();
		String inBoundsnum = request.getParameter("snum").trim();
		String inBoundpnum = request.getParameter("pnum").trim();
		String inBoundjnum = request.getParameter("jnum").trim();
		String inBoundQuantity = request.getParameter("quantity").trim();
		
		
		//establish a connection to the dataSource - the database
		if(inBoundButton.equals("execute")) {
			MysqlDataSource dataSource = setDataSource();
			try {
				Connection connection = dataSource.getConnection();
				
				String insertString = "insert into shipments values (? ,? ,?,?);";
				PreparedStatement insertStatement = connection.prepareStatement(insertString);
				insertStatement.setString(1, inBoundsnum);
				insertStatement.setString(2, inBoundpnum);
				insertStatement.setString(3, inBoundjnum);
				insertStatement.setInt(4, Integer.parseInt(inBoundQuantity));
				
				setNumRowsUpdated(insertStatement.executeUpdate());
				
				/*
	             * business/application logic
	             * This logic will increment by 5, the status of a supplier anytime that supplier is involved in the insertion/update of a shipment record in which the quantity is greater than or equal to 100. 
	             * 
	             * */
				int quantity = Integer.parseInt(inBoundQuantity);
	            if (quantity >= 100) {
	            	setBusinesLogicDetected(true);
	                // Prepare the SQL statement to update the status of the affected supplier
	            	String updateString = "UPDATE suppliers SET status = status + 5 WHERE snum = ?";
	                PreparedStatement updateStatement = connection.prepareStatement(updateString);
	                updateStatement.setString(1, inBoundsnum);
	                
	                // Execute the update statement
	                updateStatement.executeUpdate();
	            }
				
				String confirmString = "New shipment record: (" + inBoundsnum + ", " + inBoundpnum + ", " + inBoundjnum + ", " + inBoundQuantity + ") - successfully entered into database.";
				returnToClient(request, response, confirmString);
			} catch (SQLException e) {
				// sqlexception. return error message for user
				returnSQLException(request, response, e );
			}
		}
	}
	
	private MysqlDataSource setDataSource() {
		MysqlDataSource dataSource = null;
		FileInputStream filein = null;
		Properties properties = new Properties();
		
		try {
			filein = new FileInputStream(this.getServletContext().getRealPath("WEB-INF/lib/dataEntry.properties"));
			
			try {
				properties.load(filein);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			dataSource = new MysqlDataSource();
			dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
			dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
			dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSource;
	} //end of setDataSource
	
	private void returnSQLException(HttpServletRequest request, HttpServletResponse response, SQLException e1) throws ServletException, IOException {
		String returnMessage = "";
		returnMessage = e1.getMessage();
		
		HttpSession session = request.getSession();
		session.setAttribute("returnMessage", returnMessage);
		session.setAttribute("rowsUpdatedMessage", null); //ensure return message is blank
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/dataEntryHome.jsp");
		dispatcher.forward(request, response);
		e1.printStackTrace();
	} // end of returnSQLException
	
	private void returnToClient(HttpServletRequest request, HttpServletResponse response, String returnMessage) throws ServletException, IOException {
		StringBuilder rowsUpdatedMessageBuilder = null;
		String rowsUpdatedMessage = null;
		
		// populate builder with return message
		if(this.numRowsUpdated > 0) {
			rowsUpdatedMessageBuilder = new StringBuilder();
			rowsUpdatedMessageBuilder.append("<p>"+ returnMessage +"</p>");
			
			if( getBusinesLogicDetected() == true) {
				rowsUpdatedMessageBuilder.append("<p>Business Logic triggered!</p>");
			} else {
				rowsUpdatedMessageBuilder.append("<p>Business Logic not triggered!</p>");
			}
		}
		
		// if the builder is not equal to null, assign the builder to a String to avoid type conflicts with jsp file.
		if(rowsUpdatedMessageBuilder != null) {
			rowsUpdatedMessage = rowsUpdatedMessageBuilder.toString();
		}
		
		HttpSession session = request.getSession();
		session.setAttribute("rowsUpdatedMessage", rowsUpdatedMessage); //ensure return message is blank
		session.setAttribute("returnMessage", null);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/dataEntryHome.jsp");
		dispatcher.forward(request, response);
	} // end of returnToClient
	
	
	private void setNumRowsUpdated(int numRowsUpdated) {
		this.numRowsUpdated = numRowsUpdated;
	}
	
	private void setBusinesLogicDetected(boolean businesLogicDetected) {
		this.businesLogicDetected = businesLogicDetected;
	}
	
	private boolean getBusinesLogicDetected() {
		return this.businesLogicDetected;
	}
	
}

