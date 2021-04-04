package ng.al3x3i.biddingsystem;

import io.vavr.control.Try;
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

@Service
@Slf4j
public class BiddingSystemService {

    @Autowired
    private BiddersSettings biddersSettings;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

//    public Optional<BidResponsePayload> handleBiddingRequest(String id, Map<String, String> queryParams) {
//
//        var requestPayload = new BidRequestPayload(id, queryParams);
//
//        List<BidResponsePayload> responsePayloads = biddersSettings.getBidders()
//                .stream()
//                .map(bidderUrl ->
//                        Try.of(() -> restTemplate.postForObject(bidderUrl, requestPayload, BidResponsePayload.class))
//                                .onFailure((ex) -> log.error("Error, occurred unexpected exception while reading data from the bidder: `{}`", bidderUrl))
//                                .getOrNull())
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        Optional<BidResponsePayload> highestBidPayload = getHighestBid(responsePayloads);
//        return highestBidPayload;
//    }

    public Optional<BidResponsePayload> handleBiddingRequestAsynchronously(String id, Map<String, String> queryParams) {

        var requestPayload = new BidRequestPayload(id, queryParams);

        List<CompletableFuture<BidResponsePayload>> completableFuturesRequests = biddersSettings.getBidders()
                .stream()
                .map(bidderUrl ->
                        CompletableFuture.supplyAsync(() -> restTemplate.postForObject(bidderUrl, requestPayload, BidResponsePayload.class), threadPoolTaskExecutor)
                                .exceptionally(ex -> {
                                    log.error("Error, occurred unexpected exception while reading data from the bidder: `{}`", bidderUrl);
                                    return null;
                                })
                )
                .collect(Collectors.toList());

        List<BidResponsePayload> responsePayloads = completableFuturesRequests
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return getHighestBid(responsePayloads);
    }

    public Optional<BidResponsePayload> getHighestBid(List<BidResponsePayload> bidResponsePayloads) {
        return bidResponsePayloads.stream().max(Comparator.comparingLong(BidResponsePayload::getBid));
    }
}
