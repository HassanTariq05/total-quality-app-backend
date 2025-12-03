package com.i2p.accreditations.service.formIdentifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2p.accreditations.model.formIdentifier.FormIdentifier;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import com.i2p.accreditations.repository.formIdentifier.FormIdentifierRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FormIdentifierService {

    private final FormIdentifierRepository identifierRepository;
    private final ObjectMapper objectMapper;

    public FormIdentifierService(FormIdentifierRepository identifierRepository, ObjectMapper objectMapper) {
        this.identifierRepository = identifierRepository;
        this.objectMapper = objectMapper;
    }

    @Async
    @Transactional
    public void saveIdentifiersAsync(FormSubmission submission) {
        try {
            if (submission.getData() == null) return;

            // Delete previous identifiers for this submission
            identifierRepository.deleteBySubmissionId(submission.getId());

            List<FormIdentifierDto> items = objectMapper.readValue(
                    submission.getData(),
                    new TypeReference<List<FormIdentifierDto>>() {}
            );

            for (FormIdentifierDto item : items) {
                if (item.getIdentifier() != null && item.getValue() != null) {
                    FormIdentifier identifier = new FormIdentifier();
                    identifier.setName(item.getIdentifier());
                    identifier.setValue(item.getValue().toString());
                    identifier.setSubmission(submission);

                    identifierRepository.save(identifier);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // DTO for parsing form data JSON
    private static class FormIdentifierDto {
        private String id;
        private String identifier;
        private Object value;

        // getters & setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
    }
}
