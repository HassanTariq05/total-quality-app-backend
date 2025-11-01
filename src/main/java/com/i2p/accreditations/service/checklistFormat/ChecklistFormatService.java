package com.i2p.accreditations.service.checklistFormat;

import com.i2p.accreditations.dto.ChecklistFormatDto;
import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.model.checklistFormat.ChecklistFormat;
import com.i2p.accreditations.repository.checklist.ChecklistRepository;
import com.i2p.accreditations.repository.checklistFormat.ChecklistFormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ChecklistFormatService {

    private final ChecklistFormatRepository repository;
    private final ChecklistRepository checklistRepository;


    @Autowired
    public ChecklistFormatService( ChecklistFormatRepository repository, ChecklistRepository checklistRepository) {
        this.repository = repository;
        this.checklistRepository=checklistRepository;
    }

    public ChecklistFormat createChecklistFormat(ChecklistFormatDto checklistFormatDto) {
        Long nextNumber = repository.findTopByOrderByNumberDesc()
                .map(c -> c.getNumber() + 1)
                .orElse(1L);

        ChecklistFormat form = new ChecklistFormat();
        form.setNumber(nextNumber);
        form.setFormat(checklistFormatDto.getFormat());

        if (checklistFormatDto.getChecklistId() != null) {
            Checklist form1 = checklistRepository.findById(checklistFormatDto.getChecklistId())
                    .orElseThrow(() -> new RuntimeException("Checklist not found"));
            form.setChecklist(form1);
        }

        return repository.save(form);
    }

    public ChecklistFormat getChecklistFormatByFormId(UUID id) {
        return repository.findByChecklistId(id).orElse(null);
    }


    public ChecklistFormat updateChecklistFormat(UUID id, ChecklistFormat checklistFormatDetails) {
        return repository.findById(id).map(checklistFormat -> {
            checklistFormat.setFormat(checklistFormatDetails.getFormat());
            return repository.save(checklistFormat);
        }).orElseThrow(() -> new RuntimeException("Checklist not found with id " + id));
    }

    public void deleteChecklistFormat(UUID id) {
        repository.deleteById(id);
    }
}

