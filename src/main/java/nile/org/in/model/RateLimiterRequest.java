package nile.org.in.model;

public class RateLimiterRequest {
    private final String requestId;
    private final long startTime;

    public RateLimiterRequest(String requestId, long startTime) {
        this.requestId = requestId;
        this.startTime = startTime;
    }

    public String getRequestId() {
        return requestId;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return requestId.equals(((RateLimiterRequest) o).requestId);
    }

    @Override
    public int hashCode() {
        return requestId.hashCode();
    }
}
