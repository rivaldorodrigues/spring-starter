package com.github.rivaldorodrigues.springstarter.application.controllers;

import com.github.rivaldorodrigues.springstarter.domain.aggregate.profile.Profile;
import com.github.rivaldorodrigues.springstarter.domain.aggregate.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "profiles")
public class ProfileController {

    private ProfileRepository profileRepository;

    @Autowired
    public ProfileController(ProfileRepository perfilRepository) {
        this.profileRepository = perfilRepository;
    }

    @GetMapping
    public List<Profile> listAll() {
        return profileRepository.findAll();
    }
}
