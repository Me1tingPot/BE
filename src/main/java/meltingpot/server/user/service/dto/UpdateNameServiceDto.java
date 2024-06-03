package meltingpot.server.user.service.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateNameServiceDto {
    private String name;
}
