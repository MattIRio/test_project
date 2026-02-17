package com.matthew.test_project.dto;

import com.matthew.test_project.model.NoteTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NoteCreateDto {

    @NotBlank
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotBlank
    @Size(min = 1, max = 1000, message = "Text must be between 1 and 1000 characters")
    private String text;

    private Set<NoteTag> tags;
}
