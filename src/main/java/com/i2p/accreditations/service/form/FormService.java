package com.i2p.accreditations.service.form;

import com.i2p.accreditations.dto.FormDto;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formFormat.FormFormat;
import com.i2p.accreditations.repository.chapter.ChapterRepository;
import com.i2p.accreditations.repository.formFormat.FormFormatRepository;
import com.i2p.accreditations.repository.form.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FormService {

    private final FormRepository repository;
    private final FormFormatRepository formFormatRepository;

    private final ChapterRepository chapterRepository;

    @Autowired
    public FormService(FormRepository formRepository, ChapterRepository chapterRepository, FormFormatRepository formFormatRepository) {
        this.chapterRepository = chapterRepository;
        this.repository = formRepository;
        this.formFormatRepository = formFormatRepository;
    }

    public Form createForm(FormDto formDto) {
        Long nextNumber = repository.findTopByOrderByNumberDesc()
                .map(c -> c.getNumber() + 1)
                .orElse(1L);

        Form form = new Form();
        form.setNumber(nextNumber);
        form.setTitle(formDto.getTitle());
        form.setDescription(formDto.getDescription());
        form.setStatus(formDto.getStatus());

        if (formDto.getChapterId() != null) {
            Chapter chapter = chapterRepository.findById(formDto.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
            form.setChapter(chapter);
        }

        return repository.save(form);
    }

    public List<Form> getAllForms(UUID chapterId) {
        return repository.findByChapterId(chapterId);
    }
    public Optional<Form> getFormById(UUID id) {
        return repository.findByIdWithChapter(id);
    }


    public Form updateForm(UUID id, Form formDetails) {
        return repository.findById(id).map(form -> {
            form.setTitle(formDetails.getTitle());
            form.setDescription(formDetails.getDescription());
            form.setStatus(formDetails.getStatus());
            return repository.save(form);
        }).orElseThrow(() -> new RuntimeException("Form not found with id " + id));
    }

    public void deleteForm(UUID id) {
        repository.deleteById(id);
    }
}

