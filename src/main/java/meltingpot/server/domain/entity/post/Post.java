package meltingpot.server.domain.entity.post;


import jakarta.persistence.*;
import lombok.*;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Report;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.comment.CommentImage;
import meltingpot.server.domain.entity.common.BaseEntity;
import meltingpot.server.domain.entity.enums.PostType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Account account;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImage> postImages = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Report> reports = new ArrayList<>();

    public void setPostImages(List<PostImage> postImages) {
        this.postImages = postImages.stream()
                .map(postImage -> {
                    postImage.setPost(this);
                    return postImage;
                })
                .collect(Collectors.toList());
    }

    }
