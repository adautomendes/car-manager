package br.inatel.carmanager.exception;

import br.inatel.carmanager.model.entity.Car;

public class BrandNotFoundException extends RuntimeException
{
    public BrandNotFoundException(Car car)
    {
        super(
            String.format("Brand with brandId='%s' was not found.", car.getBrandId()));
    }
}
