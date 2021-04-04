package ng.al3x3i.biddingsystem;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class BiddingSystemRestIntegrationTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    @Autowired
    private BiddingSystemService empService;

    @Autowired
    private MockMvc mockMvc;

    private ResultActions resultActions;

    @Test
    @SneakyThrows
    public void should_get_bidding_by_id_and_one_query_param() {
        // GIVEN
        var responsePayload = new BidResponsePayload(1L, 750L, "a:$price$");
        var requestPayload = new BidRequestPayload("1", Map.of("a", "5"));
        when(restTemplate.postForObject("http://localhost:8083", requestPayload, BidResponsePayload.class))
                .thenReturn(responsePayload);

        // WHEN
        resultActions = mockMvc.perform(get("/1?a=5")).andDo(print());
        resultActions.andExpect(status().isOk());

        // THEN
        resultActions.andExpect(content().string("a:750"));
    }

    @Test
    @SneakyThrows
    public void should_get_bidding_by_id_and_multiple_query_params() {
        // GIVEN
        var responsePayload = new BidResponsePayload(2L, 2500L, "c:$price$");
        var requestPayload = new BidRequestPayload("2", Map.of("b", "2", "c", "5"));
        when(restTemplate.postForObject("http://localhost:8083", requestPayload, BidResponsePayload.class))
                .thenReturn(responsePayload);

        // WHEN
        resultActions = mockMvc.perform(get("/2?c=5&b=2")).andDo(print());
        resultActions.andExpect(status().isOk());

        // THEN
        resultActions.andExpect(content().string("c:2500"));
    }
}
