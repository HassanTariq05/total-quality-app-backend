package com.i2p.accreditations.service.organisation;

import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.organisation.OrganisationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganisationService {

    private final OrganisationRepository repository;

    public OrganisationService(OrganisationRepository repository) {
        this.repository = repository;
    }

    public Organisation createOrganisation(Organisation organisation) {
        return repository.save(organisation);
    }

    public List<Organisation> getAllOrganisations() {
        return repository.findAll();
    }

    public Optional<Organisation> getOrganisationById(UUID id) {
        return repository.findById(id);
    }

    public Organisation updateOrganisation(UUID id, Organisation organisationDetails) {
        return repository.findById(id).map(organisation -> {
            organisation.setName(organisationDetails.getName());
            organisation.setDescription(organisationDetails.getDescription());
            organisation.setStatus(organisationDetails.getStatus());
            return repository.save(organisation);
        }).orElseThrow(() -> new RuntimeException("Organisation not found with id " + id));
    }

    public void deleteOrganisation(UUID id) {
        repository.deleteById(id);
    }
}
