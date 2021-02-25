package com.example.retrydemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RetryDemoApplication

fun main(args: Array<String>) {
	runApplication<RetryDemoApplication>(*args)
}
