package bigarray.ai.service;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.model.url}")
    private String modelUrl;

    private final RestTemplate restTemplate;

    public HuggingFaceService() {
        // 🧩 Build a tolerant RestTemplate that ignores bad MIME headers
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(15))
                .setResponseTimeout(Timeout.ofSeconds(30))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .disableContentCompression() // avoids MIME parsing errors
                .build();

        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    public Map<String, Object> summarizeText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", text);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                modelUrl,
                HttpMethod.POST,
                entity,
                List.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> summaryResult = (Map<String, Object>) response.getBody().get(0);
            return Map.of(
                    "summary", summaryResult.get("summary_text"),
                    "original_length", text.length(),
                    "summary_length", ((String) summaryResult.get("summary_text")).length()
            );
        }

        return Map.of("error", "Failed to get summary from Hugging Face");
    }
}
