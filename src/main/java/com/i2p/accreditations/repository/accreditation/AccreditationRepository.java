package com.i2p.accreditations.repository.accreditation;

import com.i2p.accreditations.model.accreditation.Accreditation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccreditationRepository extends JpaRepository<Accreditation, UUID> {
}
