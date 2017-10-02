start transaction;

use `ga_stocks`;

CREATE TABLE `quote` (
  `id` varchar(36) COLLATE utf8mb4_bin NOT NULL,
  `symbol_id` varchar(36) COLLATE utf8mb4_bin NOT NULL,
  `price` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `day_open` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `day_high` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `day_low` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `previous_day_close` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `price_change` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `price_change_percentage` float(7,4) COLLATE utf8mb4_bin DEFAULT NULL,
  `volume` integer COLLATE utf8mb4_bin DEFAULT 0,
  `quote_date` DATE COLLATE utf8mb4_bin DEFAULT NULL,
  `dividend` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  `split_coefficient` double(9,3) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_SymbolId_QuoteDate` (`symbol_id`,`quote_date`),
  KEY `FK_Quote_Symbol` (`symbol_id`),
  KEY `IDX_SymbolID_QuoteDate_Price` (`symbol_id`,`quote_date`,`price`),
  CONSTRAINT `FK_Quote_Symbol` FOREIGN KEY (`symbol_id`) REFERENCES `symbol` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

commit;
