
package com.csc340.toolpool.service;

import com.csc340.toolpool.dto.RentalCreateRequest;
import com.csc340.toolpool.dto.RentalResponse;
import com.csc340.toolpool.entity.Rental;
import com.csc340.toolpool.entity.RentalStatus;
import com.csc340.toolpool.entity.Tool;
import com.csc340.toolpool.entity.User;
import com.csc340.toolpool.repository.RentalRepository;
import com.csc340.toolpool.repository.ToolRepository;
import com.csc340.toolpool.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ToolRepository toolRepository;
    private final UserRepository userRepository;

    public RentalService(
            RentalRepository rentalRepository,
            ToolRepository toolRepository,
            UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.toolRepository = toolRepository;
        this.userRepository = userRepository;
    }

    public RentalResponse createRental(RentalCreateRequest request) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new BadRequestException("End date must be after start date.");
        }

        User renter = userRepository.findById(request.renterId())
                .orElseThrow(() -> new NotFoundException(
                        "User " + request.renterId() + " was not found."));

        Tool tool = toolRepository.findById(request.toolId())
                .orElseThrow(() -> new NotFoundException(
                        "Tool " + request.toolId() + " was not found."));

        if (!Boolean.TRUE.equals(tool.getAvailable())) {
            throw new ConflictException("This tool is not available for rental.");
        }

        if (tool.getOwner().getId().equals(renter.getId())) {
            throw new BadRequestException("You cannot rent your own tool.");
        }

        long rentalDays = ChronoUnit.DAYS.between(
                request.startDate(), request.endDate());

        BigDecimal totalPrice = tool.getDailyPrice()
                .multiply(BigDecimal.valueOf(rentalDays));

        Rental rental = new Rental();
        rental.setTool(tool);
        rental.setRenter(renter);
        rental.setStartDate(request.startDate());
        rental.setEndDate(request.endDate());
        rental.setStatus(RentalStatus.REQUESTED);
        rental.setTotalPrice(totalPrice);

        return RentalResponse.from(rentalRepository.save(rental));
    }

    @Transactional(readOnly = true)
    public RentalResponse getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Rental " + id + " was not found."));

        return RentalResponse.from(rental);
    }

    @Transactional(readOnly = true)
    public List<RentalResponse> getRentalsByRenter(Long renterId) {
        if (!userRepository.existsById(renterId)) {
            throw new NotFoundException("User " + renterId + " was not found.");
        }

        return rentalRepository.findByRenterId(renterId)
                .stream()
                .map(RentalResponse::from)
                .toList();
    }
}

