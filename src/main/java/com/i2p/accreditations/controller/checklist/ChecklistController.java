package com.i2p.accreditations.controller.checklist;

import com.i2p.accreditations.dto.ChecklistDto;
import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.checklist.ChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import java.util.List;
import java.util.UUID;

@RestController
@ProtectedEndpoint
@RequestMapping("/api/checklists")
public class ChecklistController {

    private final ChecklistService service;

    @Autowired
    public ChecklistController(ChecklistService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Checklist> create(@RequestBody ChecklistDto checklistDto) {
        return ResponseEntity.ok(service.createChecklist(checklistDto));
    }

    @GetMapping("/getAllByChapterId/{id}")
    public ResponseEntity<List<Checklist>> getAllByChapterId(@PathVariable("id") UUID chapterId) {
        return ResponseEntity.ok(service.getAllChecklists(chapterId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checklist> getById(@PathVariable UUID id) {
        return service.getChecklistById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Checklist> update(@PathVariable UUID id, @RequestBody Checklist checklist) {
        return ResponseEntity.ok(service.updateChecklist(id, checklist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChecklist(id);
        return ResponseEntity.noContent().build();
    }
}

