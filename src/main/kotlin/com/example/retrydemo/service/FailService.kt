package com.example.retrydemo.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.ServletContext
import kotlin.jvm.Throws

@Service
class FailService(
    @Autowired val servletContext: ServletContext
) {

    @Retryable(value = [Exception::class])
    @Throws(Exception::class)
    fun fail(id: UUID, numberOfFailuresBeforeSuccess: Number) {
        LOGGER.info("Handle: $id for $numberOfFailuresBeforeSuccess")

        when (shouldMessageFail(numberOfFailuresBeforeSuccess, id)) {
            true -> throw Exception("Handle: $id failed.")
            false -> {

            }
        }
    }

    @Recover
    fun recover(exception: java.lang.Exception, id: UUID, numOfFailures: Number) {
       LOGGER.error("Failed to retry $id after $numOfFailures attempts. Exception Message: ${exception.message}")
    }

    private fun shouldMessageFail(numberOfFailuresBeforeSuccess: Number, id: UUID): Boolean {
        return when (numberOfFailuresBeforeSuccess) {
            -1 -> {
                LOGGER.debug("Handle: $id should always throw")
                true
            }
            0 -> {
                LOGGER.debug("Handle: $id should not throw an exception")
                false
            }

            else -> {
                val key = id.toString()
                when (val num = servletContext.getAttribute(key) as Number?) {
                    null -> {
                        LOGGER.debug("Handle: $id does not exist in map")
                        servletContext.setAttribute(id.toString(), numberOfFailuresBeforeSuccess.toInt() -1)
                        true
                    }
                    else -> {
                        LOGGER.debug("Handle: $id has $numberOfFailuresBeforeSuccess failures remaining")
                        servletContext.setAttribute(id.toString(), num.toInt() -1)
                        num.toInt() -1 > 0
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        val LOGGER: Logger = LoggerFactory.getLogger(javaClass)
    }
}