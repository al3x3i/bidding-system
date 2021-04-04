package ng.al3x3i.biddingsystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/")
@Slf4j
public class BiddingSystemController {

    @Autowired
    private BiddingSystemService service;

    @GetMapping("{id}")
    public String getBidding(@PathVariable String id, @RequestParam Map<String, String> queryParams) {

        log.info("Received request: id=`{}`, query-params=`{}`", id, queryParams);

        Optional<BidResponsePayload> highestBidPayload = service.handleBiddingRequestAsynchronously(id, queryParams);

        return highestBidPayload.map(payload -> {
            BidResponsePayload response = highestBidPayload.get();
            formatBidResponse(response);
            return response.getContent();
        }).orElseGet(() -> {
            log.warn("Unable handle bidding process");
            return "No responses to bids";
        });
    }

    private BidResponsePayload formatBidResponse(BidResponsePayload payload) {
        String content = payload.getContent()
                .replaceAll(Pattern.quote("$price$"), String.valueOf(payload.getBid()));
        payload.setContent(content);
        return payload;
    }
}
