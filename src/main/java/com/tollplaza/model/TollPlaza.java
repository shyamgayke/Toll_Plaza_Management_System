package com.tollplaza.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tollplaza")
public class TollPlaza {

    @Id
    @Column(name = "PlazaID", nullable = false)
    private Integer plazaId;

    @Column(name = "PlazaName", length = 50, nullable = false)
    private String plazaName;

    @Column(name = "HighwayName", length = 50, nullable = false)
    private String highwayName;

    @Column(name = "City", length = 30, nullable = false)
    private String city;

    @Column(name = "State", length = 20, nullable = false)
    private String state;

    @Column(name = "PlazaStatus", length = 20)
    private String plazaStatus = "Operational";

    @OneToMany(mappedBy = "tollPlaza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TollPlazaContact> contacts;

    @OneToMany(mappedBy = "tollPlaza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TollBooth> booths;

    public TollPlaza() {}

    public Integer getPlazaId() {
        return plazaId;
    }

    public void setPlazaId(Integer plazaId) {
        this.plazaId = plazaId;
    }

    public String getPlazaName() {
        return plazaName;
    }

    public void setPlazaName(String plazaName) {
        this.plazaName = plazaName;
    }

    public String getHighwayName() {
        return highwayName;
    }

    public void setHighwayName(String highwayName) {
        this.highwayName = highwayName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPlazaStatus() {
        return plazaStatus;
    }

    public void setPlazaStatus(String plazaStatus) {
        this.plazaStatus = plazaStatus;
    }

    public List<TollPlazaContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<TollPlazaContact> contacts) {
        this.contacts = contacts;
    }

    public List<TollBooth> getBooths() {
        return booths;
    }

    public void setBooths(List<TollBooth> booths) {
        this.booths = booths;
    }
}
