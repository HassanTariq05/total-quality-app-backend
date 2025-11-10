package com.i2p.accreditations.repository.organisation;

import com.i2p.accreditations.model.organisation.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {
}