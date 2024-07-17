package meltingpot.server.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostImageDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("imgUrl")
    private String imgUrl;

}
