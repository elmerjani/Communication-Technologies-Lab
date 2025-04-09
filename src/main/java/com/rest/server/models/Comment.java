package com.rest.server.models;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Document(collection = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private String commentId;

    @NotBlank(message = "Message cannot be empty")
    @Size(min = 2, max = 500, message = "Message must be between 2 and 500 characters")
    private String commentMessage;

    @NotNull(message = "Owner cannot be null")
    private String commentOwnerId;

    @NotBlank(message = "Post ID is mandatory")
    private String commentPostId;

    private String commentPublishDate;

    public  Comment formatDate( String format) {
        LocalDate date = LocalDate.parse(commentPublishDate);
        commentPublishDate = date.format(DateTimeFormatter.ofPattern(format));
        return this;

    }
}
