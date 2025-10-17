package bigarray.ai.dto;

import lombok.Data;

@Data
public class SummarizeRequest {
    private String text;
    private String length; // optional: "short", "medium", "long"
}
