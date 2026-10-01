package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.AccessRequestCountsDTO;
import com.wakedrive.backend.dto.AccessRequestCreateDTO;
import com.wakedrive.backend.dto.AccessRequestDTO;
import com.wakedrive.backend.dto.PageResponseDTO;
import com.wakedrive.backend.entity.AccessRequestStatus;
import com.wakedrive.backend.service.AccessRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access-requests")
@RequiredArgsConstructor
public class AccessRequestController {

    private final AccessRequestService accessRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody AccessRequestCreateDTO request) {
        accessRequestService.create(request);
    }

    @GetMapping
    public PageResponseDTO<AccessRequestDTO> getAll(
            @RequestParam(required = false) AccessRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return accessRequestService.getAll(status, page, size);
    }

    @GetMapping("/counts")
    public AccessRequestCountsDTO getCounts() {
        return accessRequestService.getCounts();
    }

    @PatchMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {
        accessRequestService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public void reject(@PathVariable Long id) {
        accessRequestService.reject(id);
    }
}
