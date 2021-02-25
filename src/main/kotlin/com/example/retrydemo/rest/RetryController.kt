package com.example.retrydemo.rest

import com.example.retrydemo.service.FailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class RetryController(
    @Autowired val failService: FailService
) {

    @GetMapping("/fail")
    fun alwaysFail(): String {
        return getRetryableSuccessOnThirdRetry(numberOfFailures = "-1")
    }

    @GetMapping("/successAfter")
    fun getRetryableSuccessOnThirdRetry(@RequestParam numberOfFailures: String): String {
        val id = UUID.randomUUID()
        val failures = if (numberOfFailures.isNullOrEmpty()) {
            0
        } else {
           numberOfFailures.toInt()
        }
        failService.fail(id, failures)
        return id.toString()
    }
}