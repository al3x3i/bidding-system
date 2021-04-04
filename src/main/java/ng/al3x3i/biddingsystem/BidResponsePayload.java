package ng.al3x3i.biddingsystem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BidResponsePayload {
    private Long id;
    private Long bid;
    private String content;
}