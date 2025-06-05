package br.inatel.carmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.inatel.carmanager.exception.BrandNotFoundException;
import br.inatel.carmanager.model.dto.CarDTO;
import br.inatel.carmanager.model.entity.Car;
import br.inatel.carmanager.model.entity.Repair;
import br.inatel.carmanager.model.rest.Brand;
import br.inatel.carmanager.repository.CarRepository;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest
{
    private Map<LocalDate, BigDecimal> repairMap;
    private CarDTO carDTO;
    private List<Repair> repairList;
    private Car car;
    private List<Brand> brandList;

    @Mock
    private CarRepository carRepository;

    @Mock
    private BrandManagerService brandManagerService;

    @InjectMocks
    private CarService carService;

    @BeforeEach
    public void init()
    {
        carDTO = CarDTO.builder()
                         .id(1L)
                         .brandId("ford")
                         .build();

        car = Car.builder()
                   .id(1L)
                   .brandId("ford")
                   .build();

        repairMap = new HashMap<>();
        repairList = new ArrayList<>();
        brandList = new ArrayList<>();

        for (int i = 0; i < 10; i++)
        {
            LocalDate date = LocalDate.now().plusDays(i);
            BigDecimal value = BigDecimal.valueOf(Math.random() * 24);

            repairMap.put(date, value);

            repairList.add(Repair.builder()
                                         .id((long) i)
                                         .date(date)
                                         .time(value)
                                         .car(car)
                                         .build());
        }

        carDTO.setRepair(repairMap);
        car.setRepairList(repairList);

        brandList.add(Brand.builder().id("ford").name("Ford Motor Company").build());
    }

    @Test
    public void givenCarDTO_whenSaveCarAndBrandIdIsValid_shouldReturnCarDTO()
    {
        when(brandManagerService.getAllBrand()).thenReturn(brandList);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        carDTO = carService.saveCar(carDTO);

        assertThat(carDTO).isNotNull();
        assertThat(carDTO.getId()).isEqualTo(1L);
        assertThat(carDTO.getBrandId()).isEqualTo("fiat");
        assertThat(carDTO.getRepair()).isNotNull();
        assertThat(carDTO.getRepair().size()).isEqualTo(10);
    }

    @Test
    public void givenGetAllCar_whenGetAllCar_shouldReturnCarDTOList()
    {
        when(carRepository.findAll()).thenReturn(Arrays.asList(car));

        List<CarDTO> carDTOList = carService.getAllCar();

        assertThat(carDTOList).isNotNull();
        assertThat(carDTOList.size()).isEqualTo(1L);
        assertThat(carDTOList.get(0).getId()).isEqualTo(1L);
        assertThat(carDTOList.get(0).getBrandId()).isEqualTo("fiat");
        assertThat(carDTOList.get(1).getRepair()).isNotNull();
        assertThat(carDTOList.get(0).getRepair()).isEqualTo(10);
    }
}