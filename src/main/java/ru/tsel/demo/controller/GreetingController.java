package ru.tsel.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	@GetMapping("/")
	public ResponseEntity<String> greeting() {
		return ResponseEntity.ok("Hello there");
	}
}
