package com.i2p.accreditations.dto;

import com.i2p.accreditations.enums.PolicyVersionStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PolicyVersionCreateDto {
    private String title;
    private String description;
    private PolicyVersionStatus status = PolicyVersionStatus.DRAFT;
    private MultipartFile document;
}