import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DisplayBalServlet")
public class DisplayBalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);  
        String accno = (session != null) ? (String) session.getAttribute("accno") : null;

        if (accno == null) {
            response.sendRedirect("index.html?error=Please%20log%20in%20first");
            return;
        }

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bar?useSSL=false", "root", "root75");
            String query = "SELECT balance FROM account_holder WHERE accno = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, accno);
            ResultSet rs = pst.executeQuery();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<html><head>");
            out.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">");
            out.println("<style>.container { margin: 50px auto; max-width: 600px; } .atm-card { border: 3px solid #73AD21; border-radius: 15px; padding: 30px; }</style>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            out.println("<div class='atm-card'>");
            out.println("<h2 class='text-center mb-4'>Account Balance</h2>");

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                out.println("<p class='text-center'>Account Number: " + accno + "</p>");
                out.println("<p class='text-center'>Current Balance: $ " + balance + "</p>");
            } else {
                out.println("<p class='text-center text-danger'>Account not found</p>");
            }

            out.println("<div class='text-center'>");
            out.println("<a href='secondservlet' class='btn btn-secondary m-2'>Back to Dashboard</a>");
            out.println("</div>");
            
            out.println("</div></div></body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("secondservlet?error=Database%20error");
        }
    }
}
