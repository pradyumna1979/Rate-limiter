package nile.org.in.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nile.org.in.component.Timer;
import nile.org.in.exceptions.RateLimitExceededException;
import nile.org.in.record.Request;
import nile.org.in.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/ratelimiter")
@Slf4j
public class RateLimiterController {
    @Autowired
    private RateLimiterService rateLimiterService;
    @Autowired
    Timer timer;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    Request request =null;
    @GetMapping("/")
    public String rateLimiter(HttpServletRequest httpServletRequest) {
        request = new Request(httpServletRequest.getRequestId(),timer.getCurrentTime(timeUnit));
        log.info("requestId : {} ",request.requestId());
        try{
            rateLimiterService.addRequest(request).get();
        }catch (RateLimitExceededException | InterruptedException | ExecutionException exception){
            log.error("Exception : {}", exception.getMessage());
            throw new RateLimitExceededException();
        }
        return "Request successful";
    }


}
