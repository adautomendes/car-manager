package br.inatel.carmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.inatel.carmanager.service.BrandManagerService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/brandcache")
@Slf4j
public class BrandController
{
    private BrandManagerService brandManagerService;

    @Autowired
    public BrandController(BrandManagerService brandManagerService)
    {
        this.brandManagerService = brandManagerService;
    }

    @DeleteMapping
    public void deleteBrandCache()
    {
        brandManagerService.clearBrandCache();
    }
}
