package ng.al3x3i.biddingsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@ConfigurationProperties("ng")
@AllArgsConstructor
public class BiddersSettings {

    private final List<String> bidders;
}
