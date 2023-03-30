import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.Scanner;

@SuppressWarnings("serial")
public class authenticationServlet extends HttpServlet{
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException  {
			String inBoundUserName = request.getParameter("username");
			String inBoundPassword = request.getParameter("password");
			
//			this.getServletContext().getRealPath("WEB-INF/lib/credentials.txt");
			boolean userCredentialsOK = false;
//			File file = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1_Tomcat1017\\webapps\\CNT4714-Project4\\WEB-INF\\lib\\credentials.txt");
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(userCredentialsOK) {
				response.sendRedirect(inBoundUserName + "Home.jsp");
			} else {
				response.sendRedirect("error-page.html");
			}
			
	   } //end doGet() method
}
