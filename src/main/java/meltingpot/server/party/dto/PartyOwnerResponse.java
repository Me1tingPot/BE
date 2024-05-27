package meltingpot.server.party.dto;

import lombok.Builder;
import lombok.Setter;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;

import java.util.List;

@Builder
public record PartyOwnerResponse(
    String name,
    String introduction,
    String nationality,
    String country,
    String language,
    String city,
    List<String> profileImages,
    int partyParticipantCount,
    int partyCreationCount
) {
    public static PartyOwnerResponse of(Account account) {
        return PartyOwnerResponse.builder()
            .name(account.getName())
            .nationality(account.getNationality())
            .country(account.getCountry())
            .language(account.getLanguage())
            .city(account.getCity())
            .profileImages(account.getProfileImages().stream().map(AccountProfileImage::getImageKey).toList())
            .build();
    }
}
