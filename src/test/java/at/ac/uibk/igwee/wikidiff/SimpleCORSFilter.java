package at.ac.uibk.igwee.wikidiff;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * Disable CORS to ease the developement in TestServer.
 *
 * Starts the TestServer, using Intellij IDEA to write HTML, set useMock to true and
 * then instantly view the page.
 *
 * Created by Joseph on 03.06.2016.
 */

@Component
public class SimpleCORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "0");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Expires", "0");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
