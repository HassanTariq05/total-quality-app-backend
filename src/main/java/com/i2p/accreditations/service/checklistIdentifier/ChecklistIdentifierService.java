package com.i2p.accreditations.service.checklistIdentifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2p.accreditations.model.checklistIdentifier.ChecklistIdentifier;
import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
import com.i2p.accreditations.repository.checklistIdentifier.ChecklistIdentifierRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChecklistIdentifierService {

    private final ChecklistIdentifierRepository identifierRepository;
    private final ObjectMapper objectMapper;

    public ChecklistIdentifierService(ChecklistIdentifierRepository identifierRepository, ObjectMapper objectMapper) {
        this.identifierRepository = identifierRepository;
        this.objectMapper = objectMapper;
    }

    @Async
    @Transactional
    public void saveIdentifiersAsync(ChecklistSubmission submission) {
        try {
            if (submission.getData() == null) return;

            identifierRepository.deleteBySubmissionId(submission.getId());

            List<com.i2p.accreditations.service.checklistIdentifier.ChecklistIdentifierService.ChecklistIdentifierDto> items = objectMapper.readValue(
                    submission.getData(),
                    new TypeReference<List<com.i2p.accreditations.service.checklistIdentifier.ChecklistIdentifierService.ChecklistIdentifierDto>>() {}
            );

            for (com.i2p.accreditations.service.checklistIdentifier.ChecklistIdentifierService.ChecklistIdentifierDto item : items) {
                if (item.getIdentifier() != null && item.getValue() != null) {
                    ChecklistIdentifier identifier = new ChecklistIdentifier();
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

    private static class ChecklistIdentifierDto {
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
