package com.wistron.springboot.springbootlearn.controller;

import com.wistron.springboot.springbootlearn.domain.dto.content.ShareAuditDTO;
import com.wistron.springboot.springbootlearn.domain.entity.content.Share;
import com.wistron.springboot.springbootlearn.service.content.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareAdminController {
    private final ShareService shareService;

    @PutMapping("audit/{id}")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO shareAuditDTO) {
        // TODO  认证、授权
        return shareService.auditById(id, shareAuditDTO);
    }
}
