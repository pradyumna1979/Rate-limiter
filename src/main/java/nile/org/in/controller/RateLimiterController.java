package nile.org.in.controller;

import jakarta.servlet.http.HttpServletRequest;
import nile.org.in.component.Timer;
import nile.org.in.model.RateLimiterRequest;
import nile.org.in.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/ratelimiter")
public class RateLimiterController {
    @Autowired
    private RateLimiterService rateLimiterService;
    @Autowired
    Timer timer;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    RateLimiterRequest rateLimiterRequest =null;
    @GetMapping("/")
    public String rateLimiter(HttpServletRequest httpServletRequest) {
        rateLimiterRequest = new RateLimiterRequest(httpServletRequest.getRequestId(),timer.getCurrentTime(timeUnit));
        rateLimiterService.addRequest(rateLimiterRequest);
        return "Request successful";
    }

}
