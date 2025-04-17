package com.example.ms_client;

import org.springframework.boot.SpringApplication;

public class TestMsClientApplication {

	public static void main(String[] args) {
		SpringApplication.from(MsClientApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
