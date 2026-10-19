package com.tollplaza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tollplazacontact")
public class TollPlazaContact {

    @EmbeddedId
    private TollPlazaContactId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("plazaId")
    @JoinColumn(name = "PlazaID")
    private TollPlaza tollPlaza;

    public TollPlazaContact() {}

    public TollPlazaContact(TollPlazaContactId id, TollPlaza tollPlaza) {
        this.id = id;
        this.tollPlaza = tollPlaza;
    }

    public TollPlazaContactId getId() {
        return id;
    }

    public void setId(TollPlazaContactId id) {
        this.id = id;
    }

    public TollPlaza getTollPlaza() {
        return tollPlaza;
    }

    public void setTollPlaza(TollPlaza tollPlaza) {
        this.tollPlaza = tollPlaza;
    }

    public Integer getPlazaId() {
        return id != null ? id.getPlazaId() : null;
    }

    public String getContactNumber() {
        return id != null ? id.getContactNumber() : null;
    }
}
