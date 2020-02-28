package com.motor.insurance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.motor.insurance.entity.Benificial;

@Repository
public interface BenificialDao extends JpaRepository<Benificial, Integer>{

}
