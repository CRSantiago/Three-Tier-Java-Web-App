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
			File file = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1_Tomcat1017\\webapps\\CNT4714-Project4\\WEB-INF\\lib\\credentials.txt");
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
				response.sendRedirect(inBoundUserName + "Home.html");
			} else {
				response.sendRedirect("error-page.html");
			}
			
//		    out.println("</script>");
			
//			response.setContentType( "text/html" );
//		      PrintWriter out = response.getWriter();  
		      
//		      // send HTML5 page to client
//		      // start HTML5 document
//		      out.println("<html>");
//		      out.println( "<meta charset=\"utf-8\">" );
//		           // head section of document
//		      out.println( "<head>" );
//			  out.println( "<style type='text/css'>");
//			  out.println( "<!--  body{background-color:black; color:white; font-family: Verdana, Arial, sans-serif; text-align: center;}");
//			  out.println( " h1{font-size:100pt; text-align:center;} h2{font-family:inherit; font-size:60pt;}");
//			  out.println( " #one{color:magenta;} #two{color:yellow;} #three{color:red;} #four{color:lime;} #five{color:cyan;}");
//			  out.println( "-->");
//			  out.println( "</style>");
//		      out.println( "<title>Welcome to Servlets!</title>" );
//		      out.println( "</head>" );
//		      // body section of document
//		      out.println( "<body>" );
//		      out.println( "<h1><span id=\"one\">H</span><span id=\"two\">e</span><span id=\"three\">l</span>"
//		      		+ "<span id=\"four\">l</span><span id=\"five\">o</span>!!</h1>");
//		      out.println( "<h2>Welcome To The Exciting World Of Servlet Technology!</h2>" );
//		      out.println("<p>Your credentials were " + userCredentialsOK);
//		      out.println( "</body>" );
//		      // end HTML5 document
//		      out.println( "</html>" );
//		      out.close();  // close stream to complete the page
	   } //end doGet() method
}
