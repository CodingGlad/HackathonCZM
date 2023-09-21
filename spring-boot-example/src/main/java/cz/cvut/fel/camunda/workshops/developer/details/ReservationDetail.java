package cz.cvut.fel.camunda.workshops.developer.details;

import lombok.Data;

import java.util.UUID;

@Data
public class ReservationDetail {
    private UUID reservationId;
    private String dateTime;
}
