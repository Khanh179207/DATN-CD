package poly.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GoMetBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoMetBeApplication.class, args);
	}

}
