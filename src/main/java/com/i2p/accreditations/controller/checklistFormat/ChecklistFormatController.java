package com.i2p.accreditations.controller.checklistFormat;

import com.i2p.accreditations.dto.ChecklistFormatDto;
import com.i2p.accreditations.model.checklistFormat.ChecklistFormat;
import com.i2p.accreditations.service.checklistFormat.ChecklistFormatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/checklistFormats")
public class ChecklistFormatController {

    private final ChecklistFormatService service;

    @Autowired
    public ChecklistFormatController(ChecklistFormatService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ChecklistFormat> create(@RequestBody ChecklistFormatDto formDto) {
        return ResponseEntity.ok(service.createChecklistFormat(formDto));
    }


    @GetMapping("/getChecklistFormatByChecklistId/{id}")
    public ResponseEntity<ChecklistFormat> getChecklistFormatByFormId(@PathVariable("id") UUID checklistId) {
        ChecklistFormat checklistFormat = service.getChecklistFormatByFormId(checklistId);
        return ResponseEntity.ok(checklistFormat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChecklistFormat> update(@PathVariable UUID id, @RequestBody ChecklistFormat checklistFormat) {
        return ResponseEntity.ok(service.updateChecklistFormat(id, checklistFormat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChecklistFormat(id);
        return ResponseEntity.noContent().build();
    }
}

