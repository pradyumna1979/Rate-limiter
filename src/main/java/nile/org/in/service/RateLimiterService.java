package nile.org.in.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import nile.org.in.component.Timer;
import nile.org.in.exceptions.RateLimitExceededException;
import nile.org.in.record.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;
@Service
@Slf4j
public class RateLimiterService {
    @Value("${rate.limiter.request-limit}")
    private  int timeOutPeriod;
    @Value("${rate.limiter.capacity-per-slot}")
    private  int capacityPerSlot;
    private final static TimeUnit timeUnit = TimeUnit.SECONDS;
    private  ArrayBlockingQueue<Request>[] slots;
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
        System.out.println(Arrays.toString(slots));
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
            for (final Request request : slots[currentSlot]) {
                if (timer.getCurrentTime(timeUnit) - request.startTime() >= timeOutPeriod) {
                    log.info("removing request : {} : from slot : {} ",request,currentSlot);
                    slots[currentSlot].remove(request);
                    reverseIndex.remove(request.requestId());
                }
            }
        });
    }

    public Future<?> addRequest(final Request request){
        final int currentSlot = getCurrentSlot();
        log.info("currentSlot : {}",currentSlot);
        return threads[currentSlot].submit(() -> {
            if (slots[currentSlot].size() >= capacityPerSlot) {
                log.info("Size exceeds on currentSlot : {} : throwing exception for requestId : {}",currentSlot,request);
                throw new RateLimitExceededException();
            }
            slots[currentSlot].add(request);
            reverseIndex.put(request.requestId(), currentSlot);
        });
    }

    public Future<?> evict(final String requestId) {
        final int currentSlot = reverseIndex.get(requestId);
        return threads[currentSlot].submit(() -> {
            slots[currentSlot].remove(new Request(requestId, 0));
            reverseIndex.remove(requestId);
        });
    }

    private int getCurrentSlot() {
        return (int) timer.getCurrentTime(timeUnit) % slots.length;
    }
}

