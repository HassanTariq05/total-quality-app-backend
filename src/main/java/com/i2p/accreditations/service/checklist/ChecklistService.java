package com.i2p.accreditations.service.checklist;

import com.i2p.accreditations.dto.ChecklistDto;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.repository.chapter.ChapterRepository;
import com.i2p.accreditations.repository.checklist.ChecklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChecklistService {

    private final ChecklistRepository repository;

    private final ChapterRepository chapterRepository;

    @Autowired
    public ChecklistService(ChecklistRepository checklistRepository, ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
        this.repository = checklistRepository;
    }

    public Checklist createChecklist(ChecklistDto checklistDto) {
        Long nextNumber = repository.findTopByOrderByNumberDesc()
                .map(c -> c.getNumber() + 1)
                .orElse(1L);

        Checklist checklist = new Checklist();
        checklist.setNumber(nextNumber);
        checklist.setTitle(checklistDto.getTitle());
        checklist.setDescription(checklistDto.getDescription());
        checklist.setStatus(checklistDto.getStatus());

        if (checklistDto.getChapterId() != null) {
            Chapter chapter = chapterRepository.findById(checklistDto.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
            checklist.setChapter(chapter);
        }

        return repository.save(checklist);
    }

    public Page<Checklist> getAllChecklists(UUID chapterId, Pageable pageable) {
        return repository.findByChapterId(chapterId, pageable);
    }
    public Optional<Checklist> getChecklistById(UUID id) {
        return repository.findByIdWithChapter(id);
    }


    public Checklist updateChecklist(UUID id, Checklist checklistDetails) {
        return repository.findById(id).map(checklist -> {
            checklist.setTitle(checklistDetails.getTitle());
            checklist.setDescription(checklistDetails.getDescription());
            checklist.setStatus(checklistDetails.getStatus());
            return repository.save(checklist);
        }).orElseThrow(() -> new RuntimeException("Checklist not found with id " + id));
    }

    public void deleteChecklist(UUID id) {
        repository.deleteById(id);
    }
}


