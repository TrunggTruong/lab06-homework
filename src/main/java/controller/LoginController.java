package controller;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    
    private UserDAO userDAO;
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 minutes in seconds
    
    @Override
    public void init() {
        // TODO: Initialize userDAO
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // TODO: Check if already logged in
        // request.getSession(false) prevents creating a new session if one doesn't exist
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // Already logged in, redirect to dashboard
            response.sendRedirect("dashboard"); // Sẽ được xử lý bởi một DashboardController sau này
            return;
        }
        
        // TODO: Forward to login.jsp
        // Display the login form
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // TODO: Get form parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // TODO: Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            // Set error and forward back to login
            request.setAttribute("error", "Username and password are required.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }
        
        // TODO: Authenticate user
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            // TODO: Authentication successful
            
            // 1. Invalidate old session (security) - Tùy chọn nhưng nên làm
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            
            // 2. Create new session (request.getSession(true) or request.getSession())
            HttpSession session = request.getSession(); 
            
            // 3. Store user data in session (Không lưu password!)
            session.setAttribute("user", user); 
            
            // 4. Set session timeout (30 minutes)
            session.setMaxInactiveInterval(SESSION_TIMEOUT);
            
            // 5. Redirect based on role
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if (user.isUser()) {
                response.sendRedirect(request.getContextPath() + "/user/dashboard");
            } else {
               
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
            
        } else {
            // TODO: Authentication failed
            // Set error message and forward back to login
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}