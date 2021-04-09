package de.sandkastenliga.resultserver.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletResponse r = ((HttpServletResponse) response);
        r.setHeader("Access-Control-Allow-Origin", "*");
        r.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        r.setHeader("Access-Control-Allow-Credentials", "true");
        r.setHeader("Access-Control-Allow-Headers",
                "X-Requested-With, Content-Type, Authorization");
        if (!((HttpServletRequest) request).getMethod().equalsIgnoreCase("options")) {
            chain.doFilter(request, response);
        }
    }

}
