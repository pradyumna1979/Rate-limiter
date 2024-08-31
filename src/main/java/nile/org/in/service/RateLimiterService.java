package nile.org.in.service;

import jakarta.annotation.PostConstruct;
import nile.org.in.component.Timer;
import nile.org.in.exceptions.RateLimitExceededException;
import nile.org.in.model.RateLimiterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
@Service
public class RateLimiterService {
    @Value("${rate.limiter.request-limit}")
    private  int timeOutPeriod;
    @Value("${rate.limiter.capacity-per-slot}")
    private  int capacityPerSlot;
    private final static TimeUnit timeUnit = TimeUnit.SECONDS;
    private  ArrayBlockingQueue<RateLimiterRequest>[] slots;
    private  Map<String, Integer> reverseIndex;
    @Autowired
    private  Timer timer;
    private  ExecutorService[] threads;
    @PostConstruct
    public void init(){
        if (this.timeOutPeriod > 1000) {
            throw new IllegalArgumentException();
        }
        this.slots = new ArrayBlockingQueue[this.timeOutPeriod];
        this.threads = new ExecutorService[this.timeOutPeriod];
        this.reverseIndex = new ConcurrentHashMap<>();
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new ArrayBlockingQueue<>(capacityPerSlot);
            threads[i] = Executors.newSingleThreadExecutor();
        }
        final long timePerSlot = TimeUnit.MILLISECONDS.convert(1, timeUnit);
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::flushRequests,
                        timePerSlot - (timer.getCurrentTimeInMillis() % timePerSlot),
                        timePerSlot, TimeUnit.MILLISECONDS);
    }

    public Future<?> flushRequests() {
        final int currentSlot = getCurrentSlot();
        return threads[currentSlot].submit(() -> {
            for (final RateLimiterRequest rateLimiterRequest : slots[currentSlot]) {
                if (timer.getCurrentTime(timeUnit) - rateLimiterRequest.getStartTime() >= timeOutPeriod) {
                    slots[currentSlot].remove(rateLimiterRequest);
                    reverseIndex.remove(rateLimiterRequest.getRequestId());
                }
            }
        });
    }

    public Future<?> addRequest(final RateLimiterRequest rateLimiterRequest) throws RateLimitExceededException{
        final int currentSlot = getCurrentSlot();
        return threads[currentSlot].submit(() -> {
            if (slots[currentSlot].size() >= capacityPerSlot) {
                throw new RateLimitExceededException();
            }
            slots[currentSlot].add(rateLimiterRequest);
            reverseIndex.put(rateLimiterRequest.getRequestId(), currentSlot);
        });
    }

    public Future<?> evict(final String requestId) {
        final int currentSlot = reverseIndex.get(requestId);
        return threads[currentSlot].submit(() -> {
            slots[currentSlot].remove(new RateLimiterRequest(requestId, 0));
            reverseIndex.remove(requestId);
        });
    }

    private int getCurrentSlot() {
        return (int) timer.getCurrentTime(timeUnit) % slots.length;
    }
}

