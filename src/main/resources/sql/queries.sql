-- Query inactive and not a symbol with "-" or "."
select * from symbol where id in (
 (select id from symbol
  where active = 0
 and identifier not like ('%-%')))
and identifier not like ('%.%');

-- Queries for symbols without quotes
select x.* from
  (select s.identifier, s.active, count(q.id) as quote_count
  from symbol s
  left join quote q on q.symbol_id = s.id
  group by (s.id)) as x
where x.quote_count = 0
order by x.identifier;

-- Query all quotes for single symbol
select
 s.identifier, s.description,
 q.price, q.quote_date
from quote q
join symbol s on q.symbol_id = s.id
where s.identifier = 'MYGN'
order by quote_date desc;

-- Query all holiday dates by exchange market
select ex.short_name, hc.holiday_date
from holiday_calendar hc
join exchange_market ex on ex.id = hc.exchange_market_id
order by holiday_date asc, short_name;


