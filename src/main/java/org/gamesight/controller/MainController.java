package org.gamesight.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	/*
	The MainController provides the REST services for misc. mappings
	 */

	// Mapping for no URL.
	@GetMapping("/")
	@ResponseStatus(HttpStatus.OK)
	String sayHello() {
		return "Gamesight says Hello!";
	}
}
