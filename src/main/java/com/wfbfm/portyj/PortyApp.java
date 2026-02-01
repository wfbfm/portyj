package com.wfbfm.portyj;

import com.wfbfm.portyj.position.PositionNormaliser;
import com.wfbfm.portyj.price.PriceLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PortyApp implements CommandLineRunner
{
    private final PositionNormaliser normaliser;
    private final PriceLoader loader;

    public PortyApp(final PositionNormaliser normaliser, final PriceLoader loader)
    {
        this.normaliser = normaliser;
        this.loader = loader;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(PortyApp.class, args);
    }

    @Override
    public void run(final String... args) throws Exception
    {

    }
}
