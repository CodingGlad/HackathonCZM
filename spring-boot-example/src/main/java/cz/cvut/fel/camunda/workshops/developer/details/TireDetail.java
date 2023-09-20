package cz.cvut.fel.camunda.workshops.developer.details;

import lombok.Data;

@Data
public class TireDetail {
    private String spzOfCar;
    private String sizeOfTires;
    private String depthOfTreads;
    private String tireManufacturer;
    private String placeInStorge;
    private boolean areTiresStored;
    private String seasonType;
}
