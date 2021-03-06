package com.motor.insurance.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.motor.insurance.entity.Claim;

@Repository
public interface ClaimListingDao  extends JpaRepository<Claim, Integer>{

}
