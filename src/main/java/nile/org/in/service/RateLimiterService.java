package nile.org.in.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import nile.org.in.model.UserRequestData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
@Service
@Slf4j
public class RateLimiterService {
    @Value("${rate.limiter.request-limit}")
    private int REQUEST_LIMIT;

    //private static final long TIME_WINDOW_MS= ChronoUnit.MINUTES.getDuration().toMillis();
    @Value("${rate.limiter.time-window-min}")
    private long timeWindowMin;
    private static  long TIME_WINDOW_MS;
    private final Map<String, UserRequestData> userRequestMap = new HashMap<>();
    @PostConstruct
    public void init(){
        TIME_WINDOW_MS= Duration.ofMinutes(timeWindowMin).toMillis();
        log.info("TIME_WINDOW_MS : {}",TIME_WINDOW_MS);
    }
    public boolean isRateLimited(String userId) {

        Instant now = Instant.now();
        UserRequestData requestData = userRequestMap.get(userId);
        if (requestData == null) {
            requestData = new UserRequestData(now, 2);
            userRequestMap.put(userId, requestData);
            return false;
        }
        if (now.isAfter(requestData.getResetTime()) && requestData.getRequestCount() <= 2) {
            // Reset the counter and the time window
            requestData.setResetTime(now.plusMillis(TIME_WINDOW_MS));
            requestData.setRequestCount(requestData.getRequestCount()+1);
            return false;
        }
        if (Instant.now().compareTo(requestData.getResetTime())>=0){
            log.info("Request  exceeded the time window {} ", TIME_WINDOW_MS);
            return true;
        }
        if (requestData.getRequestCount() >= REQUEST_LIMIT) {
            log.info("Request Limit exceeded the threshold {} ", REQUEST_LIMIT);
            return true; // Rate limited
        }

        // Increment the counter and allow the request
        requestData.incrementRequestCount();
        return false;
    }

}
