package ng.al3x3i.biddingsystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity getBidding(@PathVariable String id, @RequestParam Map<String, String> queryParams) {

        log.info("Received request: id=`{}`, query-params=`{}`", id, queryParams);

        Optional<BidResponsePayload> highestBidPayload = service.handleBiddingRequestAsynchronously(id, queryParams);

        return highestBidPayload.map(payload -> {
            BidResponsePayload response = highestBidPayload.get();
            return ResponseEntity.ok(formatBidResponse(response));
        }).orElseGet(() -> {
            log.warn("Unable handle bidding process");
            return ResponseEntity.badRequest().build();
        });
    }

    private String formatBidResponse(BidResponsePayload payload) {
        String content = payload.getContent()
                .replaceAll(Pattern.quote("$price$"), String.valueOf(payload.getBid()));
        payload.setContent(content);
        return payload.getContent();
    }
}
