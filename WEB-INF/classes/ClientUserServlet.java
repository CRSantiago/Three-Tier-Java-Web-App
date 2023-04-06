import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
public class ClientUserServlet extends HttpServlet{
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException  {
//		String testing = request.getParameter("testing");
//		HttpSession session = request.getSession();
//		session.setAttribute("testing is working", testing);
//		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("clientHome.jsp");
//		dispatcher.forward(request, response);
		
		String inBoundButton = request.getParameter("action").trim();
		String sqlStatement = request.getParameter("sqlStatement").trim();
		Connection connection = null;
		MysqlDataSource dataSource = null;
		Statement stmt = null;
        ResultSet rs = null;
		Properties properties = new Properties();
		FileInputStream filein = null;
		String returnMessage = "";
		StringBuilder html = null;
		
		// check button type
		if(inBoundButton.equals("execute")) {
			try {
				//set property and file object
				filein = new FileInputStream(this.getServletContext().getRealPath("WEB-INF/lib/client.properties"));
				properties.load(filein);
					
				dataSource = new MysqlDataSource();
				dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
				dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
				dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
						
				try {
					//establish a connection to the dataSource - the database
					connection = dataSource.getConnection();
					stmt = connection.createStatement();
						if(sqlStatement.contains("select")) {
							try {
					            rs = stmt.executeQuery(sqlStatement);
					            
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
					            // test to see if parts was request. parts contains 5 columns where the other tables have 4 columns. 
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
					            
					         // use session to return table and insert into table variable found in clientHome.jsp
								HttpSession session = request.getSession();
								session.setAttribute("returnMessage", null); //ensure return message is blank
								session.setAttribute("table", html.toString());
								RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientHome.jsp");
								dispatcher.forward(request, response);
					            
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								// sqlexception. return error message for user
								returnMessage = e1.getMessage();
								HttpSession session = request.getSession();
								session.setAttribute("returnMessage", returnMessage);
								session.setAttribute("table", null); //ensure table is blank
								RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientHome.jsp");
								dispatcher.forward(request, response);
								e1.printStackTrace();
							} 
						} else {
							try {
								int numRowUpdated = stmt.executeUpdate(sqlStatement);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								// sqlexception. return error message for user
								returnMessage = e1.getMessage();
								HttpSession session = request.getSession();
								session.setAttribute("returnMessage", returnMessage);
								session.setAttribute("table", null); //ensure table is blank
								RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientHome.jsp");
								dispatcher.forward(request, response);
								e1.printStackTrace();
							} 
						}
						
							
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					// sqlexception. return error message for user
					returnMessage = e1.getMessage();
					HttpSession session = request.getSession();
					session.setAttribute("returnMessage", returnMessage);
					session.setAttribute("table", null); //ensure table is blank
					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientHome.jsp");
					dispatcher.forward(request, response);
					e1.printStackTrace();
				} 
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
	}

}
