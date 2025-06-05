package br.inatel.carmanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.inatel.carmanager.exception.BrandNotFoundException;
import br.inatel.carmanager.mapper.CarMapper;
import br.inatel.carmanager.model.dto.CarDTO;
import br.inatel.carmanager.model.entity.Car;
import br.inatel.carmanager.repository.CarRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CarService
{
    private BrandManagerService brandManagerService;
    private CarRepository carRepository;

    @Autowired
    public CarService(BrandManagerService brandManagerService,
                       CarRepository carRepository)
    {
        this.brandManagerService = brandManagerService;
        this.carRepository = carRepository;
    }

    public CarDTO saveCar(CarDTO carDTO)
    {
        Car car = CarMapper.toCar(carDTO);

        if (this.isBrandValid(car))
        {
            return CarMapper.toCarDto(carRepository.save(car));
        }
        throw new BrandNotFoundException(car);
    }

    public List<CarDTO> getCarByBrandId(String brandId)
    {
        return CarMapper.toCarDtoList(carRepository.findByBrandId(brandId));
    }

    public List<CarDTO> getAllCar()
    {
        return CarMapper.toCarDtoList(carRepository.findAll());
    }

    private Boolean isBrandValid(Car car)
    {
        return brandManagerService.getAllBrand().stream()
                                      .noneMatch(
                                          brand -> brand.getId()
                                                                .equals(car.getBrandId()));
    }
}
