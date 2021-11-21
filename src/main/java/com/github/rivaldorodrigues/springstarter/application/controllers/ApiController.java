package com.github.rivaldorodrigues.springstarter.application.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ApiController {

    @Value("${info.app.version:unavailable}")
    private String appVersion;

    @GetMapping("version")
    public VersionInfo versao() {
        return new VersionInfo("v1", appVersion);
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class VersionInfo {
    private String api;
    private String aplicacao;
}
