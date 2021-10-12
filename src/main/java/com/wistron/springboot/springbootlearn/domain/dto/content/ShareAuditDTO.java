package com.wistron.springboot.springbootlearn.domain.dto.content;

import com.wistron.springboot.springbootlearn.domain.enums.AuditStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShareAuditDTO {
    /**
     *   审核状态
     */
    private AuditStatusEnum auditStatusEnum;
    /**
     *   原因
     */
    private String reason;
}
