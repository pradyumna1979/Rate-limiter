package nile.org.in.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@Builder
@AllArgsConstructor
public class UserRequestData {
    private Instant resetTime;
    private int requestCount;
    public void incrementRequestCount() {
        this.requestCount++;
    }
}
