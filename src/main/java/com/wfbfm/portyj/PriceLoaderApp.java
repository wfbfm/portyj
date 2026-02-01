package com.wfbfm.portyj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PriceLoaderApp implements CommandLineRunner
{
    private final PriceLoader priceLoader;

    public PriceLoaderApp(final PriceLoader priceLoader)
    {
        this.priceLoader = priceLoader;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(PriceLoaderApp.class, args);
    }

    @Override
    public void run(final String... args) throws Exception
    {
        System.out.println("Price loader is running");
        this.priceLoader.fetchPrices();
    }
}
