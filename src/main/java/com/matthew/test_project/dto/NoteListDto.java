package com.matthew.test_project.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteListDto(
        UUID id,
        String title,
        LocalDateTime createdDate
) {}
