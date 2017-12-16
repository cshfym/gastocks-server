# gastocks-server

java -jar build/libs/gastocks-server-1.0.0-SNAPSHOT.war

Ideas:
1. Calculate the price range / trading range for a symbol & quotes for various time periods.
    - Assign a volatility / risk score for the symbol based on standard deviation??


Technical Indicators:
1. MACD
    - Add another setting to delay buy/sell until after X days after crossover?
    - Add another setting to look at "velocity" of the crossover, i.e., how rapidly it's moving up or down?
    - Prevent triggering if the price is not changing. [DONE]

2. Relative Strength Index (RSI)
http://www.investopedia.com/articles/active-trading/042114/overbought-or-oversold-use-relative-strength-index-find-out.asp
https://finance.yahoo.com/news/overbought-vs-oversold-means-traders-030200320.html

3. Stochastic Oscillator

4. On-Balance Volume - uses volume to predict changes in stock price.

5. Accumulation/Distribution - tracks relationship between volume and price.

6. Price Range or trading range.

7. Evaluate each ticker:
    - Analyze every significant price change (up, down)
    - What statistical event preceeded to the price change?

https://en.wikipedia.org/wiki/68%E2%80%9395%E2%80%9399.7_rule



