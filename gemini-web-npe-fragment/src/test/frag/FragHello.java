
package test.frag;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello-frag")
public class FragHello extends HttpServlet {

	private static final long serialVersionUID = 8182131927020663067L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.println("<html>Hello, fragment!</html>");
		writer.flush();
	}

}
