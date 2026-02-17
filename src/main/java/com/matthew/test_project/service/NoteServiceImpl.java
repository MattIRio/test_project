package com.matthew.test_project.service;

import com.matthew.test_project.dto.NoteCreateDto;
import com.matthew.test_project.dto.NoteDetailDto;
import com.matthew.test_project.dto.NoteListDto;
import com.matthew.test_project.dto.NoteUpdateDto;
import com.matthew.test_project.exception.NoteNotFoundException;
import com.matthew.test_project.model.Note;
import com.matthew.test_project.model.NoteTag;
import com.matthew.test_project.repository.NoteRepository;
import com.mongodb.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Override
    public NoteDetailDto create(NoteCreateDto dto) {

        Note note = Note.builder()
                .id(UUID.randomUUID())
                .title(dto.getTitle())
                .text(dto.getText())
                .tags(dto.getTags() != null ? new HashSet<>(dto.getTags()) : new HashSet<>())
                .createdDate(LocalDateTime.now())
                .build();

        Note saved = noteRepository.save(note);
        return noteMapper.toDetailDto(saved);
    }

    @Override
    public Page<NoteListDto> findAll(@Nullable Set<NoteTag> tags, Pageable pageable) {
        Page<Note> page;

        if (tags == null || tags.isEmpty()) {
            page = noteRepository.findAll(pageable);
        } else {
            page = noteRepository.findByTagsIn(tags, pageable);
        }

        return page.map(note -> new NoteListDto(note.getId(), note.getTitle(), note.getCreatedDate()));

    }

    @Override
    public NoteDetailDto findById(UUID id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + id));

        return noteMapper.toDetailDto(note);
    }

    @Override
    public NoteDetailDto update(UUID id, NoteUpdateDto dto) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + id));

        if (dto.getTitle() != null) {
            note.setTitle(dto.getTitle());
        }
        if (dto.getText() != null) {
            note.setText(dto.getText());
        }
        if (dto.getTags() != null) {
            note.setTags(dto.getTags());
        }

        Note updated = noteRepository.save(note);
        return noteMapper.toDetailDto(updated);
    }

    @Override
    public void delete(UUID id) {
        if (!noteRepository.existsById(id)) {
            throw new NoteNotFoundException("Note not found with id: " + id);
        }
        noteRepository.deleteById(id);
    }

    @Override
    public Map<String, Long> getWordFrequency(UUID id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + id));

        String text = note.getText();
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        String cleaned = text.toLowerCase()
                .replaceAll("[^a-zа-яё0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        ArrayList<String> words = new ArrayList<>(Arrays.asList(cleaned.split("\\s+")));

        Map<String, Long> frequency = new HashMap<>();

        for (int i = 0; i < words.size(); i++) {

            String currentWord = words.get(i);

            if (frequency.containsKey(currentWord)) {
                continue;
            }

            long count = 0;

            for (int j = i; j < words.size(); j++) {
                if (currentWord.equals(words.get(j))) {
                    count++;
                }
            }

            frequency.put(currentWord, count);
        }
        List<Map.Entry<String, Long>> list = new ArrayList<>(frequency.entrySet());

        Collections.sort(list, (a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String, Long> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : list) {
            sorted.put(entry.getKey(), entry.getValue());
        }

        return sorted;
    }

}
