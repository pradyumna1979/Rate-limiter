package nile.org.in.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nile.org.in.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {
    @Autowired
    private RateLimiterService rateLimiterService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userId = getUserId((HttpServletRequest) request);

        if (rateLimiterService.isRateLimited(userId)) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS,"Too Many Rquests !!!!!");
            problemDetail.setTitle("Test rate limiter");
            problemDetail.setType(URI.create(request.getRequestURI()));
            ObjectMapper mapper = new ObjectMapper();
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(mapper.writeValueAsString(problemDetail));
        }else {
            filterChain.doFilter(request, response);
        }

    }

    private String getUserId(HttpServletRequest request) {
        // For simplicity, using IP address as user ID
        return request.getRemoteAddr();
    }


}
