package com.wfbfm.portyj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PositionLoaderApp implements CommandLineRunner
{
    private final PositionNormaliser normaliser;

    public PositionLoaderApp(final PositionNormaliser normaliser)
    {
        this.normaliser = normaliser;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(PositionLoaderApp.class, args);
    }

    @Override
    public void run(final String... args) throws Exception
    {
        this.normaliser.initialisePositions("positions.csv");
    }
}
