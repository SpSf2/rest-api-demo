package com.example.services;

import java.util.List;

import com.example.entities.Presentation;

public interface PresentationService {

    List<Presentation> findAll();
    
    Presentation findById(int id);

    void save(Presentation presentation);
}
