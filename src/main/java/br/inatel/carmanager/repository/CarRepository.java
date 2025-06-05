package br.inatel.carmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.inatel.carmanager.model.entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, String>
{
    List<Car> findByBrandId(String brandId);
}
