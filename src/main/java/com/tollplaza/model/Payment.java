package com.tollplaza.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @Column(name = "PaymentID")
    private Integer paymentId;

    @Column(name = "PaymentDate")
    private LocalDate paymentDate;

    @Column(name = "PaymentMode", nullable = false)
    private String paymentMode;

    @Column(name = "PaymentStatus")
    private String paymentStatus = "Completed";

    @Column(name = "TollAmount", nullable = false)
    private BigDecimal tollAmount;

    @Column(name = "VehicleID")
    private Integer vehicleId;

    @Column(name = "BoothID")
    private Integer boothId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VehicleID", referencedColumnName = "VehicleID", insertable = false, updatable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BoothID", referencedColumnName = "BoothID", insertable = false, updatable = false)
    private TollBooth tollBooth;

    public Payment() {}

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTollAmount() {
        return tollAmount;
    }

    public void setTollAmount(BigDecimal tollAmount) {
        this.tollAmount = tollAmount;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getBoothId() {
        return boothId;
    }

    public void setBoothId(Integer boothId) {
        this.boothId = boothId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public TollBooth getTollBooth() {
        return tollBooth;
    }

    public void setTollBooth(TollBooth tollBooth) {
        this.tollBooth = tollBooth;
    }
}
