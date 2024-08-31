package nile.org.in.controller;

import jakarta.servlet.http.HttpServletRequest;
import nile.org.in.component.Timer;
import nile.org.in.service.TimerWheelService;
import nile.org.in.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/ratelimiter")
public class RateLimiterController {
    @Autowired
    private TimerWheelService timerWheelService;
    @Autowired
    Timer timer;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    Request request=null;
    @GetMapping("/")
    public String rateLimiter(HttpServletRequest httpServletRequest) {
        request = new Request(httpServletRequest.getRequestId(),timer.getCurrentTime(timeUnit));
        timerWheelService.addRequest(request);
        return "Request successful";
    }

}
