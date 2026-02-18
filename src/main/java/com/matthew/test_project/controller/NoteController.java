package com.matthew.test_project.controller;

import com.matthew.test_project.dto.NoteCreateDto;
import com.matthew.test_project.dto.NoteDetailDto;
import com.matthew.test_project.dto.NoteListDto;
import com.matthew.test_project.dto.NoteUpdateDto;
import com.matthew.test_project.model.NoteTag;
import com.matthew.test_project.repository.NoteRepository;
import com.matthew.test_project.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;
    private final NoteRepository noteRepository;

    public NoteController(NoteService noteService, NoteRepository noteRepository) {
        this.noteService = noteService;
        this.noteRepository = noteRepository;
    }

    @PostMapping
    public ResponseEntity<NoteDetailDto> createNote(@Valid @RequestBody NoteCreateDto dto) {
        return ResponseEntity.ok(noteService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<NoteListDto>> getAllNotes(

            @RequestParam(required = false) Set<NoteTag> tags,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(noteService.findAll(tags, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDetailDto> getNoteById(@PathVariable UUID id) {
        return ResponseEntity.ok(noteService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDetailDto> updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody NoteUpdateDto dto
    ) {
        return ResponseEntity.ok(noteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable UUID id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/word-stats")
    public ResponseEntity<Map<String, Long>> getWordStatistics(@PathVariable UUID id) {
        return ResponseEntity.ok(noteService.getWordFrequency(id));
    }



}