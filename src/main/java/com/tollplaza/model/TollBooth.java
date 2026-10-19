package com.tollplaza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tollbooth")
public class TollBooth {

    @Id
    @Column(name = "BoothID")
    private Integer boothId;

    @Column(name = "BoothStatus")
    private String boothStatus = "Active";

    @Column(name = "PlazaID")
    private Integer plazaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlazaID", referencedColumnName = "PlazaID", insertable = false, updatable = false)
    private TollPlaza tollPlaza;

    public TollBooth() {}

    public Integer getBoothId() {
        return boothId;
    }

    public void setBoothId(Integer boothId) {
        this.boothId = boothId;
    }

    public String getBoothStatus() {
        return boothStatus;
    }

    public void setBoothStatus(String boothStatus) {
        this.boothStatus = boothStatus;
    }

    public Integer getPlazaId() {
        return plazaId;
    }

    public void setPlazaId(Integer plazaId) {
        this.plazaId = plazaId;
    }

    public TollPlaza getTollPlaza() {
        return tollPlaza;
    }

    public void setTollPlaza(TollPlaza tollPlaza) {
        this.tollPlaza = tollPlaza;
    }
}
