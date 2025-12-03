package com.i2p.accreditations.service.policy;

import com.i2p.accreditations.dto.PolicyDto;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.model.policy.Policy;
import com.i2p.accreditations.repository.chapter.ChapterRepository;
import com.i2p.accreditations.repository.policy.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class PolicyService {

    private final PolicyRepository repository;

    private final ChapterRepository chapterRepository;

    @Autowired
    public PolicyService(PolicyRepository policyRepository, ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
        this.repository = policyRepository;
    }

    // createPolicy
    public Policy createPolicy(String title, String description, String status, UUID chapterId, MultipartFile document) throws IOException {
        Long nextNumber = repository.findTopByOrderByNumberDesc()
                .map(c -> c.getNumber() + 1)
                .orElse(1L);

        Policy policy = new Policy();
        policy.setNumber(nextNumber);
        policy.setTitle(title);
        policy.setDescription(description);
        policy.setStatus(status);

        if (chapterId != null) {
            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
            policy.setChapter(chapter);
        }

        if (document != null && !document.isEmpty()) {
            policy.setDocument(document.getBytes());
            policy.setDocumentName(document.getOriginalFilename()); // <-- store filename
        }

        return repository.save(policy);
    }

    // updatePolicy
    public Policy updatePolicy(UUID id, String title, String description, String status, UUID chapterId, MultipartFile document) throws IOException {
        return repository.findById(id).map(policy -> {
            policy.setTitle(title);
            policy.setDescription(description);
            policy.setStatus(status);

            if (chapterId != null) {
                Chapter chapter = chapterRepository.findById(chapterId)
                        .orElseThrow(() -> new RuntimeException("Chapter not found"));
                policy.setChapter(chapter);
            }

            // Update document if provided
            try {
                if (document != null && !document.isEmpty()) {
                    policy.setDocument(document.getBytes());
                    policy.setDocumentName(document.getOriginalFilename());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read document", e);
            }

            return repository.save(policy);
        }).orElseThrow(() -> new RuntimeException("Policy not found with id " + id));
    }




    public Page<Policy> getAllPolicys(UUID chapterId, Pageable pageable) {
        return repository.findByChapterId(chapterId, pageable);
    }
    public Optional<Policy> getPolicyById(UUID id) {
        return repository.findByIdWithChapter(id);
    }

    public void deletePolicy(UUID id) {
        repository.deleteById(id);
    }
}


