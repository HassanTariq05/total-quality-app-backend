package com.i2p.accreditations.service.chapter;

import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.repository.chapter.ChapterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChapterService {

    private final ChapterRepository repository;

    public ChapterService(ChapterRepository repository) {
        this.repository = repository;
    }

    public Chapter createChapter(Chapter chapter) {
        // get the latest number
        Long nextNumber = repository.findTopByOrderByNumberDesc()
                .map(c -> c.getNumber() + 1)
                .orElse(1L); // start from 1 if no chapters exist

        chapter.setNumber(nextNumber);

        return repository.save(chapter);
    }

    public List<Chapter> getAllChapters() {
        return repository.findAll();
    }

    public Optional<Chapter> getChapterById(UUID id) {
        return repository.findById(id);
    }

    public Chapter updateChapter(UUID id, Chapter chapterDetails) {
        return repository.findById(id).map(chapter -> {
            chapter.setTitle(chapterDetails.getTitle());
            chapter.setDescription(chapterDetails.getDescription());
            chapter.setStatus(chapterDetails.getStatus());
            return repository.save(chapter);
        }).orElseThrow(() -> new RuntimeException("Chapter not found with id " + id));
    }

    public void deleteChapter(UUID id) {
        repository.deleteById(id);
    }
}

