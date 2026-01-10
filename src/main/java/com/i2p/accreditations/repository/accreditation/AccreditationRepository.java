package com.i2p.accreditations.repository.accreditation;

import com.i2p.accreditations.model.accreditation.Accreditation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccreditationRepository extends JpaRepository<Accreditation, UUID> {

    @Query("SELECT a FROM Accreditation a JOIN a.organisations o WHERE o.id = :orgId")
    List<Accreditation> findByOrganisationsId(@Param("orgId") UUID orgId);
}
