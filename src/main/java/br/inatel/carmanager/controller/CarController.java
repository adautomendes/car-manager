package br.inatel.carmanager.controller;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.inatel.carmanager.model.dto.CarDTO;
import br.inatel.carmanager.service.CarService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/car")
@Slf4j
public class CarController
{
    private CarService carService;

    @Autowired
    public CarController(CarService carService)
    {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<CarDTO>> getCars(
        @RequestParam(required = false) Optional<String> brandId)
    {
        List<CarDTO> carDtoList;
        if (brandId.isPresent())
        {
            carDtoList = carService.getCarByBrandId(brandId.get());
        }
        else
        {
            carDtoList = carService.getAllCar();
        }

        return ResponseEntity.ok(carDtoList);
    }

    @PostMapping
    public ResponseEntity<CarDTO> saveCar(@Valid @RequestBody CarDTO carDTO)
    {
        return ResponseEntity.created(null).body(carService.saveCar(carDTO));
    }
}
