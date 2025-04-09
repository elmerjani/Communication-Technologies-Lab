package com.rest.server.graphql;

import com.rest.server.models.Comment;
import com.rest.server.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CommentGraphQLController {

    @Autowired
    private CommentService commentService;

    // Récupérer tous les commentaires avec pagination
    @QueryMapping
    public List<Comment> getAllComments(@Argument int page, @Argument int size) {
        Page<Comment> commentsPage = commentService.allComments(PageRequest.of(page, size));
        return commentsPage.getContent();
    }

    // Récupérer un commentaire par ID
    @QueryMapping
    public Comment getComment(@Argument String commentId) {
        return commentService.singleComment(commentId).orElse(null);
    }

    // Récupérer les commentaires par post
    @QueryMapping
    public List<Comment> getCommentsByPost(@Argument String postId, @Argument int page, @Argument int size) {
        Page<Comment> commentsPage = commentService.findCommentsByPostId(postId, page, size);
        return commentsPage.getContent();
    }

    // Récupérer les commentaires par utilisateur
    @QueryMapping
    public List<Comment> getCommentsByUser(@Argument String userId, @Argument int page, @Argument int size) {
        Page<Comment> commentsPage = commentService.findCommentsByUserId(userId, page, size);
        return commentsPage.getContent();
    }

    // Créer un nouveau commentaire
    @MutationMapping
    public Comment createComment(
            @Argument String commentMessage,
            @Argument String commentOwnerId,
            @Argument String commentPostId,
            @Argument String commentPublishDate
    ) {
        Comment comment = new Comment();
        comment.setCommentMessage(commentMessage);
        comment.setCommentOwnerId(commentOwnerId);
        comment.setCommentPostId(commentPostId);
        comment.setCommentPublishDate(commentPublishDate);
        return commentService.createComment(comment);
    }

    // Mettre à jour un commentaire
    @MutationMapping
    public Comment updateComment(
            @Argument String commentId,
            @Argument String commentMessage
    ) {
        Comment comment = commentService.singleComment(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        if (commentMessage != null) comment.setCommentMessage(commentMessage);
        return commentService.updateComment(commentId, comment);
    }

    // Supprimer un commentaire
    @MutationMapping
    public boolean deleteComment(@Argument String commentId) {
        commentService.deleteComment(commentId);
        return true;
    }
}