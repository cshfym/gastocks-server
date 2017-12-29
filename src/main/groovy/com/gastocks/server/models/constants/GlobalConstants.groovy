package com.gastocks.server.models.constants

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.text.DateFormat
import java.text.SimpleDateFormat

class GlobalConstants {

    static final String DB_DATE_FORMAT_STRING = "MMM dd, yyyy"

    static final String SHORT_DATE_FORMAT_STRING = "yyyy-MM-dd"

    static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat(SHORT_DATE_FORMAT_STRING)

    static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormat.forPattern(SHORT_DATE_FORMAT_STRING)

}
