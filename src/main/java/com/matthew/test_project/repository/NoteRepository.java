package com.matthew.test_project.repository;

import com.matthew.test_project.model.Note;
import com.matthew.test_project.model.NoteTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface NoteRepository extends MongoRepository<Note, UUID> {
    Page<Note> findByTagsIn(Set<NoteTag> tags, Pageable pageable);
}
