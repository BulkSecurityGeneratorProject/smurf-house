package com.smurfhouse.scratch.model;

import java.math.BigDecimal;

/**
 * Created by fmunozse on 6/7/16.
 */
public class ScratchHouse {

    private String id;
    private String title;
    private BigDecimal price;
    private Integer meters;
    private Integer numberRooms;
    private String details;
    private Boolean hasElevator;
    private Boolean hasGarage;
    private Boolean facingOutside;

    public Boolean getHasGarage() {
        return hasGarage;
    }

    public void setHasGarage(Boolean hasGarage) {
        this.hasGarage = hasGarage;
    }

    private String url;
    private String floor;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getMeters() {
        return meters;
    }

    public void setMeters(Integer meters) {
        this.meters = meters;
    }

    public Integer getNumberRooms() {
        return numberRooms;
    }

    public void setNumberRooms(Integer numberRooms) {
        this.numberRooms = numberRooms;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean getHasElevator() {
        return hasElevator;
    }

    public void setHasElevator(Boolean hasElevator) {
        this.hasElevator = hasElevator;
    }

    public Boolean getFacingOutside() {
        return facingOutside;
    }

    public void setFacingOutside(Boolean facingOutside) {
        this.facingOutside = facingOutside;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "id: " + id +
                ", title: " + title +
                ", price: " + price +
                ", meters: " + meters +
                ", numberRooms: " + numberRooms +
                ", floor: " + floor +
                ", hasElevator: " + hasElevator +
                ", facingOutside: " + facingOutside +
                ", hasGarage: " + hasGarage +
                ", url: " + url +
                ", details: " + details;
    }


}
