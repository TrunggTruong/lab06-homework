package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // TODO: Get session (don't create if doesn't exist)
        // Passing 'false' ensures that a new session is NOT created if one does not already exist.
        HttpSession session = request.getSession(false);
        
        // TODO: If session exists, invalidate it
        if (session != null) {
            // Invalidate the session, clearing all stored attributes (like the 'user' object)
            session.invalidate();
        }
        
        // TODO: Redirect to login with success message
        // Redirect the user to the login page with a query parameter 'message'
        response.sendRedirect("login?message=You have been logged out successfully.");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle POST requests by treating them the same as GET requests (logging out)
        doGet(request, response);
    }
}