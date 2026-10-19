package com.tollplaza.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TollPlazaContactId implements Serializable {

    @Column(name = "PlazaID")
    private Integer plazaId;

    @Column(name = "ContactNumber")
    private String contactNumber;

    public TollPlazaContactId() {}

    public TollPlazaContactId(Integer plazaId, String contactNumber) {
        this.plazaId = plazaId;
        this.contactNumber = contactNumber;
    }

    public Integer getPlazaId() {
        return plazaId;
    }

    public void setPlazaId(Integer plazaId) {
        this.plazaId = plazaId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TollPlazaContactId that = (TollPlazaContactId) o;
        return Objects.equals(plazaId, that.plazaId) && Objects.equals(contactNumber, that.contactNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plazaId, contactNumber);
    }
}
