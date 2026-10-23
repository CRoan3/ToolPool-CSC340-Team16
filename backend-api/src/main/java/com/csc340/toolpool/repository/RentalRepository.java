
package com.csc340.toolpool.repository;

import com.csc340.toolpool.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByRenterId(Long renterId);

    List<Rental> findByToolId(Long toolId);
}
