
package com.csc340.toolpool.controller;

import com.csc340.toolpool.dto.ToolCreateRequest;
import com.csc340.toolpool.dto.ToolResponse;
import com.csc340.toolpool.service.ToolService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.csc340.toolpool.dto.ToolUpdateRequest;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    // Create a new tool listing.
    @PostMapping
    public ResponseEntity<ToolResponse> createTool(
            @Valid @RequestBody ToolCreateRequest request) {
        ToolResponse tool = toolService.createTool(request);

        return ResponseEntity
                .created(URI.create("/api/tools/" + tool.id()))
                .body(tool);
    }

    // View all available tools.
    @GetMapping
    public List<ToolResponse> getAvailableTools() {
        return toolService.getAvailableTools();
    }

    // View one tool by ID.
    @GetMapping("/{id}")
    public ToolResponse getToolById(@PathVariable Long id) {
        return toolService.getToolById(id);
    }


    // Edit a tool listing.
    @PutMapping("/{id}")
    public ToolResponse updateTool(
            @PathVariable Long id,
            @Valid @RequestBody ToolUpdateRequest request) {
        return toolService.updateTool(id, request);
    }

    // Unlist a tool.
    @PatchMapping("/{id}/unlist")
    public ToolResponse unlistTool(@PathVariable Long id) {
        return toolService.unlistTool(id);
    }

}
