import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import restcontroller.implementation.CrawlController;

@Configuration
@ComponentScan(basePackageClasses= CrawlController.class)
@EnableAutoConfiguration
public class AuroraMainApp {
    public static void main(String[] args) {

        SpringApplication.run(AuroraMainApp.class, args);
    }
}
