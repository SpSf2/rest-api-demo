package com.example;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Presentation;

public interface PresentacionDao extends JpaRepository<Presentation, Integer> {


}
