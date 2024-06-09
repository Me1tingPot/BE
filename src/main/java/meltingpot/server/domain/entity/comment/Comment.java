package meltingpot.server.domain.entity.comment;

import jakarta.persistence.*;
import lombok.*;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @Column(name = "is_anonymous")
    private boolean isAnonymous = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children;

    @OneToOne(mappedBy = "comment", cascade = CascadeType.ALL)
    private CommentImage commentImage ;



    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Account getAccount() {
        return account;
    }

    public Boolean getIsAnonymous(){
        return isAnonymous;
    }


    public void setCommentImage(CommentImage commentImage) {
        this.commentImage = commentImage;
    }




}
