package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
public class UserReportCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_report_count_id")
    private Long id;

    @Column(name = "report_count")
    private int reportCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account ;

    public void incrementReportCount() {
        this.reportCount++;
    }

    public UserReportCount(Account account, int reportCount) {
        this.account = account;
        this.reportCount = reportCount;
    }
}
