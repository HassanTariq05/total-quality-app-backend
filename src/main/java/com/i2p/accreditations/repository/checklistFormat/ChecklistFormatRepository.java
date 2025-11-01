package com.i2p.accreditations.repository.checklistFormat;

import com.i2p.accreditations.model.checklistFormat.ChecklistFormat;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formFormat.FormFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistFormatRepository extends JpaRepository<ChecklistFormat, UUID> {
    Optional<ChecklistFormat> findTopByOrderByNumberDesc();
    Optional<ChecklistFormat> findByChecklistId(UUID checklistId);

}
