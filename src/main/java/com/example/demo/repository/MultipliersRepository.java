package com.example.demo.repository;

import com.example.demo.model.Multipliers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultipliersRepository extends JpaRepository<Multipliers, Long> {
    Multipliers findMultipliersById(int id);
}
