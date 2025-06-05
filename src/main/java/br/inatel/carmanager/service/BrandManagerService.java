package br.inatel.carmanager.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientException;

import br.inatel.carmanager.adapter.BrandManagerAdapter;
import br.inatel.carmanager.exception.BrandManagerConnectionException;
import br.inatel.carmanager.model.rest.Notification;
import br.inatel.carmanager.model.rest.Brand;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BrandManagerService
{
    private final BrandManagerAdapter brandManagerAdapter;
    @Value("${server.host}")
    private String serverHost;
    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public BrandManagerService(BrandManagerAdapter brandManagerAdapter)
    {
        this.brandManagerAdapter = brandManagerAdapter;
    }

    @Cacheable(cacheNames = "brand")
    public List<Brand> getAllBrand()
    {
        try
        {
            Brand[]
                brandArr =
                this.brandManagerAdapter.getBrandManagerWebClient()
                                            .get().uri("/brand").retrieve()
                                            .bodyToMono(Brand[].class)
                                            .block();
            return Arrays.asList(brandArr);
        }
        catch (WebClientException webClientException)
        {
            throw new BrandManagerConnectionException(
                this.brandManagerAdapter.getBrandManagerUrl());
        }
    }

    @CacheEvict(cacheNames = "brand")
    public void clearBrandCache()
    {
        log.info("Cache cleared");
    }

    public Notification[] registerOnBrandManager()
    {
        try
        {
            log.info("Registering at {}", this.brandManagerAdapter.getBrandManagerUrl());
            Notification notification = Notification.builder()
                                                    .host(this.serverHost)
                                                    .port(this.serverPort)
                                                    .build();

            return this.brandManagerAdapter.getBrandManagerWebClient().post()
                                               .uri("/notification")
                                               .body(BodyInserters.fromValue(notification))
                                               .retrieve()
                                               .bodyToMono(Notification[].class)
                                               .block();
        }
        catch (WebClientException webClientException)
        {
            webClientException.printStackTrace();
            throw new BrandManagerConnectionException(
                this.brandManagerAdapter.getBrandManagerUrl());
        }
    }
}
