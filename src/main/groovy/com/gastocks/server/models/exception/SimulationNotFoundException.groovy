package com.gastocks.server.models.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Identifier not found.")
class SimulationNotFoundException extends RuntimeException {

    String identifier

}
