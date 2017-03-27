package com.byd.ats.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byd.ats.entity.Person;

public interface PersonRepository  extends JpaRepository<Person, Long>{

}
