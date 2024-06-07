package meltingpot.server.user.service.dto;

import lombok.Builder;
import lombok.Getter;
import meltingpot.server.domain.entity.AccountProfileImage;

@Getter
@Builder
public class UserImagesResponseDto {
    private Long id;
    private boolean isThumbnail;
    private String imageUrl;
    private int sequence;

    public static UserImagesResponseDto of(AccountProfileImage image, String imageUrl){
        return UserImagesResponseDto.builder()
                .id(image.getId())
                .imageUrl(imageUrl)
                .isThumbnail(image.isThumbnail())
                .sequence(image.getSequence())
                .build();
    }

}
