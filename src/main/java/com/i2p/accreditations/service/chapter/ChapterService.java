package com.i2p.accreditations.service.chapter;

import com.i2p.accreditations.dto.ChapterDto;
import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.repository.accreditation.AccreditationRepository;
import com.i2p.accreditations.repository.chapter.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChapterService {

    private final ChapterRepository repository;

    private  final AccreditationRepository accreditationRepository;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository, AccreditationRepository accreditationRepository) {
        this.repository = chapterRepository;
        this.accreditationRepository = accreditationRepository;
    }

    public Chapter createChapter(ChapterDto chapterDto) {
        Long nextNumber = repository.findTopByOrderByNumberDesc()
                .map(c -> c.getNumber() + 1)
                .orElse(1L);

        Chapter chapter = new Chapter();
        chapter.setNumber(nextNumber);
        chapter.setTitle(chapterDto.getTitle());
        chapter.setDescription(chapterDto.getDescription());
        chapter.setStatus(chapterDto.getStatus());

        if (chapterDto.getAccreditationId() != null) {
            Accreditation accreditation = accreditationRepository.findById(chapterDto.getAccreditationId())
                    .orElseThrow(() -> new RuntimeException("Accreditation not found"));
            chapter.setAccreditation(accreditation);
        }

        return repository.save(chapter);
    }


    public List<Chapter> getAllChapters(UUID accreditationId) {
        return repository.findByAccreditationId(accreditationId);
    }
    public Optional<Chapter> getChapterById(UUID id) {
        return repository.findByIdWithAccreditation(id);
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

