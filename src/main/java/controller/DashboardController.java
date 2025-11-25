package controller;

import com.student.dao.StudentDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/dashboard", "/user/dashboard", "/admin/dashboard"})
public class DashboardController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        // Initialize StudentDAO object
        studentDAO = new StudentDAO(); 
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get the current session, do NOT create a new one if it doesn't exist (false)
        HttpSession session = request.getSession(false);
        
        // 1. Check for active session and login status
        if (session == null || session.getAttribute("user") == null) {
            // If session is null or 'user' attribute is missing, redirect to login
            response.sendRedirect("login?error=Session expired or not logged in.");
            return;
        }
        
        // Retrieve the authenticated User object from the session
        User user = (User) session.getAttribute("user");
        
        try {
            // 2. Get statistics (total students)
            // Assumes StudentDAO has a method to count all students
            int totalStudents = studentDAO.getTotalStudents();
            
            // 3. Set attributes for the JSP
            // Set the User object to display user info
            request.setAttribute("user", user); 
            // Set the statistical data
            request.setAttribute("totalStudents", totalStudents); 
            
            // 4. Forward to dashboard.jsp
            // Use RequestDispatcher to preserve request attributes
            request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Handle database errors or other exceptions during data retrieval
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/views/error.jsp").forward(request, response); // Forward to an error page
        }
    }
    
 
}