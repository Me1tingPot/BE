package meltingpot.server.domain.specification.party;

import meltingpot.server.domain.entity.Area;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public interface PartySpecification {
    static Specification<Party> containingSubject(String q) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("partySubject"), "%" + q + "%");
    }

    static Specification<Party> containingOwner(String q) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("account").get("name"), "%" + q + "%");
    }

    static Specification<Party> containingAddress(String q) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("partyLocationAddress"), "%" + q + "%");
    }

    static Specification<Party> areaEqual(Area q) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("partyArea"), q);
    }

    static Specification<Party> inParentArea(List<Area> q) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("partyArea").get("areaParent").in(q));
    }

    static Specification<Party> startInDay(Long q) {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lessThan = now.plusDays(q);

            return criteriaBuilder.between(root.get("partyStartTime"), now, lessThan);
        };
    }

    static Specification<Party> orderByStartTimeAsc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("partyStartTime")));
            return null;
        };
    }

    static Specification<Party> orderByStartTimeDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("partyStartTime")));
            return null;
        };
    }

    static Specification<Party> isStatus(PartyStatus q) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("partyStatus"), q);
    }
}
