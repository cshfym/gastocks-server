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

-- List of all company entities
select
 s.identifier,
 c.name,
 sec.description as industry,
 ind.category, ind.sub_category,
 c.employee_count as employees, c.ceo, c.headquarter_state as state
from company c
join symbol s on s.id = c.symbol_id
join industry ind on ind.id = c.industry_id
join sector sec on sec.id = c.sector_id
-- where sec.description = 'Healthcare'
order by ind.category, ind.sub_category
limit 10000;

-- Count of symbols belonging to a particular sector
select count(sec.description) as sector_count, sec.description
from company c
join symbol s on c.symbol_id = s.id
join sector sec on sec.id = c.sector_id
group by sec.description
order by sector_count desc;

-- Count of symbols belonging to a particular industry
select count(i.category) as category_count, i.category
from company c
join symbol s on c.symbol_id = s.id
join industry i on i.id = c.industry_id
group by i.category
order by category_count desc;

-- Count of companies belonging to specific sector and industry (category / sub_category).
select count(c.id) as company_count, sec.description, ind.category, ind.sub_category
from company c
join symbol s on c.symbol_id = s.id
join sector sec on sec.id = c.sector_id
join industry ind on c.industry_id = ind.id
group by sec.description, ind.category, ind.sub_category
order by company_count desc;

-- Count of companies belonging to specific sector and industry (category / sub_category).
select count(c.id) as company_count, sec.description as sector, ind.category as industry_category, ind.sub_category as industry_sub_category
from company c
join symbol s on c.symbol_id = s.id
join sector sec on sec.id = c.sector_id
join industry ind on c.industry_id = ind.id
group by sec.description, ind.category, ind.sub_category
order by sec.description, ind.category, ind.sub_category;
