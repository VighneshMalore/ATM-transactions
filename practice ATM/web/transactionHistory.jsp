<%@page import="java.sql.Timestamp"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<!DOCTYPE html>
<html>
<head>
    <title>Transaction History</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <style>
        .container { margin: 50px auto; max-width: 600px; }
        .atm-card { border: 3px solid #73AD21; border-radius: 15px; padding: 30px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="atm-card">
            <h2 class="text-center mb-4">Transaction History</h2>
            
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Transaction ID</th>
                        <th>Transaction Type</th>
                        <th>Amount</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        // Assuming you have a Connection and ResultSet to get the transactions
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bar?useSSL=false", "root", "root75");
                        String accno = (String) session.getAttribute("accno");
                        String query = "SELECT * FROM transactions WHERE accno = ? ORDER BY transaction_date DESC";
                        PreparedStatement pst = con.prepareStatement(query);
                        pst.setString(1, accno);
                        ResultSet rs = pst.executeQuery();
                        
                        while (rs.next()) {
                            int transactionId = rs.getInt("transaction_id");
                            String transactionType = rs.getString("transaction_type");
                            double amount = rs.getDouble("amount");
                            Timestamp transactionDate = rs.getTimestamp("transaction_date");
                    %>
                    <tr>
                        <td><%= transactionId %></td>
                        <td><%= transactionType %></td>
                        <td><%= amount %></td>
                        <td><%= transactionDate %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>

            <div class="text-center">
                <a href="secondservlet" class="btn btn-secondary">Back to Dashboard</a>
            </div>
        </div>
    </div>
</body>
</html>
