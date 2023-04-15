import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class RootUserServlet extends HttpServlet{
	private int numRowsUpdated;
	private boolean businesLogicDetected;
	private int businessLogicRowsUpdated;
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException  {		
		String inBoundButton = request.getParameter("action").trim();
		String sqlStatement = request.getParameter("sqlStatement").trim();
		Connection connection = null;
		Statement stmt = null;
		
		// check button type
		if(inBoundButton.equals("execute")) {		
			try {
				//establish a connection to the dataSource - the database
				MysqlDataSource dataSource = setDataSource();
				connection = dataSource.getConnection();
				stmt = connection.createStatement();
					if(sqlStatement.contains("select")) {
						try {
							ResultSet rs = stmt.executeQuery(sqlStatement);
					            
							// generate html based on results set
					        StringBuilder html = setHTMLTable(rs);
					        
					        setNumRowsUpdated(0);
					        
					        // return result data to client, no return message
					        returnToClient(request, response,null, html, sqlStatement );
					            
						} catch (SQLException e1) {
							// sqlexception. return error message for user
							returnSQLException(request, response, e1, sqlStatement );
						} 
					} else {
						// try another type of command other than select
						try {
							if (sqlStatement.contains("insert") && sqlStatement.contains("shipments")) {
							      String searchString = "values";
							      int index = sqlStatement.toLowerCase().indexOf(searchString);

							      if (index != -1) { // If the search string is found
							        String resultString = sqlStatement.substring(index + searchString.length()).replaceAll("[();]", ""); // get the values for the prepared statement and remove all extra characters
							        String[] resultSplit = resultString.split(",");
							        
						         // Execute the insert statement
						            setNumRowsUpdated(stmt.executeUpdate(sqlStatement));
						            
						            // Check if the inserted quantity is greater than or equal to 100
						            int quantity = Integer.parseInt(resultSplit[3].trim());
						            if (quantity >= 100) {
						            	setBusinesLogicDetected(true);
						                // Prepare the SQL statement to update the status of the affected supplier
						            	String updateString = "UPDATE suppliers SET status = status + 5 WHERE snum = ?";
						                PreparedStatement updateStatement = connection.prepareStatement(updateString);
						                updateStatement.setString(1, resultSplit[0].trim());
						                
						                // Execute the update statement
						                setBusinessLogicRowsUpdated(updateStatement.executeUpdate());
							      }
							    }
							} else {
								setBusinesLogicDetected(false);
								setNumRowsUpdated(stmt.executeUpdate(sqlStatement));
							}
							returnToClient(request, response, null, null, null );
						} catch (SQLException e1) {
							// sqlexception. return error message for user
							returnSQLException(request, response, e1, sqlStatement );
						} 
					} // end of else		
			} catch (SQLException e1) {
				// sqlexception. return error message for user
				returnSQLException(request, response, e1, sqlStatement );
			} 
		} // end of if execute button
		
		if(inBoundButton.equals("clear")) {
			// clear all results
			returnToClient(request, response,null, null, null );
		}
		
		if (inBoundButton.equals("reset")) {
			// clear all results
			returnToClient(request, response,null, null, null );
		}
	}// end of doPost
	
	private MysqlDataSource setDataSource() {
		MysqlDataSource dataSource = null;
		FileInputStream filein = null;
		Properties properties = new Properties();
		
		try {
			filein = new FileInputStream(this.getServletContext().getRealPath("WEB-INF/lib/root.properties"));
			
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
	
	private StringBuilder setHTMLTable( ResultSet rs) throws SQLException {
		StringBuilder html = null;
		 // Get the column names from the result set metadata
		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }
        
     // Build an HTML table with the results
        html = new StringBuilder();
        html.append("<table>");
        html.append("<tr>");
        // loop through columnNames and append th tags to html string
        for(int i = 0; i < columnNames.length; i++) {
        	html.append("<th>" + columnNames[i] + "</th>");
        }
        html.append("</tr>");
        //loop through results appending data to corresponding column
        while (rs.next()) {
            html.append("<tr>");
            for(int i = 0; i < columnNames.length; i++) {
            	html.append("<td>").append(rs.getString(columnNames[i])).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
		return html;
	}// end of setHTMLTable
	
	
	private void returnSQLException(HttpServletRequest request, HttpServletResponse response, SQLException e1, String sqlStatement) throws ServletException, IOException {
		String returnMessage = "";
		returnMessage = e1.getMessage();
		HttpSession session = request.getSession();
		session.setAttribute("returnMessage", returnMessage);
		session.setAttribute("rowsUpdatedMessage", null); //ensure return message is blank
		session.setAttribute("table", null); //ensure table is blank
		request.setAttribute("sqlStatement", sqlStatement);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/rootHome.jsp");
		dispatcher.forward(request, response);
		e1.printStackTrace();
	} // end of returnSQLException
	
	
	private void returnToClient(HttpServletRequest request, HttpServletResponse response, String returnMessage, StringBuilder table, String sqlStatement ) throws ServletException, IOException {
		String returnTable = null;
		if(table != null) {
			returnTable = table.toString();
		}
		StringBuilder rowsUpdatedMessageBuilder = null;
		String rowsUpdatedMessage = null;
		
		// populate builder with return message
		if(this.numRowsUpdated > 0) {
			rowsUpdatedMessageBuilder = new StringBuilder();
			rowsUpdatedMessageBuilder.append("<p>The statement executed successfully.</p>");
			rowsUpdatedMessageBuilder.append("<p>" + getNumRowsUpdated() + " rows(s) affected.</p>");
			if( getBusinesLogicDetected() == true) {
				rowsUpdatedMessageBuilder.append("<p>Business Logic Detected! - Updating Supplier Status.</p>");
				rowsUpdatedMessageBuilder.append("<p>" + getBusinessLogicRowsUpdated()+ " rows(s) affected.</p>");
			}
		}
		
		// if the builder is not equal to null, assign the builder to a String to avoid type conflicts with jsp file.
		if(rowsUpdatedMessageBuilder != null) {
			rowsUpdatedMessage = rowsUpdatedMessageBuilder.toString();
		}
		HttpSession session = request.getSession();
		session.setAttribute("rowsUpdatedMessage", rowsUpdatedMessage); //ensure return message is blank
		session.setAttribute("returnMessage", returnMessage);
		request.setAttribute("sqlStatement", sqlStatement);
		session.setAttribute("table", returnTable);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/rootHome.jsp");
		dispatcher.forward(request, response);
	} // end of returnToClient
	
	
	private void setNumRowsUpdated(int numRowsUpdated) {
		this.numRowsUpdated = numRowsUpdated;
	}
	
	private int getNumRowsUpdated() {
		return this.numRowsUpdated;
	}
	
	private void setBusinessLogicRowsUpdated(int businessLogicRowsUpdated) {
		this.businessLogicRowsUpdated = businessLogicRowsUpdated;
	}
	
	private int getBusinessLogicRowsUpdated() {
		return this.businessLogicRowsUpdated;
	}
	
	private void setBusinesLogicDetected(boolean businesLogicDetected) {
		this.businesLogicDetected = businesLogicDetected;
	}
	
	private boolean getBusinesLogicDetected() {
		return this.businesLogicDetected;
	}
}

