

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 


/**
 * Servlet implementation class URLE
 */
@WebServlet("/URLE")
public class URLE extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public URLE() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String url = "jdbc:mysql://localhost:3306/urlshortener";
		String username = "java";
		String password = "password";
		String x = null;
		String parameter = null;
		
		try{
        	 parameter = request.getParameter("a");
        	        	
        } catch(Exception e) {
        	System.out.println("Error:" + e.getMessage() + "<br>" + e.toString());
        }
		System.out.println(parameter == null);
		if (parameter == null) {
    		System.out.println("No parameter supplied");
		}
		else{
	    	try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(url,username,password);
				System.out.println("Database Connected!");
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM link WHERE linkid = '" + parameter + "'");
				while(rs.next()) {
					String linkID = rs.getString("linkid");
					String originalUrl = rs.getString("originalurl");
					System.out.println("LinkId:" + linkID + " originalURL:" + originalUrl);
					x = originalUrl;
				}
				
				rs.close();
				stmt.close();
				connection.close();			
				
			} catch (SQLException e) {			
				//throw new IllegalStateException("Cannot connect the database!", e); Works, but throws big error. Let's soften this up.
				response.setContentType("text/html");
		        PrintWriter out = response.getWriter();
		        out.println("<html>");
		        out.println("<head>");
		        out.println("</head>");
		        out.println("<body>");
		        out.println("Database not connected. Aborting.");
		        out.println("Get Message: " + e.getMessage() + "<br>" + "getSQLState: " + e.getSQLState());
		        
		        out.println("</body>");
		        out.println("</html>");
		        return;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				PrintWriter out = response.getWriter();
		        out.println("Class Not Found Exception: ");  
		        e.printStackTrace(out);
		        return;
			}	
		}
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        if (x == null) { // No parameter supplied - Load full page
        	// TODO: Design full page link entry
	        out.println("<h3>URL Shortener</h3>");
	        
	        out.println("<title>URL Shortener - By Bu</title>");
	        out.println("</head>");
	        out.println("<body>");
	        out.println("<P>");
	        out.println("<form method=\"post\">Enter URL to Shorten: <input type=\"text\" name=\"urlToShorten\" size = 70 id=\"urlToShorten\" /> <br>");
	        out.println("<input type=\"submit\" value=\"Submit\" /> <br>");
	        out.println("</form>");
        }        
        else {
        	if ( !x.matches("http*") && !x.matches("localhost*") ) {
        		x = "http://" + x;        	        	
        	}        	
        	out.println("<meta http-equiv=\"refresh\" content=\"1; URL=" + x + "\">");
        	out.println("<script type=\"text/javascript\">");
        	out.println("window.location.href = \"" + x + "\"");
        	out.println("</script>");
        	out.println("<title> Page Redirection</title>");        	
        	out.println("</head>");
        	out.println("<body>");            
        	out.println("If you are not redirected automatically, follow this <a=href='" + x + "'>link.</a>");
        }
        
        out.println("</body>");
        out.println("</html>");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String url = "jdbc:mysql://localhost:3306/urlshortener";
		String username = "java";
		String password = "password";
		String parameter = null;
		String linkId = null;
		
		try{
        	parameter = request.getParameter("urlToShorten");
        	if (parameter == null) {
        		System.out.println("No parameter supplied");
        	}        	
        } catch(Exception e) {
        	System.out.println("Error:" + e.getMessage() + "<br>" + e.toString());
        }
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url,username,password);
			System.out.println("Database Connected!");
			Statement stmt = connection.createStatement();
			linkId = generateKey();
			int result = stmt.executeUpdate("INSERT INTO link (linkid, originalurl) VALUES ('" + linkId + "', '" + parameter + "')");
			System.out.println ("Result By BU:" + result);
			stmt.close();
			connection.close();			
			
		} catch (SQLException e) {			
			//throw new IllegalStateException("Cannot connect the database!", e); Works, but throws big error. Let's soften this up.
			response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
	        out.println("<html>");
	        out.println("<head>");
	        out.println("</head>");
	        out.println("<body>");
	        out.println("Database not connected. Aborting.");
	        out.println("Get Message: " + e.getMessage() + "<br>" + "getSQLState: " + e.getSQLState());
	        
	        out.println("</body>");
	        out.println("</html>");
	        return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			PrintWriter out = response.getWriter();
	        out.println("Class Not Found Exception: ");  
	        e.printStackTrace(out);
	        return;
		}		
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        {
        	out.println("<title> Done!</title>");        	
        	out.println("</head>");
        	out.println("<body>");            
        	out.println("Address has been added! key is " + linkId);
        }
        
        out.println("</body>");
        out.println("</html>");
	}
	
	public String filter(String message) {

        if (message == null)
            return (null);

        char content[] = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuffer result = new StringBuffer(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            default:
                result.append(content[i]);
            }
        }
        return (result.toString());

    }
	
	public String generateKey() {
		final String CHARLIST = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		int rndLength = random.nextInt(10);
		for(int x = 0; x < rndLength; x++) {
			sb.append(CHARLIST.charAt(random.nextInt(CHARLIST.length() - 1)));
		}
		return sb.toString();
	}

}
