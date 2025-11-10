package com.i2p.accreditations.controller.chapter;

import com.i2p.accreditations.dto.ChapterDto;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.chapter.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@ProtectedEndpoint
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService service;

    @Autowired
    public ChapterController(ChapterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Chapter> create(@RequestBody ChapterDto chapterDto) {
        return ResponseEntity.ok(service.createChapter(chapterDto));
    }

    @GetMapping("/getAllByAccreditationId/{id}")
    public ResponseEntity<List<Chapter>> getAllByAccreditationId(@PathVariable("id") UUID accreditationId) {
        return ResponseEntity.ok(service.getAllChapters(accreditationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chapter> getById(@PathVariable UUID id) {
        return service.getChapterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> update(@PathVariable UUID id, @RequestBody Chapter chapter) {
        return ResponseEntity.ok(service.updateChapter(id, chapter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }
}

