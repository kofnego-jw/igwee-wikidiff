package at.ac.uibk.igwee.wikidiff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"at.ac.uibk.igwee.wikidiff"})
public class IgweeWikidiffTestApp extends IgweeWikidiffWebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgweeWikidiffTestApp.class, args);
	}

}
