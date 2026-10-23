package com.csc340.toolpool.repository;


import com.csc340.toolpool.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {

    List<Tool> findByAvailableTrue();

    List<Tool> findByCategoryIgnoreCaseAndAvailableTrue(String category);

    List<Tool> findByOwnerId(Long ownerId);
}
