package com.wfbfm.portyj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.expression.Sets;

import java.util.*;

@SpringBootApplication
public class PortyjApplication implements CommandLineRunner
{
    private final PositionNormaliser normaliser;

    public PortyjApplication(final PositionNormaliser normaliser)
    {
        this.normaliser = normaliser;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(PortyjApplication.class, args);
    }

    @Override
    public void run(final String... args) throws Exception
    {
        this.normaliser.initialisePositions("positions.csv");
    }
}
