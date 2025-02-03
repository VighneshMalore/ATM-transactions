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

@WebServlet("/TransactionServlet")
public class TransactionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);  
        String accno = (session != null) ? (String) session.getAttribute("accno") : null;

        if (accno == null) {
            response.sendRedirect("index.html?error=Please%20log%20in%20first");
            return;
        }

        String transactionType = request.getParameter("transactionType");
        double amount = Double.parseDouble(request.getParameter("amount"));

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bar?useSSL=false", "root", "root75");

            // Process Deposit
            if (transactionType.equals("deposit")) {
                String depositQuery = "UPDATE account_holder SET balance = balance + ? WHERE accno = ?";
                PreparedStatement pst = con.prepareStatement(depositQuery);
                pst.setDouble(1, amount);
                pst.setString(2, accno);
                int rows = pst.executeUpdate();

                if (rows > 0) {
                    // Insert transaction into the 'transactions' table
                    String transactionQuery = "INSERT INTO transactions (accno, transaction_type, amount) VALUES (?, ?, ?)";
                    PreparedStatement transactionStmt = con.prepareStatement(transactionQuery);
                    transactionStmt.setString(1, accno);
                    transactionStmt.setString(2, transactionType);
                    transactionStmt.setDouble(3, amount);
                    transactionStmt.executeUpdate();

                    // Successful deposit
                    response.sendRedirect("secondservlet?success=Amount%20Deposited%20Successfully");
                } else {
                    // Database error during deposit
                    response.sendRedirect("secondservlet?error=Deposit%20failed");
                }

            } 
            // Process Withdrawal
            else if (transactionType.equals("withdraw")) {
                // First, check if user has sufficient balance
                String balanceQuery = "SELECT balance FROM account_holder WHERE accno = ?";
                PreparedStatement balanceStmt = con.prepareStatement(balanceQuery);
                balanceStmt.setString(1, accno);
                ResultSet rs = balanceStmt.executeQuery();
                
                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");

                    // Check if sufficient balance is available for withdrawal
                    if (currentBalance >= amount) {
                        String withdrawQuery = "UPDATE account_holder SET balance = balance - ? WHERE accno = ?";
                        PreparedStatement withdrawStmt = con.prepareStatement(withdrawQuery);
                        withdrawStmt.setDouble(1, amount);
                        withdrawStmt.setString(2, accno);
                        int rows = withdrawStmt.executeUpdate();

                        if (rows > 0) {
                            // Insert transaction into the 'transactions' table
                            String transactionQuery = "INSERT INTO transactions (accno, transaction_type, amount) VALUES (?, ?, ?)";
                            PreparedStatement transactionStmt = con.prepareStatement(transactionQuery);
                            transactionStmt.setString(1, accno);
                            transactionStmt.setString(2, transactionType);
                            transactionStmt.setDouble(3, amount);
                            transactionStmt.executeUpdate();

                            // Successful withdrawal
                            response.sendRedirect("secondservlet?success=Withdrawal%20Successful");
                        } else {
                            // Error in updating balance
                            response.sendRedirect("secondservlet?error=Withdrawal%20failed");
                        }
                    } else {
                        // Insufficient balance
                        response.sendRedirect("secondservlet?error=Insufficient%20balance");
                    }
                } else {
                    // Account not found
                    response.sendRedirect("secondservlet?error=Account%20not%20found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("secondservlet?error=Transaction%20failed%20due%20to%20database%20error");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("secondservlet?error=Invalid%20amount");
        }
    }
}
