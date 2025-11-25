package filter;

import model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(filterName = "AdminFilter", urlPatterns = {"/student"})
public class AdminFilter implements Filter {

    private static final String[] ADMIN_ACTIONS = {
        "new", "insert", "edit", "update", "delete"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AdminFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String action = req.getParameter("action");

        if (isAdminAction(action)) {
            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;

            if (user != null && user.isAdmin()) {
                chain.doFilter(request, response);
            } else {
                // Non-admin → redirect back with error flag
                System.out.println("Access denied: user=" + (user != null ? user.getUsername() : "null") + ", action=" + action);
                res.sendRedirect(req.getContextPath() + "/student?error=You+do+not+have+permission");
                return; // STOP chain
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("AdminFilter destroyed.");
    }

    private boolean isAdminAction(String action) {
        if (action == null) return false;
        for (String a : ADMIN_ACTIONS) {
            if (a.equalsIgnoreCase(action)) return true;
        }
        return false;
    }
}
