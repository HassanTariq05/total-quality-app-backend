package com.i2p.accreditations.repository.role;

import com.i2p.accreditations.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);

    boolean existsByNameAndIsDeletedFalse(String name);

    List<Role> findByOrganisationIdAndIsDeletedFalse(UUID organisationId);

    List<Role> findByIsDeletedFalse();

    Optional<Role> findByIdAndIsDeletedFalse(UUID id);
}
