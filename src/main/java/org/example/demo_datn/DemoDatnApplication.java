package org.example.demo_datn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching

//@EnableMethodSecurity(prePostEnabled = true)
public class DemoDatnApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDatnApplication.class, args);
    }

}
