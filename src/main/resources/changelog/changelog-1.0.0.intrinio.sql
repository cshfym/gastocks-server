start transaction;

use `ga_stocks`;

-- cleanup --
DELETE FROM `company_dump`;
DROP TABLE `company_dump`;

CREATE TABLE `company_dump` (
  `id` varchar(36) COLLATE utf8mb4_bin NOT NULL,
  `symbol_id` varchar(36) COLLATE utf8mb4_bin NOT NULL,
  `json_dump` text COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `symbol_id_UNIQUE` (`symbol_id`),
  CONSTRAINT `FK_CompanyDump_Symbol` FOREIGN KEY (`symbol_id`) REFERENCES `symbol` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
