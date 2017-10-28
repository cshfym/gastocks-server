package com.gastocks.server.models.avtimeseriesadjusted

import com.gastocks.server.models.IQuote
import org.joda.time.DateTime

class AVTimeSeriesAdjustedDay implements IQuote {

    public double dayOpen
    public double dayHigh
    public double dayLow
    public double close
    public double adjustedClose
    public int volume
    public double dividend
    public double splitCoefficient
    public DateTime date
}
