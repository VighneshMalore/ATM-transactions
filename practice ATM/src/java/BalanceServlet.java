
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BalanceServlet")
public class BalanceServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String accno = (String) getServletContext().getAttribute("accno");

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bar?useSSL=false", "root", "root75");
            
            // Get current balance
            PreparedStatement balanceStmt = con.prepareStatement("SELECT mdeposit FROM account_holder WHERE accnum = ?");
            balanceStmt.setString(1, accno);
            ResultSet rs = balanceStmt.executeQuery();
            rs.next();
            double balance = rs.getDouble("mdeposit");
            
            // Get transaction history
            PreparedStatement historyStmt = con.prepareStatement("SELECT * FROM transactions WHERE accnum = ? ORDER BY date DESC");
            historyStmt.setString(1, accno);
            ResultSet historyRs = historyStmt.executeQuery();

            // Display results
            out.println("<html><head>");
            out.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">");
            out.println("<style>.container { margin: 50px auto; max-width: 800px; } .atm-card { border: 3px solid #73AD21; border-radius: 15px; padding: 30px; }</style>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            out.println("<div class='atm-card'>");
            out.println("<h2 class='text-center mb-4'>Account Statement</h2>");
            out.println("<h4>Current Balance: ₹" + balance + "</h4>");
            
            out.println("<h5 class='mt-4'>Transaction History:</h5>");
            out.println("<table class='table'>");
            out.println("<tr><th>Date</th><th>Type</th><th>Amount</th></tr>");
            
            while(historyRs.next()) {
                out.println("<tr>");
                out.println("<td>" + historyRs.getDate("date") + "</td>");
                out.println("<td>" + historyRs.getString("type") + "</td>");
                out.println("<td>₹" + historyRs.getDouble("amount") + "</td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
            out.println("<a href='secondservlet' class='btn btn-primary'>Back to Dashboard</a>");
            out.println("</div></div></body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}