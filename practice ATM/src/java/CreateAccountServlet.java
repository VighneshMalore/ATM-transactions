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

@WebServlet("/CreateAccountServlet")
public class CreateAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accno = request.getParameter("accno");
        String pinno = request.getParameter("pinno");
        String initialDepositStr = request.getParameter("initialDeposit");

        double initialDeposit = Double.parseDouble(initialDepositStr);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        PreparedStatement checkAccountStmt = null;
        PreparedStatement insertLoginStmt = null;
        PreparedStatement insertAccountHolderStmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bar?useSSL=false", "root", "root75");

            String checkAccountQuery = "SELECT * FROM account_holder WHERE accno = ?";
            checkAccountStmt = connection.prepareStatement(checkAccountQuery);
            checkAccountStmt.setString(1, accno);
            ResultSet rs = checkAccountStmt.executeQuery();

            if (rs.next()) {
                out.println("<h3>Account already exists. Please try with a different account number.</h3>");
            } else {
                String insertLoginQuery = "INSERT INTO login (accno, pinno) VALUES (?, ?)";
                insertLoginStmt = connection.prepareStatement(insertLoginQuery);
                insertLoginStmt.setString(1, accno);
                insertLoginStmt.setString(2, pinno);
                insertLoginStmt.executeUpdate();

                String insertAccountHolderQuery = "INSERT INTO account_holder (accno, balance) VALUES (?, ?)";
                insertAccountHolderStmt = connection.prepareStatement(insertAccountHolderQuery);
                insertAccountHolderStmt.setString(1, accno);
                insertAccountHolderStmt.setDouble(2, initialDeposit);
                insertAccountHolderStmt.executeUpdate();

                // ✅ Store accno in session after account creation
                HttpSession session = request.getSession();
                session.setAttribute("accno", accno);

                out.println("<h3>Account created successfully!</h3>");
                response.sendRedirect("dashboard.jsp");  // Redirect to user dashboard
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        } finally {
            try {
                if (checkAccountStmt != null) checkAccountStmt.close();
                if (insertLoginStmt != null) insertLoginStmt.close();
                if (insertAccountHolderStmt != null) insertAccountHolderStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
            }
        }
    }
}
