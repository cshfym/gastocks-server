-- Query inactive and not a symbol with "-" or "."
select * from symbol where id in (
 (select id from symbol
  where active = 0
 and identifier not like ('%-%')))
and identifier not like ('%.%');

-- Queries for symbols without quotes
select * from
  (select s.identifier as symbol, count(q.id) as quote_count
  from symbol s
  join quote q on q.symbol_id = s.id
  group by (s.id)) as x
where x.quote_count = 0;

