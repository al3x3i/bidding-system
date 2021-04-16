package ng.al3x3i.biddingsystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class BiddingSystemServiceIntegrationTest {

    @Autowired
    private BiddingSystemService service;

    @Test
    public void should_get_highest_bid() {

        // GIVEN
        var requests = List.of(
                new BidResponsePayload(5L, 400L, "c:$price$"),
                new BidResponsePayload(5L, 600L, "c:$price$"),
                new BidResponsePayload(5L, 555L, "c:$price$"),
                new BidResponsePayload(5L, 500L, "c:$price$")
        );

        // WHEN
        Optional<BidResponsePayload> bid = service.getHighestBid(requests);

        // THEN
        then(bid).isNotEmpty();
        then(bid.get().getBid()).isEqualTo(600L);
    }

    @Test
    public void should_get_second_price_plus_one() {

        // GIVEN
        var requests = List.of(
                new BidResponsePayload(5L, 10L, "c:$price$"),
                new BidResponsePayload(5L, 7L, "c:$price$"),
                new BidResponsePayload(5L, 5L, "c:$price$"),
                new BidResponsePayload(5L, 4L, "c:$price$")
        );

        // WHEN
        var bid  = service.getHighestBidSecond(requests);

        // THEN
        then(bid).isNotEmpty();
        then(bid.get().getBid()).isEqualTo(8L);

    }

    @Test
    public void should_get_not_get_second_price_raised_by_one() {

        // GIVEN
        var requests = List.of(
                new BidResponsePayload(5L, 4L, "c:$price$"),
                new BidResponsePayload(5L, 4L, "c:$price$")
        );

        // WHEN
        var bid  = service.getHighestBidSecond(requests);

        // THEN
        then(bid).isNotEmpty();
        then(bid.get().getBid()).isEqualTo(4L);

    }

}
