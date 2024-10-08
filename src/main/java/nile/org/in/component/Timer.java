package nile.org.in.component;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
@Component
public class Timer {
    public long getCurrentTime(final TimeUnit timeUnit) {
        return timeUnit.convert(getCurrentTimeInMillis(), TimeUnit.MILLISECONDS);
    }

    public long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }
}
