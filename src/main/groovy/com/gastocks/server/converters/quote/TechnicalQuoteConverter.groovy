package com.gastocks.server.converters.quote

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.technical.response.EMVTechnicalData
import com.gastocks.server.models.technical.response.MACDTechnicalData
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.response.OBVTechnicalData
import com.gastocks.server.models.technical.response.RSITechnicalData
import com.gastocks.server.models.technical.response.TechnicalQuote
import com.gastocks.server.models.technical.response.TechnicalQuoteMetadata
import com.gastocks.server.models.technical.response.TechnicalQuoteParameters
import org.springframework.stereotype.Component

@Component
class TechnicalQuoteConverter {

    static TechnicalQuote fromPersistableQuote(PersistableQuote persistableQuote, TechnicalDataWrapper technicalData) {

        new TechnicalQuote(
            quoteDate: persistableQuote.quoteDate,
            symbol: persistableQuote.symbol.identifier,
            exchangeName: persistableQuote.symbol.exchangeMarket.shortName,
            price: persistableQuote.price,
            open: persistableQuote.dayOpen,
            high: persistableQuote.dayHigh,
            low: persistableQuote.dayLow,
            volume: persistableQuote.volume,

            // Parameters
            quoteParameters: new TechnicalQuoteParameters(
                    priceChangeFromLastQuote: technicalData.quoteParameters.priceChangeFromLastQuote
            ),

            // MACD Data
            macdParameters: new MACDTechnicalData(
                emaShort: technicalData.macdTechnicalData.emaShort,
                emaLong: technicalData.macdTechnicalData.emaLong,
                macd: technicalData.macdTechnicalData.macd,
                macdSignalLine: technicalData.macdTechnicalData.macdSignalLine,
                macdHist: technicalData.macdTechnicalData.macdHist,
                aboveSignalLine: technicalData.macdTechnicalData.aboveSignalLine,
                belowSignalLine: technicalData.macdTechnicalData.belowSignalLine,
                centerCrossoverNegative: technicalData.macdTechnicalData.centerCrossoverNegative,
                centerCrossoverPositive: technicalData.macdTechnicalData.centerCrossoverPositive,
                signalCrossoverNegative: technicalData.macdTechnicalData.signalCrossoverNegative,
                signalCrossoverPositive: technicalData.macdTechnicalData.signalCrossoverPositive,
                periodsAboveSignalLine: technicalData.macdTechnicalData.periodsAboveSignalLine,
                periodsBelowSignalLine: technicalData.macdTechnicalData.periodsBelowSignalLine
            ),

            //RSI Data
            rsiParameters: new RSITechnicalData(
                interval: technicalData.rsiTechnicalData.interval,
                overBoughtLine: technicalData.rsiTechnicalData.overBoughtLine,
                overSoldLine: technicalData.rsiTechnicalData.overSoldLine,
                priceGain: technicalData.rsiTechnicalData.priceGain,
                priceLoss: technicalData.rsiTechnicalData.priceLoss,
                averagePriceGain: technicalData.rsiTechnicalData.averagePriceGain,
                averagePriceLoss: technicalData.rsiTechnicalData.averagePriceLoss,
                relativeStrength: technicalData.rsiTechnicalData.relativeStrength,
                relativeStrengthIndex: technicalData.rsiTechnicalData.relativeStrengthIndex,
                overBought: technicalData.rsiTechnicalData.overBought,
                overSold: technicalData.rsiTechnicalData.overSold,
                periodsOverBought: technicalData.rsiTechnicalData.periodsOverBought,
                periodsOverSold: technicalData.rsiTechnicalData.periodsOverSold,
                overBoughtCrossoverPositive: technicalData.rsiTechnicalData.overBoughtCrossoverPositive,
                overBoughtCrossoverNegative: technicalData.rsiTechnicalData.overBoughtCrossoverNegative,
                overSoldCrossoverPositive: technicalData.rsiTechnicalData.overSoldCrossoverPositive,
                overSoldCrossoverNegative: technicalData.rsiTechnicalData.overSoldCrossoverNegative,
                centerLineCrossoverNegative: technicalData.rsiTechnicalData.centerLineCrossoverNegative,
                centerLineCrossoverPositive: technicalData.rsiTechnicalData.centerLineCrossoverPositive
            ),

            // On-Balance Volume Data
            onBalanceVolumeData: new OBVTechnicalData(
                onBalanceVolume: technicalData.onBalanceVolumeData.onBalanceVolume,
                onBalanceVolumeShort: technicalData.onBalanceVolumeData.onBalanceVolumeShort,
                onBalanceVolumeLong: technicalData.onBalanceVolumeData.onBalanceVolumeLong
            ),

            // Ease of Movement Data
            emvTechnicalData: new EMVTechnicalData(
                periods: technicalData.emvTechnicalData.periods,
                volume: technicalData.emvTechnicalData.volume,
                boxRatio: technicalData.emvTechnicalData.boxRatio,
                easeOfMovement: technicalData.emvTechnicalData.easeOfMovement,
                easeOfMovementSMA: technicalData.emvTechnicalData.easeOfMovementSMA,
                smaCrossoverPositive: technicalData.emvTechnicalData.smaCrossoverPositive,
                smaCrossoverNegative: technicalData.emvTechnicalData.smaCrossoverNegative,
                easyMovement: technicalData.emvTechnicalData.easyMovement,
                difficultMovement: technicalData.emvTechnicalData.difficultMovement,
                volumeTriggerPercentage: technicalData.emvTechnicalData.volumeTriggerPercentage,
                highLowTriggerPercentage: technicalData.emvTechnicalData.highLowTriggerPercentage
            ),

            // Averages
            quoteMetadata: new TechnicalQuoteMetadata(
                _52WeekAverage: technicalData.quoteMetadata._52WeekAverage,
                _26WeekAverage: technicalData.quoteMetadata._26WeekAverage,
                _12WeekAverage: technicalData.quoteMetadata._12WeekAverage,
                _6WeekAverage: technicalData.quoteMetadata._6WeekAverage,
                _3WeekAverage: technicalData.quoteMetadata._3WeekAverage,
                _1WeekAverage: technicalData.quoteMetadata._1WeekAverage,

                // High/Low
                _52WeekHigh: technicalData.quoteMetadata._52WeekHigh,
                _52WeekLow: technicalData.quoteMetadata._52WeekLow,
                _26WeekHigh: technicalData.quoteMetadata._26WeekHigh,
                _26WeekLow: technicalData.quoteMetadata._26WeekLow,
                _12WeekHigh: technicalData.quoteMetadata._12WeekHigh,
                _12WeekLow: technicalData.quoteMetadata._12WeekLow,
                _6WeekHigh: technicalData.quoteMetadata._6WeekHigh,
                _6WeekLow: technicalData.quoteMetadata._6WeekLow,
                _3WeekHigh: technicalData.quoteMetadata._3WeekHigh,
                _3WeekLow: technicalData.quoteMetadata._3WeekLow,
                _1WeekHigh: technicalData.quoteMetadata._1WeekHigh,
                _1WeekLow: technicalData.quoteMetadata._1WeekLow
            )
        )
    }
}
