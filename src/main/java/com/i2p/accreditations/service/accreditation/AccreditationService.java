package com.i2p.accreditations.service.accreditation;

import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.repository.accreditation.AccreditationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccreditationService {

    private final AccreditationRepository repository;

    public AccreditationService(AccreditationRepository repository) {
        this.repository = repository;
    }

    public Accreditation createAccreditation(Accreditation accreditation) {
        return repository.save(accreditation);
    }

    public List<Accreditation> getAllAccreditations() {
        return repository.findAll();
    }

    public Optional<Accreditation> getAccreditationById(UUID id) {
        return repository.findById(id);
    }

    public Accreditation updateAccreditation(UUID id, Accreditation accreditationDetails) {
        return repository.findById(id).map(accreditation -> {
            accreditation.setName(accreditationDetails.getName());
            accreditation.setDescription(accreditationDetails.getDescription());
            accreditation.setStatus(accreditationDetails.getStatus());
            return repository.save(accreditation);
        }).orElseThrow(() -> new RuntimeException("Accreditation not found with id " + id));
    }

    public void deleteAccreditation(UUID id) {
        repository.deleteById(id);
    }
}

