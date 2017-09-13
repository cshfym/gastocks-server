package com.gastocks.server.services.technical

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.simulation.MACDRequestParameters
import com.gastocks.server.models.technical.MACDTechnicalData
import com.gastocks.server.models.technical.TechnicalDataWrapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class MACDService {

    @Autowired
    TechnicalToolsService technicalToolsService

    private static final MACD_SIGNAL_DAYS = 9

    /**
     * Builds out the base data required for calculating the MACD signal data.
     * @param technicalDataList
     * @param quoteData
     * @param emaShortDays
     * @param emaLongDays
     */
    void buildMACDTechnicalData(List<TechnicalDataWrapper> technicalDataList, List<PersistableQuote> quoteData, MACDRequestParameters parameters) {

        quoteData.eachWithIndex { quote, ix ->

            def technicalWrapper = technicalDataList.find { it.quoteDate == quote.quoteDate }

            if (ix == 0) {
                technicalWrapper.macdTechnicalData = new MACDTechnicalData(emaShort: quote.price, emaLong: quote.price)
            } else {
                double emaShort = technicalToolsService.calculateEMA(quote.price, technicalDataList.get(ix - 1).macdTechnicalData.emaShort, parameters.macdShortPeriod)
                double emaLong = technicalToolsService.calculateEMA(quote.price, technicalDataList.get(ix - 1).macdTechnicalData.emaLong, parameters.macdLongPeriod)
                double macd = (emaShort - emaLong).round(4)
                technicalWrapper.macdTechnicalData = new MACDTechnicalData(
                    emaShort: emaShort,
                    emaLong: emaLong,
                    macd: macd)
            }
        }
    }

    /**
     * The MACD signal line is the 9-day EMA of the MACD.
     * @param technicalDataList
     */
    void buildMACDSignalData(List<TechnicalDataWrapper> technicalDataList) {

        technicalDataList.eachWithIndex { technicalData, ix ->

            MACDTechnicalData macdTechnicalData = technicalDataList[ix].macdTechnicalData
            MACDTechnicalData macdTechnicalDataYesterday = technicalDataList[ix - 1].macdTechnicalData

            if (ix == 0) {
                macdTechnicalData.macdSignalLine = macdTechnicalData.macd
                macdTechnicalData.macdHist = 0.0d
            } else {

                macdTechnicalData.macdSignalLine = technicalToolsService.calculateEMA(macdTechnicalData.macd, macdTechnicalDataYesterday.macdSignalLine, MACD_SIGNAL_DAYS)
                macdTechnicalData.macdHist = (macdTechnicalData.macd - macdTechnicalData.macdSignalLine).round(4)

                // Positive MACD center line crossover
                if ((macdTechnicalData.macd >= 0.0) && (macdTechnicalDataYesterday.macd < 0.0 )) {
                    macdTechnicalData.centerCrossoverPositive = true
                    macdTechnicalData.centerCrossoverNegative = false
                }
                // Negative MACD center line crossover
                if ((macdTechnicalData.macd < 0.0) && (macdTechnicalDataYesterday.macd >= 0.0 )) {
                    macdTechnicalData.centerCrossoverNegative = true
                    macdTechnicalData.centerCrossoverPositive = false
                }
                // Positive MACD signal line crossover
                if ((macdTechnicalData.macd >= macdTechnicalData.macdSignalLine) &&
                        (macdTechnicalDataYesterday.macd < macdTechnicalDataYesterday.macdSignalLine)) {
                    macdTechnicalData.signalCrossoverPositive = true
                    macdTechnicalData.signalCrossoverNegative = false
                }
                // Positive MACD signal line crossover
                if ((macdTechnicalData.macd < macdTechnicalData.macdSignalLine) &&
                        (macdTechnicalDataYesterday.macd >= macdTechnicalDataYesterday.macdSignalLine)) {
                    macdTechnicalData.signalCrossoverNegative = true
                    macdTechnicalData.signalCrossoverPositive = false
                }
            }
        }
    }

}
