package at.ac.uibk.igwee.wikidiff;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by Joseph on 03.06.2016.
 */
public class TestServer {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(IgweeWikidiffTestApp.class);
        for (String now: ctx.getBeanDefinitionNames()) {
            System.out.println(now);
        }
    }

}
