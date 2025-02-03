import java.io.IOException;
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

@WebServlet("/servlet1")
public class servlet1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to the login page with an error message
        response.sendRedirect("index.html?error=GET%20method%20not%20allowed");
    }
  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Get input parameters
            String accno = request.getParameter("accno");
            String pinno = request.getParameter("pinno");

            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/bar?useSSL=false", "root", "root75");

            // SQL Query to check login credentials
            pst = con.prepareStatement("SELECT * FROM login WHERE accno = ? AND pinno = ?");
            pst.setString(1, accno);
            pst.setString(2, pinno);
            rs = pst.executeQuery();

            if (rs.next()) {
                // ✅ Successful login: Store accno in session
                HttpSession session = request.getSession();
                session.setAttribute("accno", accno);

                // Redirect to secondservlet directly
                response.sendRedirect("secondservlet");
            } else {
                // ❌ Login failed: Redirect to login page with error message
                response.sendRedirect("index.html?error=Invalid%20Account%20or%20PIN");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            response.sendRedirect("index.html?error=Database%20error.%20Please%20try%20again.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
            }
        }
    }
}