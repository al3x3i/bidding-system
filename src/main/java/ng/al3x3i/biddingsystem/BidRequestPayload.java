package ng.al3x3i.biddingsystem;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class BidRequestPayload {
    private String id;
    private Map<String, String> attributes;
}
