package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.models.SymbolMinMaxObject
import org.hibernate.annotations.NamedNativeQuery
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

import javax.persistence.ColumnResult
import javax.persistence.ConstructorResult
import javax.persistence.SqlResultSetMapping

interface QuoteRepository extends CrudRepository<PersistableQuote, String> {

    PersistableQuote findBySymbolAndQuoteDate(PersistableSymbol symbol, Date date)

    List<PersistableQuote> findAllBySymbol(PersistableSymbol symbol)

    @Query(value="SELECT MAX(q.price) FROM quote q WHERE q.symbol = :symbol AND q.quoteDate >= :oldestDate")
    Double find52WeekMaximumForSymbolAndDate(@Param("symbol") PersistableSymbol symbol, @Param("oldestDate") Date oldestDate)

    @Query(value="SELECT MIN(q.price) FROM quote q WHERE q.symbol = :symbol AND q.quoteDate >= :oldestDate")
    Double find52WeekMinimumForSymbolAndDate(@Param("symbol") PersistableSymbol symbol, @Param("oldestDate") Date oldestDate)

    @Query(value="SELECT AVG(q.price) FROM quote q WHERE q.symbol = :symbol AND q.quoteDate >= :oldestDate")
    Double find52WeekAverageForSymbolAndDate(@Param("symbol") PersistableSymbol symbol, @Param("oldestDate") Date oldestDate)

    @SqlResultSetMapping(name = "symbolMinMaxMapping", classes = {
                @ConstructorResult(
                        targetClass = SymbolMinMaxObject.class,
                        columns = {
                            @ColumnResult(name="identifier", type = String.class),
                            @ColumnResult(name="max_price", type = Double.class),
                            @ColumnResult(name="min_price", type = Double.class),
                            @ColumnResult(name="avg_price", type = Double.class),
                        }
                )
            }
    )

    @NamedNativeQuery(name="getSymbolMinMaxObject", query="", resultSetMapping="symbolMinMaxMapping")

}