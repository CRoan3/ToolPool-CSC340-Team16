
package com.csc340.toolpool.service;

import com.csc340.toolpool.dto.ToolCreateRequest;
import com.csc340.toolpool.dto.ToolResponse;
import com.csc340.toolpool.entity.Tool;
import com.csc340.toolpool.entity.User;
import com.csc340.toolpool.repository.ToolRepository;
import com.csc340.toolpool.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csc340.toolpool.dto.ToolUpdateRequest;

import java.util.List;

@Service
@Transactional
public class ToolService {

    private final ToolRepository toolRepository;
    private final UserRepository userRepository;

    public ToolService(
            ToolRepository toolRepository,
            UserRepository userRepository) {
        this.toolRepository = toolRepository;
        this.userRepository = userRepository;
    }

    // Create a new tool listing.
    public ToolResponse createTool(ToolCreateRequest request) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new NotFoundException(
                        "User " + request.ownerId() + " was not found."));

        Tool tool = new Tool();
        tool.setName(request.name().trim());
        tool.setDescription(request.description().trim());
        tool.setCategory(request.category());
        tool.setDailyPrice(request.dailyPrice());
        tool.setDepositAmount(request.depositAmount());
        tool.setAvailable(true);
        tool.setOwner(owner);

        return ToolResponse.from(toolRepository.save(tool));
    }

    // Get all tools that are currently available.
    @Transactional(readOnly = true)
    public List<ToolResponse> getAvailableTools() {
        return toolRepository.findByAvailableTrue()
                .stream()
                .map(ToolResponse::from)
                .toList();
    }

    // Get one tool by its ID.
    @Transactional(readOnly = true)
    public ToolResponse getToolById(Long id) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Tool " + id + " was not found."));

        return ToolResponse.from(tool);
    }

    // edit tool details 
    public ToolResponse updateTool(Long id, ToolUpdateRequest request) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Tool " + id + " was not found."));

        tool.setName(request.name().trim());
        tool.setDescription(request.description().trim());
        tool.setCategory(request.category());
        tool.setDailyPrice(request.dailyPrice());
        tool.setDepositAmount(request.depositAmount());

        return ToolResponse.from(toolRepository.save(tool));
    }

    // Unlist a tool without permanently deleting its record.
    public ToolResponse unlistTool(Long id) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Tool " + id + " was not found."));

        tool.setAvailable(false);

        return ToolResponse.from(toolRepository.save(tool));
    }

}
