/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.NoteDTO;
import com.tdbang.crm.entities.Note;

@Component
public class NoteMapper {

    public NoteDTO mappingNoteEntityToNoteDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setPk(note.getPk());
        dto.setEntityType(note.getEntityType());
        dto.setEntityFk(note.getEntityFk());
        dto.setNoteType(note.getNoteType().getName());
        dto.setContent(note.getContent());
        dto.setCreatedByName(note.getCreatedBy().getName());
        dto.setCreatedByFk(note.getCreatedBy().getPk());
        dto.setCreatedOn(note.getCreatedOn());
        dto.setUpdatedOn(note.getUpdatedOn());
        return dto;
    }

    public List<NoteDTO> mappingToListNoteDTO(List<Note> notes) {
        return notes.stream().map(this::mappingNoteEntityToNoteDTO).toList();
    }
}
