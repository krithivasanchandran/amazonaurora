import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import restcontroller.implementation.CrawlController;

@Configuration
@ComponentScan(basePackageClasses= CrawlController.class)
@EnableAutoConfiguration
public class AuroraMainApp {
    public static void main(String[] args) {

        Runtime runtime = Runtime.getRuntime();

        if(runtime != null){
            int numberOfProcessors = runtime.availableProcessors();
            System.out.println("Number of processors available to this JVM: " + numberOfProcessors);
        }

        SpringApplication.run(AuroraMainApp.class, args);
    }
}