package nile.org.in;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
public class RateLimiterApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder()
				.profiles("dev") // and so does this
				.sources(RateLimiterApplication.class)
				.run(args);

	}
}
