package ng.al3x3i.biddingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BiddersSettings.class)
public class BiddingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiddingSystemApplication.class, args);
    }
}
