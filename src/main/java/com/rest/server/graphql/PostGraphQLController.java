package com.rest.server.graphql;

import com.rest.server.models.Post;
import com.rest.server.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Controller
public class PostGraphQLController {

    @Autowired
    private PostService postService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;


    // Récupérer tous les posts avec pagination
    @QueryMapping
    public List<Post> getAllPosts(@Argument int page, @Argument int size) {
        Page<Post> postsPage = postService.allPosts(PageRequest.of(page, size));

        // Charger les utilisateurs associés à chaque post
        return postsPage.getContent().stream().map(post -> post.formatDate(getFormat())).map(post -> {
            post.setOwner(postService.getPostOwner(post.getPostOwnerId()));

            return post;
        }).toList();
    }


    // Récupérer un post par ID
    @QueryMapping
    public Post getPost(@Argument String postId) {

        return postService.singlePost(postId).map(post -> post.formatDate(getFormat())).orElse(null);
    }

    // Récupérer les posts par utilisateur
    @QueryMapping
    public List<Post> getPostsByUser(@Argument String userId, @Argument int page, @Argument int size) {
        Page<Post> postsPage = postService.findPostsByUserId(userId, page, size).map(post -> post.formatDate(getFormat()));
        return postsPage.getContent();
    }

    // Récupérer les posts par tag
    @QueryMapping
    public List<Post> getPostsByTag(@Argument String tag, @Argument int page, @Argument int size) {
        Page<Post> postsPage = postService.findPostsByTag(tag, page, size).map(post -> post.formatDate(getFormat()));
        return postsPage.getContent();
    }

    // Rechercher des posts
    @QueryMapping
    public List<Post> searchPosts(@Argument String query, @Argument int page, @Argument int size) {
        Page<Post> postsPage = postService.searchPosts(query, PageRequest.of(page, size)).map(post -> post.formatDate(getFormat()));
        return postsPage.getContent();
    }

    // Créer un nouveau post
    @MutationMapping
    public Post createPost(
            @Argument String postText,
            @Argument String postImage,
            @Argument Integer postLikes,
            @Argument String postLink,
            @Argument List<String> postTags,
            @Argument String postOwnerId
    ) {
        Post post = new Post();
        post.setPostText(postText);
        post.setPostImage(postImage);
        post.setPostLikes(postLikes);
        post.setPostLink(postLink);
        post.setPostTags(postTags);
        post.setPostOwnerId(postOwnerId);
        post.setPostPublishDate(LocalDate.now().toString());
        return postService.createPost(post);
    }

    // Mettre à jour un post
    @MutationMapping
    public Post updatePost(
            @Argument String postId,
            @Argument String postText,
            @Argument String postImage,
            @Argument Integer postLikes,
            @Argument String postLink,
            @Argument List<String> postTags
    ) {
        Post post = postService.singlePost(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        if (postText != null) post.setPostText(postText);
        if (postImage != null) post.setPostImage(postImage);
        if (postLikes != null) post.setPostLikes(postLikes);
        if (postLink != null) post.setPostLink(postLink);
        if (postTags != null) post.setPostTags(postTags);
        return postService.updatePost(postId, post);
    }

    // Supprimer un post
    @MutationMapping
    public boolean deletePost(@Argument String postId) {
        postService.deletePost(postId);
        return true;
    }

    private String getFormat() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage("date.format", null, locale);
    }
}