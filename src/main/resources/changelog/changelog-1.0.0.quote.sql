CREATE TABLE `quote` (
  `id` varchar(36) COLLATE utf8mb4_bin NOT NULL,
  `symbol_id` varchar(36) COLLATE utf8mb4_bin NOT NULL,
  `latest_price` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `current_trading_day_open` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `current_trading_day_high` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `current_trading_day_low` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `previous_trading_day_close` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `price_change` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `price_change_percentage` float(7,4) COLLATE utf8mb4_bin DEFAULT NULL,
  `volume` integer COLLATE utf8mb4_bin DEFAULT 0,
  `create_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_market_date_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Quote_Symbol` (`symbol_id`),
  CONSTRAINT `FK_Quote_Symbol` FOREIGN KEY (`symbol_id`) REFERENCES `symbol` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
