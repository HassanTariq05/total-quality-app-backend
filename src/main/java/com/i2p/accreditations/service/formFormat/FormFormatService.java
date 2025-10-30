package com.i2p.accreditations.service.formFormat;

import com.i2p.accreditations.dto.FormFormatDto;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formFormat.FormFormat;
import com.i2p.accreditations.repository.form.FormRepository;
import com.i2p.accreditations.repository.formFormat.FormFormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class FormFormatService {

    private final FormFormatRepository repository;
    private final FormRepository formRepository;


    @Autowired
    public FormFormatService( FormFormatRepository repository, FormRepository formRepository) {
        this.repository = repository;
        this.formRepository=formRepository;
    }

    public FormFormat createFormFormat(FormFormatDto formDto) {
        Long nextNumber = repository.findTopByOrderByNumberDesc()
                .map(c -> c.getNumber() + 1)
                .orElse(1L);

        FormFormat form = new FormFormat();
        form.setNumber(nextNumber);
        form.setFormat(formDto.getFormat());

        if (formDto.getFormId() != null) {
            Form form1 = formRepository.findById(formDto.getFormId())
                    .orElseThrow(() -> new RuntimeException("Form not found"));
            form.setForm(form1);
        }

        return repository.save(form);
    }

    public FormFormat getFormFormatByFormId(UUID id) {
        return repository.findByFormId(id).orElse(null);
    }


    public FormFormat updateFormFormat(UUID id, FormFormat formFormatDetails) {
        return repository.findById(id).map(formFormat -> {
            formFormat.setFormat(formFormatDetails.getFormat());
            return repository.save(formFormat);
        }).orElseThrow(() -> new RuntimeException("Form not found with id " + id));
    }

    public void deleteFormFormat(UUID id) {
        repository.deleteById(id);
    }
}

