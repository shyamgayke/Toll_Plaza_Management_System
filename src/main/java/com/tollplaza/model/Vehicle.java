package com.tollplaza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @Column(name = "VehicleID")
    private Integer vehicleId;

    @Column(name = "VehicleNumber", nullable = false, unique = true)
    private String vehicleNumber;

    @Column(name = "FASTagNumber", unique = true)
    private String fasTagNumber;

    @Column(name = "PassID")
    private Integer passId;

    @Column(name = "PassType")
    private String passType;

    @Column(name = "VehicleCategory")
    private String vehicleCategory;

    public Vehicle() {}

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getFasTagNumber() {
        return fasTagNumber;
    }

    public void setFasTagNumber(String fasTagNumber) {
        this.fasTagNumber = fasTagNumber;
    }

    public Integer getPassId() {
        return passId;
    }

    public void setPassId(Integer passId) {
        this.passId = passId;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }
}
