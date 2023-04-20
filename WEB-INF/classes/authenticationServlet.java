/* Name: Christopher Santiago
 Course: CNT 4714 – Spring 2023 – Project Four
 Assignment title: A Three-Tier Distributed Web-Based Application
 Date: April 23, 2023
*/

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.Scanner;

@SuppressWarnings("serial")
public class authenticationServlet extends HttpServlet{
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException  {
			String inBoundUserName = request.getParameter("username");
			String inBoundPassword = request.getParameter("password");
			
			boolean userCredentialsOK = false;
			File file = new File(this.getServletContext().getRealPath("WEB-INF/lib/credentials.txt"));
			Scanner scanner;
			try {
				scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					   final String lineFromFile = scanner.nextLine();
					   String[] credentials = lineFromFile.split(",");
					   if(credentials[0].equals(inBoundUserName) && credentials[1].equals(inBoundPassword)) {
						   userCredentialsOK = true;
						   break;
					   }
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if(userCredentialsOK) {
				response.sendRedirect(inBoundUserName + "Home.jsp");
			} else {
				response.sendRedirect("error-page.html");
			}
	   } //end doGet() method
}
