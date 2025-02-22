package com.rest.server.graphql;

import com.rest.server.models.Tag;
import com.rest.server.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TagGraphQLController {

    @Autowired
    private TagService tagService;

    // Récupérer tous les tags avec pagination
    @QueryMapping
    public List<Tag> getAllTags(@Argument int page, @Argument int size) {
        Page<Tag> tagsPage = tagService.allTags(PageRequest.of(page, size));
        return tagsPage.getContent();
    }

    // Récupérer un tag par ID
    @QueryMapping
    public Tag getTag(@Argument String tagId) {
        return tagService.singleTag(tagId).orElse(null);
    }

    // Créer un nouveau tag
    @MutationMapping
    public Tag createTag(@Argument String tagName) {
        Tag tag = new Tag();
        tag.setTagName(tagName);
        return tagService.createTag(tag);
    }

    // Mettre à jour un tag
    @MutationMapping
    public Tag updateTag(@Argument String tagId, @Argument String tagName) {
        Tag tag = tagService.singleTag(tagId).orElseThrow(() -> new RuntimeException("Tag not found"));
        if (tagName != null) tag.setTagName(tagName);
        return tagService.updateTag(tagId, tag);
    }

    // Supprimer un tag
    @MutationMapping
    public boolean deleteTag(@Argument String tagId) {
        tagService.deleteTag(tagId);
        return true;
    }
}