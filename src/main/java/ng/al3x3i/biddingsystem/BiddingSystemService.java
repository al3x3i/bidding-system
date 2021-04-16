package ng.al3x3i.biddingsystem;

import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class BiddingSystemService {

    @Autowired
    private BiddersSettings biddersSettings;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public Optional<BidResponsePayload> handleBiddingRequestAsynchronously(String id, Map<String, String> queryParams) {

        var requestPayload = new BidRequestPayload(id, queryParams);

        Stream<CompletableFuture<BidResponsePayload>> completableFuturesRequests =
                biddersSettings.getBidders()
                        .stream()
                        .map(bidderUrl ->
                                CompletableFuture.supplyAsync(() -> restTemplate.postForObject(bidderUrl, requestPayload, BidResponsePayload.class), threadPoolTaskExecutor)
                                        .exceptionally(ex -> {
                                            log.error("Error, occurred unexpected exception while reading data from the bidder: `{}`", bidderUrl);
                                            return null;
                                        })
                        );

        List<BidResponsePayload> responsePayloads = completableFuturesRequests
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return getHighestBid(responsePayloads);
    }

    public Optional<BidResponsePayload> getHighestBid(List<BidResponsePayload> bidResponsePayloads) {
        return bidResponsePayloads.stream().max(Comparator.comparingLong(BidResponsePayload::getBid));
    }

    public Optional<BidResponsePayload> getHighestBidSecond(@NotNull List<BidResponsePayload> bidResponsePayloads) {

        var allSortedBids = bidResponsePayloads
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(BidResponsePayload::getBid).reversed())
                .collect(Collectors.toList());

        if (allSortedBids.size() >= 2) {

            var first = allSortedBids.get(0);
            var second = allSortedBids.get(1);

            var response = first.getBid().equals(second.getBid()) ? first : raiseBidByOne(second);

            return Optional.of(response);
        } else if (allSortedBids.size() == 1) {
            return Optional.of(allSortedBids.get(0));
        }

        return Optional.empty();
    }

    public BidResponsePayload raiseBidByOne(BidResponsePayload bid) {
        bid.setBid(bid.getBid() + 1);
        return bid;
    }
}
