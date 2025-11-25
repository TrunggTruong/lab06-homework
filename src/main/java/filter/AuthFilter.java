package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    // Public URLs that don't require authentication
    private static final String[] PUBLIC_URLS = {
        "/login",
        "/logout",
        ".css",
        ".js",
        ".png",
        ".jpg"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Cast to HTTP request & response
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Get request info
        String uri = req.getRequestURI();
        String context = req.getContextPath();

        // Extract path relative to context root
        String path = uri.substring(context.length());

        // Check if request is public → allow
        if (isPublicUrl(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check session authentication
        HttpSession session = req.getSession(false);
        Object user = (session != null) ? session.getAttribute("user") : null;

        if (user != null) {
            // Logged in → allow
            chain.doFilter(request, response);
        } else {
            // Not logged in → redirect
            res.sendRedirect(context + "/login");
        }
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter destroyed.");
    }

    // Utility: check if URL matches public patterns
    private boolean isPublicUrl(String path) {
        for (String url : PUBLIC_URLS) {
            if (path.startsWith(url) || path.contains(url)) {
                return true;
            }
        }
        return false;
    }
}
