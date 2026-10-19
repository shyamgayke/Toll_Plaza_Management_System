package com.tollplaza.repository;

import com.tollplaza.model.TollPlazaContact;
import com.tollplaza.model.TollPlazaContactId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TollPlazaContactRepository extends JpaRepository<TollPlazaContact, TollPlazaContactId> {
}
