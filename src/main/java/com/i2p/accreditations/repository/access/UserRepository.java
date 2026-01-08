package com.i2p.accreditations.repository.access;

import com.i2p.accreditations.model.access.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findByOrganisationId(UUID organisationId);

}