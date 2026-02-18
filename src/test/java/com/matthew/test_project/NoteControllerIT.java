package com.matthew.test_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthew.test_project.dto.NoteCreateDto;
import com.matthew.test_project.dto.NoteUpdateDto;
import com.matthew.test_project.model.Note;
import com.matthew.test_project.model.NoteTag;
import com.matthew.test_project.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class NoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void createNote_shouldReturn200_andPersistInDb() throws Exception {
        NoteCreateDto dto = new NoteCreateDto();
        dto.setTitle("Test Note");
        dto.setText("Test content");
        dto.setTags(Set.of(NoteTag.BUSINESS));

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Note"))
                .andExpect(jsonPath("$.text").value("Test content"))
                .andExpect(jsonPath("$.tags[0]").value("BUSINESS"));

        assertThat(noteRepository.findAll())
                .anyMatch(note -> note.getTitle().equals("Test Note") &&
                        note.getText().equals("Test content"));
    }

    @Test
    void createNote_withoutTitle_shouldReturn400() throws Exception {
        NoteCreateDto dto = new NoteCreateDto();
        dto.setText("Test text");

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createNote_withoutText_shouldReturn400() throws Exception {
        NoteCreateDto dto = new NoteCreateDto();
        dto.setTitle("Title");

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createNote_withoutTags_shouldReturn200_andPersistEmptyTags() throws Exception {
        NoteCreateDto dto = new NoteCreateDto();
        dto.setTitle("No Tags Note");
        dto.setText("Test text");

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").isEmpty());

        assertThat(noteRepository.findAll())
                .anyMatch(note -> note.getTitle().equals("No Tags Note") &&
                        note.getTags().isEmpty());
    }

    @Test
    void getAllNotes_shouldReturnAllNotesSortedDesc() throws Exception {
        Note note1 = Note.builder().title("First").text("Text1").tags(Set.of(NoteTag.BUSINESS)).createdDate(LocalDateTime.now().minusDays(1)).build();
        Note note2 = Note.builder().title("Second").text("Text2").tags(Set.of(NoteTag.PERSONAL)).createdDate(LocalDateTime.now()).build();
        noteRepository.saveAll(List.of(note1, note2));

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Second"))
                .andExpect(jsonPath("$.content[1].title").value("First"));
    }

    @Test
    void getAllNotes_shouldFilterByTags() throws Exception {
        noteRepository.deleteAll();
        Note note1 = Note.builder().id(UUID.randomUUID()).title("Business Note").text("Text").tags(Set.of(NoteTag.BUSINESS)).createdDate(LocalDateTime.now()).build();
        Note note2 = Note.builder().id(UUID.randomUUID()).title("Personal Note").text("Text").tags(Set.of(NoteTag.PERSONAL)).createdDate(LocalDateTime.now()).build();
        noteRepository.saveAll(List.of(note1, note2));

        mockMvc.perform(get("/api/notes")
                        .param("tags", "BUSINESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Business Note"));
    }

    @Test
    void getAllNotes_shouldReturnEmptyList_whenNoNotes() throws Exception {
        noteRepository.deleteAll();
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void getAllNotes_shouldRespectPagination() throws Exception {
        for (int i = 1; i <= 25; i++) {
            noteRepository.save(Note.builder().id(UUID.randomUUID()).title("Note " + i).text("Text").tags(Set.of(NoteTag.BUSINESS)).createdDate(LocalDateTime.now().minusDays(i)).build());
        }

        mockMvc.perform(get("/api/notes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void getNoteById_shouldReturnNote() throws Exception {
        noteRepository.deleteAll();

        Note note = Note.builder()
                .id(UUID.randomUUID())
                .title("Test Note")
                .text("Some content")
                .tags(Set.of(NoteTag.BUSINESS))
                .createdDate(LocalDateTime.now())
                .build();

        noteRepository.save(note);

        mockMvc.perform(get("/api/notes/{id}", note.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Note"))
                .andExpect(jsonPath("$.text").value("Some content"))
                .andExpect(jsonPath("$.tags[0]").value("BUSINESS"));
    }

    @Test
    void getNoteById_shouldReturn404IfNotFound() throws Exception {
        noteRepository.deleteAll();

        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/notes/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNote_shouldModifyExistingNote() throws Exception {
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .title("Original Title")
                .text("Original Text")
                .tags(Set.of(NoteTag.BUSINESS))
                .createdDate(LocalDateTime.now())
                .build();
        noteRepository.save(note);

        NoteUpdateDto updateDto = new NoteUpdateDto();
        updateDto.setTitle("Updated Title");
        updateDto.setText("Updated Text");
        updateDto.setTags(Set.of(NoteTag.PERSONAL));

        mockMvc.perform(put("/api/notes/{id}", note.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.text").value("Updated Text"))
                .andExpect(jsonPath("$.tags[0]").value("PERSONAL"));

        Note updatedNote = noteRepository.findById(note.getId()).orElseThrow();
        assertThat(updatedNote.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedNote.getText()).isEqualTo("Updated Text");
        assertThat(updatedNote.getTags()).containsExactly(NoteTag.PERSONAL);
    }

    @Test
    void updateNote_shouldReturnNotFoundForNonExistingNote() throws Exception {
        UUID nonExistingId = UUID.randomUUID();

        NoteUpdateDto updateDto = new NoteUpdateDto();
        updateDto.setTitle("New Title");

        mockMvc.perform(put("/api/notes/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNote_shouldRemoveNote() throws Exception {
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .title("Note to Delete")
                .text("Text")
                .tags(Set.of(NoteTag.BUSINESS))
                .createdDate(LocalDateTime.now())
                .build();
        noteRepository.save(note);

        mockMvc.perform(delete("/api/notes/{id}", note.getId()))
                .andExpect(status().isNoContent());

        assertThat(noteRepository.existsById(note.getId())).isFalse();
    }

    @Test
    void deleteNote_shouldReturnNotFoundForNonExistingNote() throws Exception {
        UUID nonExistingId = UUID.randomUUID();

        mockMvc.perform(delete("/api/notes/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWordStats_shouldReturnCorrectFrequency() throws Exception {
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .title("Word Stats Note")
                .text("Note is just a note")
                .tags(Set.of(NoteTag.BUSINESS))
                .createdDate(LocalDateTime.now())
                .build();
        noteRepository.save(note);

        mockMvc.perform(get("/api/notes/{id}/word-stats", note.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value(2))
                .andExpect(jsonPath("$.is").value(1))
                .andExpect(jsonPath("$.just").value(1))
                .andExpect(jsonPath("$.a").value(1));
    }

    @Test
    void getWordStats_shouldReturnEmptyForEmptyText() throws Exception {
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .title("Empty Text Note")
                .text("")
                .tags(Set.of(NoteTag.BUSINESS))
                .createdDate(LocalDateTime.now())
                .build();
        noteRepository.save(note);

        mockMvc.perform(get("/api/notes/{id}/word-stats", note.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getWordStats_shouldReturnNotFoundForNonExistingNote() throws Exception {
        UUID nonExistingId = UUID.randomUUID();

        mockMvc.perform(get("/api/notes/{id}/word-stats", nonExistingId))
                .andExpect(status().isNotFound());
    }
}