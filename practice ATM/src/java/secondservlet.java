import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@WebServlet("/secondservlet")
public class secondservlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false); 
        String accno = (session != null) ? (String) session.getAttribute("accno") : null;

        // Get success or error message from URL parameters
        String successMessage = request.getParameter("success");
        String errorMessage = request.getParameter("error");

        out.println("<html><head>");
        out.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">");
        out.println("<style>.container { margin: 50px auto; max-width: 600px; } .atm-card { border: 3px solid #73AD21; border-radius: 15px; padding: 30px; }</style>");
        out.println("</head><body>");
        
        out.println("<div class='container'>");
        out.println("<div class='atm-card'>");
        out.println("<h2 class='text-center mb-4'>Account Dashboard</h2>");
        
        if (accno == null) {
            out.println("<p class='text-center text-danger'>Session expired. Please <a href='index.html'>login again</a>.</p>");
        } else {
            out.println("<p class='text-center'>Account Number: " + accno + "</p>");
            
            // Display success message if available
            if (successMessage != null) {
                out.println("<p class='text-center text-success'>" + successMessage + "</p>");
            }
            
            // Display error message if available
            if (errorMessage != null) {
                out.println("<p class='text-center text-danger'>" + errorMessage + "</p>");
            }
            
             // Show transaction history
            out.println("<h4 class='text-center'>Transaction History</h4>");
            try {
                // Connect to database and fetch transactions
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bar?useSSL=false", "root", "root75");
                PreparedStatement pst = con.prepareStatement("SELECT * FROM transactions WHERE accno = ? ORDER BY transaction_date DESC");
                pst.setString(1, accno);
                ResultSet rs = pst.executeQuery();

                out.println("<table class='table table-bordered'><thead><tr><th>Transaction Type</th><th>Amount</th><th>Transaction Date</th></tr></thead><tbody>");
                while (rs.next()) {
                    out.println("<tr><td>" + rs.getString("transaction_type") + "</td><td>" + rs.getDouble("amount") + "</td><td>" + rs.getTimestamp("transaction_date") + "</td></tr>");
                }
                out.println("</tbody></table>");
            } catch (SQLException e) {
                out.println("<p class='text-center text-danger'>Error fetching transaction history</p>");
            }
            // Transaction Options
            out.println("<div class='text-center'>");
            out.println("<a href='deposit.html' class='btn btn-success m-2'>Deposit</a>");
            out.println("<a href='withdraw.html' class='btn btn-warning m-2'>Withdraw</a>");
            out.println("<a href='DisplayBalServlet' class='btn btn-info m-2'>Check Balance</a>");
            out.println("<a href='transactionHistory.jsp' class='btn btn-info'>Transaction History</a>");


            out.println("</div>");
        }     

        out.println("</div></div></body></html>");
    }
}
