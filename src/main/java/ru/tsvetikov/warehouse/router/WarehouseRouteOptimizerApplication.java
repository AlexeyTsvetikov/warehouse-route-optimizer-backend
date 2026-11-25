package ru.tsvetikov.warehouse.router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.retry.annotation.EnableRetry;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@EnableRetry
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WarehouseRouteOptimizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarehouseRouteOptimizerApplication.class, args);
	}

}
