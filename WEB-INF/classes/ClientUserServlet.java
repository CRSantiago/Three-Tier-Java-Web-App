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
		String returnMessage = null;
		String returnMessage2 = sqlStatement;
		StringBuilder html = null;
		
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
				// check button type
				if(inBoundButton.equals("execute")) {
					if(sqlStatement.contains("select")) {
						returnMessage = "select query is found.. executing";
						stmt = connection.createStatement();
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
			            html.append("<tr><th>Column 1</th><th>Column 2</th><th>Column 3</th><th>Column 4</th></tr>");
			            while (rs.next()) {
			                html.append("<tr>");
			                html.append("<td>").append(rs.getString(columnNames[0])).append("</td>");
			                html.append("<td>").append(rs.getString(columnNames[1])).append("</td>");
			                html.append("<td>").append(rs.getString(columnNames[2])).append("</td>");
			                html.append("<td>").append(rs.getString(columnNames[3])).append("</td>");
			                html.append("</tr>");
			            }
			            html.append("</table>");
			            
					}
				}
				String testing = request.getParameter("testing");
				HttpSession session = request.getSession();
				session.setAttribute("testing", returnMessage);
				session.setAttribute("testingStatement", returnMessage2);
				session.setAttribute("table", html.toString());
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientHome.jsp");
				dispatcher.forward(request, response);
						
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				HttpSession session = request.getSession();
				session.setAttribute("testing", "an error has occured in statement execution");
				session.setAttribute("testingStatement", returnMessage2);
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientHome.jsp");
				dispatcher.forward(request, response);
				e1.printStackTrace();
			} 
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

}
