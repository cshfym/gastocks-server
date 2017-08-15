package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableHolidayCalendar
import org.springframework.data.repository.CrudRepository

interface HolidayCalendarRepository extends CrudRepository<PersistableHolidayCalendar, String> {

    List<PersistableHolidayCalendar> findAll()
}