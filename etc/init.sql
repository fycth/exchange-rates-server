-- createdb xrserver
-- CREATE USER xrserver WITH PASSWORD 'xrserver';
-- GRANT ALL PRIVILEGES ON DATABASE "xrserver" to xrserver;

CREATE TABLE IF NOT EXISTS rates (id BIGSERIAL, name varchar, rate double precision, source smallint, time timestamp);
CREATE INDEX source ON rates (source);

CREATE TABLE IF NOT EXISTS sources (id SERIAL, name varchar, url varchar, descr varchar, parser_class_name varchar, enabled boolean);
INSERT INTO sources (name,url,descr,parser_class_name,enabled) VALUES ('ECB','https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml','European Central Bank','com.sergiienko.xrserver.parsers.ECBParser', true);
INSERT INTO sources (name,url,descr,parser_class_name,enabled) VALUES ('Fixer','http://api.fixer.io/latest','Free rates from Fixer.io','com.sergiienko.xrserver.parsers.FixerParser', true);

CREATE TABLE IF NOT EXISTS groups (id SERIAL, name varchar, descr varchar, sources integer[], dflt boolean);
INSERT INTO groups (name,descr,sources,dflt) VALUES ('default','default group',array[1],TRUE);
