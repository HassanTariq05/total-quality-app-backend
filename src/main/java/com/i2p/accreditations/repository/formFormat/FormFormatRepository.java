package com.i2p.accreditations.repository.formFormat;

import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formFormat.FormFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormFormatRepository extends JpaRepository<FormFormat, UUID> {
    Optional<FormFormat> findTopByOrderByNumberDesc();
    Optional<FormFormat> findByFormId(UUID formId);

}
