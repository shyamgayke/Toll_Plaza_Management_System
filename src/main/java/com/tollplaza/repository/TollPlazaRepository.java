package com.tollplaza.repository;

import com.tollplaza.model.TollPlaza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TollPlazaRepository extends JpaRepository<TollPlaza, Integer> {
}
