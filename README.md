Steps to Run this application:
step-1
    clone the project https://github.com/pradyumna1979/Rate-limiter.git on Intellij
step-2:
    Run RateLimiterApplication
step-3
    Hit the URL localhost://8080/ratelimiter/ on postman/browser to the request limit 40
    Response : Request successful
    
    Response:
    {
    "type": "/",
    "title": "Test rate limiter",
    "status": 429,
    "detail": "Too Many Rquests !!!!!",
    "instance": null,
    "properties": null
    }
Step-4
  Default configuration: application.yml
  rate:
  limiter:
      request-limit: ${REQUEST-LIMIT:6}
      time-window-min : ${TIME-WINDOW-MIN:3}
      capacity-per-slot: ${CAPACITY-PER-SLOT:3}
    
  You can pass REQUEST-LIMIT and capacity-per-slot as enviornment Variable
    
    
