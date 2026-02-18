package com.matthew.test_project.dto;

import com.matthew.test_project.model.NoteTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NoteDetailDto {

    private String title;
    private LocalDateTime createdDate;
    private String text;

    private Set<NoteTag> tags;

}
