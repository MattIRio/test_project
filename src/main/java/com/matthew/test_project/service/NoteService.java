package com.matthew.test_project.service;

import com.matthew.test_project.dto.NoteCreateDto;
import com.matthew.test_project.dto.NoteDetailDto;
import com.matthew.test_project.dto.NoteListDto;
import com.matthew.test_project.dto.NoteUpdateDto;
import com.matthew.test_project.model.NoteTag;
import com.mongodb.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface NoteService {

    NoteDetailDto create(NoteCreateDto dto);

    Page<NoteListDto> findAll(@Nullable Set<NoteTag> tags, Pageable pageable);

    NoteDetailDto findById(UUID id);

    NoteDetailDto update(UUID id, NoteUpdateDto dto);

    void delete(UUID id);

    Map<String, Long> getWordFrequency(UUID id);
}
