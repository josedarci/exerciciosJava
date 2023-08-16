import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LoginService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    public String authenticateUser(String email, String password) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Query to check if the user exists with the given email and password
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Generate a unique token for authentication
                String authToken = generateAuthToken();
                resultSet.close();
                
                // Store the token in the database (assuming there's a users table)
                String updateQuery = "UPDATE users SET auth_token = ? WHERE email = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, authToken);
                updateStatement.setString(2, email);
                updateStatement.executeUpdate();
                updateStatement.close();
                
                connection.close();
                return authToken;
            }
            
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        LoginService loginService = new LoginService();
        String email = "user@example.com";
        String password = "password123";
        
        String authToken = loginService.authenticateUser(email, password);
        if (authToken != null) {
            System.out.println("User authenticated. Auth Token: " + authToken);
        } else {
            System.out.println("Authentication failed.");
        }
    }
}
