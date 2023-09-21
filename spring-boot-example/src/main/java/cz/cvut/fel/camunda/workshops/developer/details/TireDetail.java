package cz.cvut.fel.camunda.workshops.developer.details;

import lombok.Data;

import java.util.UUID;

@Data
public class TireDetail {
    private UUID code;
    private String spzOfCar;
    private String depthOfTreads;
    private String sizeOfTires;
    private String tireManufacturer;
    private String isStored;
    private String placeInStorage;
    private String seasonType;
    private String notes;
}
