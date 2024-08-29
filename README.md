Steps to Run this application:
step-1
    clone the project https://github.com/pradyumna1979/Rate-limiter.git on Intellij
step-2:
    Run RateLimiterApplication
step-3
    Hit the URL localhost://8080 on postman/browser to the request limit 40
    Response : Request successful
    At 41 request Or time limit 3 min
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
    request-limit: ${REQUEST-LIMIT:40}
    time-window-min : ${TIME-WINDOW-MIN:3}
  You can pass REQUEST-LIMIT and TIME-WINDOW-MIN as enviornment Variable
    
    
