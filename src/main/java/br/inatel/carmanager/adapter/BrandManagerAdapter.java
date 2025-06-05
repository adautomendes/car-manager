package br.inatel.carmanager.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BrandManagerAdapter
{
    @Value("${brand.manager.host}")
    private String brandManagerHost;

    @Value("${brand.manager.port}")
    private String brandManagerPort;

    @Bean
    public String getBrandManagerUrl()
    {
        return String.format("http://%s:%s", this.brandManagerHost, this.brandManagerPort);
    }

    @Bean
    public WebClient getBrandManagerWebClient()
    {
        return WebClient.builder()
                        .baseUrl(this.getBrandManagerUrl())
                        .build();
    }
}
