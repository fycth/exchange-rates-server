-- createdb xrserver
-- CREATE USER xrserver WITH PASSWORD 'xrserver';
-- GRANT ALL PRIVILEGES ON DATABASE "xrserver" to xrserver;

CREATE TABLE IF NOT EXISTS rates (id SERIAL, name varchar, rate double precision, source smallint, time timestamp);
CREATE INDEX source ON rates (source);

INSERT INTO rates (source,name,rate,time) VALUES ('src1','USD',1.0711,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','JPY',131.98,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','BGN',1.9558,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','CZK',27.044,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','DKK',7.4601,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','GBP',0.70840,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','HUF',312.69,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','PLN',4.2432,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','RON',4.4520,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','SEK',9.3102,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','CHF',1.0765,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','NOK',9.2765,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','HRK',7.6000,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','RUB',69.0542,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','TRY',3.1220,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','AUD',1.5189,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','BRL',4.0627,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','CAD',1.4204,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','CNY',6.8121,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','HKD',8.3026,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','IDR',14601.38,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','ILS',4.1989,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','INR',70.9818,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','KRW',1242.06,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','MXN',17.9789,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','MYR',4.6946,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','NZD',1.6360,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','PHP',50.600,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','SGD',1.5234,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','THB',38.485,now());
INSERT INTO rates (source,name,rate,time) VALUES ('src1','ZAR',15.3716,now());

-- 0 - XML
-- 1 - JSON
CREATE TABLE IF NOT EXISTS sources (id SERIAL, name varchar, url varchar, descr varchar, type smallint);
INSERT INTO sources (name,url,descr,type) VALUES ('ECB','https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml','',0);

CREATE TABLE IF NOT EXISTS xml_parsers (id SERIAL,
  source smallint,
  pattern_section varchar,
  pattern_currency varchar,
  pattern_rate varchar,
  attribute_currency varchar default '',
  attribute_rate varchar default ''
);
INSERT INTO xml_parsers (source,pattern_section,pattern_currency,pattern_rate,attribute_currency,attribute_rate) VALUES (1,'gesmes:Envelope/Cube/Cube/Cube','gesmes:Envelope/Cube/Cube/Cube','gesmes:Envelope/Cube/Cube/Cube','currency','rate');
