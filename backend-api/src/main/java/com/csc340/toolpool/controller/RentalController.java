
package com.csc340.toolpool.controller;

import com.csc340.toolpool.dto.RentalCreateRequest;
import com.csc340.toolpool.dto.RentalResponse;
import com.csc340.toolpool.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<RentalResponse> createRental(
            @Valid @RequestBody RentalCreateRequest request) {

        RentalResponse rental = rentalService.createRental(request);

        return ResponseEntity
                .created(URI.create("/api/rentals/" + rental.id()))
                .body(rental);
    }

    @GetMapping("/{id}")
    public RentalResponse getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id);
    }

    @GetMapping("/renter/{renterId}")
    public List<RentalResponse> getRentalsByRenter(
            @PathVariable Long renterId) {

        return rentalService.getRentalsByRenter(renterId);
    }

    @PatchMapping("/{id}/complete")
public RentalResponse completeRental(@PathVariable Long id) {
    return rentalService.completeRental(id);
}

}
