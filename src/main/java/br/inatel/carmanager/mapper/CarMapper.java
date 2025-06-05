package br.inatel.carmanager.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import br.inatel.carmanager.model.dto.CarDTO;
import br.inatel.carmanager.model.entity.Car;
import br.inatel.carmanager.model.entity.Repair;

public class CarMapper
{
    public static List<Car> toCarList(List<CarDTO> carDTOList)
    {
        return carDTOList.stream().map(CarMapper::toCar).collect(Collectors.toList());
    }

    public static List<CarDTO> toCarDtoList(List<Car> carList)
    {
        return carList.stream().map(CarMapper::toCarDto).collect(Collectors.toList());
    }

    public static Car toCar(CarDTO carDTO)
    {
        Car car = Car.builder()
                        .id(carDTO.getId())
                        .brandId(carDTO.getBrandId())
                        .name(carDTO.getName())
                        .repairList(new ArrayList<>())
                        .build();

        carDTO.getRepair().entrySet().stream().forEach(repair -> car.addRepair(
            Repair.builder()
                      .date(repair.getKey())
                      .time(repair.getValue())
                      .build()));

        return car;
    }

    public static CarDTO toCarDto(Car car)
    {
        CarDTO carDTO = CarDTO.builder()
                                 .id(car.getId())
                                 .brandId(car.getBrandId())
                                 .name(car.getName())
                                 .repair(new HashMap<>())
                                 .build();

        carDTO.setRepair(car.getRepairList().stream()
                                  .collect(
                                      Collectors.toMap(Repair::getDate, Repair::getTime)));

        return carDTO;
    }
}
