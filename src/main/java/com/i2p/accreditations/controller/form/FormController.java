package com.i2p.accreditations.controller.form;

import com.i2p.accreditations.dto.FormDto;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formFormat.FormFormat;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.form.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import java.util.List;
import java.util.UUID;

@RestController
@ProtectedEndpoint
@RequestMapping("/api/forms")
public class FormController {

    private final FormService service;

    @Autowired
    public FormController(FormService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Form> create(@RequestBody FormDto formDto) {
        return ResponseEntity.ok(service.createForm(formDto));
    }

    @GetMapping("/getAllByChapterId/{id}")
    public ResponseEntity<Page<Form>> getAllByChapterId(
            @PathVariable("id") UUID chapterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getAllForms(chapterId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Form> getById(@PathVariable UUID id) {
        return service.getFormById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Form> update(@PathVariable UUID id, @RequestBody Form form) {
        return ResponseEntity.ok(service.updateForm(id, form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteForm(id);
        return ResponseEntity.noContent().build();
    }
}

