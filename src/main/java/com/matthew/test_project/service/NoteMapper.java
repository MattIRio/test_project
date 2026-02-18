package com.matthew.test_project.service;

import com.matthew.test_project.dto.NoteDetailDto;
import com.matthew.test_project.model.Note;
import org.springframework.stereotype.Service;

@Service
public class NoteMapper {

    public NoteDetailDto toDetailDto(Note note) {
        return new NoteDetailDto(
                note.getTitle(),
                note.getCreatedDate(),
                note.getText(),
                note.getTags()
        );
    }
}
