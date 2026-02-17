package com.matthew.test_project.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "testproject")
public class Note {

    @Id
    private UUID id;

    @NotBlank
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    private LocalDateTime createdDate;

    @NotBlank
    @Size(min = 1, max = 1000, message = "Text must be between 1 and 1000 characters")
    private String text;

    @Nullable
    private Set<NoteTag> tags;

}
