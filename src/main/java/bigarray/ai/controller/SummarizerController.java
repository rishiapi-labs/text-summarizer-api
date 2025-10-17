package bigarray.ai.controller;

import bigarray.ai.service.HuggingFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api")
public class SummarizerController {

    @Autowired
    private HuggingFaceService huggingFaceService;

    @PostMapping("/summarize")
    public ResponseEntity<?> summarize(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Text cannot be empty"));
        }

        try {
            Map<String, Object> result = huggingFaceService.summarizeText(text);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/")
    public String home() {
        return "✅ Summarizer API is running!";
    }
}
