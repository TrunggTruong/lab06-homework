package dao;

import model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UserDAO {
    
    // Database connection details constants
    private static final String DB_URL = 
    "jdbc:mysql://localhost:3306/student_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "12345"; 
    
    // SQL query constants
    // Query to fetch user details by username, only if the user is active.
    private static final String SQL_AUTHENTICATE = 
        "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
    
    // Query to update the last_login timestamp for a specific user ID.
    private static final String SQL_UPDATE_LAST_LOGIN = 
        "UPDATE users SET last_login = NOW() WHERE id = ?";
    
    // Query to retrieve all details of a user by their ID.
    private static final String SQL_GET_BY_ID = 
        "SELECT * FROM users WHERE id = ?";
    
    /**
     * Establishes a connection to the database.
     * @return A valid SQL Connection object.
     * @throws SQLException If a database access error occurs or the driver is not found.
     */
    private Connection getConnection() throws SQLException {
        // Explicitly load the driver (optional for modern JDBC but good practice)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Handle case where the MySQL JDBC Driver is missing
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        // Return the connection using DriverManager
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Authenticates a user by checking their username and verifying the password hash.
     * @param username The user's provided username.
     * @param password The user's provided plain-text password.
     * @return The User object if authentication is successful, otherwise null.
     */
    public User authenticate(String username, String password) {
        User user = null;
        // Use try-with-resources for automatic closing of Connection and PreparedStatement
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_AUTHENTICATE)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 1. Map ResultSet to a User object to get the stored hashed password
                    user = mapResultSetToUser(rs);
                    
                    // 2. Get the hashed password stored in the database
                    String hashedPassword = user.getPassword();
                    
                    // 3. Use BCrypt.checkpw() to safely verify the plain password against the hash
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        // 4. If valid, update the last login time
                        updateLastLogin(user.getId());
                        return user; // Authentication successful
                    }
                    // If password verification fails, execution continues, returning null
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database Error during authentication: " + e.getMessage());
        }
        // 5. Returns null if user is not found, inactive, or password verification fails
        return null;
    }
    
    /**
     * Updates the last_login timestamp for a specific user.
     * @param userId The ID of the user whose last login time needs updating.
     */
    private void updateLastLogin(int userId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_LAST_LOGIN)) {
            
            ps.setInt(1, userId);
            ps.executeUpdate(); // Execute the update query
            
        } catch (SQLException e) {
            // Log the error but don't stop the application flow
            System.err.println("Error updating last login for user ID: " + userId + " - " + e.getMessage());
        }
    }
    
    /**
     * Retrieves a User object based on their unique ID.
     * @param id The ID of the user to retrieve.
     * @return The User object if found, otherwise null.
     */
    public User getUserById(int id) {
        User user = null;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_BY_ID)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs); // Map the row data to a User object
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    /**
     * Helper method to convert a database row (ResultSet) into a User Java object.
     * @param rs The ResultSet containing user data.
     * @return A fully populated User object.
     * @throws SQLException If there's an issue accessing column data.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password")); // Needed temporarily for BCrypt check
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }

   
        }
    
    

