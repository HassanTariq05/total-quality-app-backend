package com.i2p.accreditations.service.organisation;

import com.i2p.accreditations.dto.OrganisationRequestDto;
import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.accreditation.AccreditationRepository;
import com.i2p.accreditations.repository.organisation.OrganisationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrganisationService {

    private final OrganisationRepository repository;
    private final AccreditationRepository accreditationRepository;

    public OrganisationService(OrganisationRepository repository, AccreditationRepository accreditationRepository) {
        this.repository = repository;
        this.accreditationRepository = accreditationRepository;
    }

    @Transactional
    public Organisation createOrganisation(OrganisationRequestDto dto) {
        Organisation organisation = new Organisation();
        organisation.setName(dto.getName());
        organisation.setEmail(dto.getEmail());
        organisation.setPhoneNumber(dto.getPhoneNumber());
        organisation.setDescription(dto.getDescription());
        organisation.setStatus(dto.getStatus());
        if (!dto.getAccreditationIds().isEmpty()) {
            List<Accreditation> found = accreditationRepository.findAllById(dto.getAccreditationIds());

            if (found.size() != dto.getAccreditationIds().size()) {
                throw new IllegalArgumentException("Some accreditation IDs were not found");
            }
            Set<Accreditation> accs = new HashSet<>(found);

            organisation.getAccreditations().addAll(accs);
        }

        return repository.save(organisation);
    }

    public List<Organisation> getAllOrganisations() {
        return repository.findAll();
    }

    public Optional<Organisation> getOrganisationById(UUID id) {
        return repository.findById(id);
    }

    public List<Accreditation> getAccreditationsByOrganisationIdEfficient(UUID orgId) {
        return accreditationRepository.findByOrganisationsId(orgId);
    }

    @Transactional
    public Organisation updateOrganisation(UUID id, OrganisationRequestDto dto) {
        Organisation organisation = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organisation not found"));

        // Always allowed fields (both roles)
        organisation.setName(dto.getName());
        organisation.setEmail(dto.getEmail());
        organisation.setPhoneNumber(dto.getPhoneNumber());
        organisation.setDescription(dto.getDescription());
        organisation.setStatus(dto.getStatus());

        if (isSuperAdmin()) {
            organisation.getAccreditations().clear();

            if (!dto.getAccreditationIds().isEmpty()) {
                List<Accreditation> found = accreditationRepository.findAllById(dto.getAccreditationIds());

                if (found.size() != dto.getAccreditationIds().size()) {
                    throw new IllegalArgumentException("Some accreditation IDs were not found");
                }

                Set<Accreditation> accs = new HashSet<>(found);
                organisation.getAccreditations().addAll(accs);
            }
        }

        return repository.save(organisation);
    }

    private boolean isSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Super Admin"));
    }



    public void deleteOrganisation(UUID id) {
        repository.deleteById(id);
    }
}
