package br.inatel.carmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CarManagerApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CarManagerApplication.class, args);
    }
}
