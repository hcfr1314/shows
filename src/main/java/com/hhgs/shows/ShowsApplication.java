package com.hhgs.shows;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.hhgs.shows.mapper")
@EnableScheduling
public class ShowsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowsApplication.class, args);
	}

}
