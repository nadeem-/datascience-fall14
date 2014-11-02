with temp1 as (
	select country_id, count(medal) as num_medals
	from
	(results INNER JOIN players
	on results.player_id = 
	players.player_id)
	group by country_id
	)
select name, num_medals from temp1 LEFT JOIN countries ;


with temp1 as (
	select country_id, count(medal) as num_medals
	from
	(results INNER JOIN players
	on results.player_id = 
	players.player_id)
	group by country_id)
select name, num_medals 
from temp1 LEFT JOIN countries
on temp1.country_id=countries.country_id
order by name asc
;

create view NumberOfMedals as
	with temp1 as (
		select country_id, count(medal) as num_medals
		from
		(results INNER JOIN players
		on results.player_id = 
		players.player_id)
		group by country_id)
	select name, num_medals 
	from temp1 LEFT JOIN countries
	on temp1.country_id=countries.country_id
	order by name asc
	;


SELECT name, party
      ,COALESCE(party,'None') AS aff
  FROM msp WHERE name LIKE 'C%'


# query for 2a.)
SELECT R.a, R.b, R.c, S.d
FROM R INNER JOIN S
ON (R.c = S.c or R.c is NULL and S.c is NULL)
;

SELECT R.a, R.b, R.c, S.d
FROM R INNER JOIN S
ON (R.c = S.c or R.c is NULL and S.c is NULL)
;

SELECT R.a, R.b, R.c, S.d
FROM R LEFT JOIN S
ON (R.c = S.c or R.c is NULL and R.c is NULL)
;

SELECT R.a, R.b, R.c, S.d
FROM R LEFT JOIN S
ON (R.c = S.c or R.c is NULL and S.c is NULL)
;

#query for 2b.)
SELECT R.a, R.b, R.c, S.d
FROM R INNER JOIN S
ON (R.c = S.c or R.c is NULL and R.c is NULL) 
or (R.a is NULL) or (R.b is NULL) or (S.d is NULL)
;

     a      | b  | c  |     d      
------------+----+----+------------
 a3         | 30 | 30 | d1        
 a4         |  0 |    | d2        

SELECT R.a, R.b, R.c, S.d
FROM R INNER JOIN S
ON (R.c = S.c or (R.c is NULL and S.c is NULL) or (R.a is NULL and S.a is NULL)) 
;


SELECT R.a, R.b, R.c, S.d
FROM R INNER JOIN S
ON (R.c = S.c or (R.c is NULL and S.c is NULL) or (R.b is NULL and S.c is NULL) 
	or (R.a is NULL and S.c is NULL) or (S.c is NULL));

(a.[LineA] = n.[LineA] OR (a.[LineA] is null AND n.[LineA] is null))
AND (a.[LineB] = n.[LineB] OR (a.[LineB] is null AND n.[LineB] is null))
AND (a.[LineC] = n.[LineC] OR (a.[LineC] is null AND n.[LineC] is null)


SELECT R.a, R.b, R.c, S.d
FROM R INNER JOIN S
ON (R.c = S.c or R.c is NULL or S.c is NULL or S.d is NULL or R.a is NULL or R.b is NULL)
;



SELECT R.a, R.b, R.c, S.d
FROM R INNER JOIN S
ON (R.c = S.c or (R.c is NULL and S.c is NULL) or (R.b is NULL and S.c is NULL) 
	or (R.a is NULL and S.c is NULL) or (R.a is NULL and R.b is NULL));

# WORKING SOMEWHAT >>>>>>
SELECT R.a, R.b, R.c, S.d
FROM R LEFT JOIN S
ON (R.c = S.c or (R.a is NULL and S.c is NULL));

SELECT R.a, R.b, R.c, S.d
FROM R FULL OUTER JOIN S
ON (R.c = S.c or (R.a is NULL and S.c is NULL));
     a      | b  | c  |     d      
------------+----+----+------------
 a1         | 15 | 15 | 
 a2         | 20 | 20 | 
 a3         | 30 | 30 | d1        
 a4         |  0 |    | 
            |    |    | d2    

SELECT R.a, R.b, R.c, S.d
FROM R LEFT JOIN S
ON (R.c = S.c or (R.a is NULL and S.c is NULL) or ((R.a is NULL and R.b is NULL) and S.c is NULL));


     a      | b  | c  |     d      
------------+----+----+------------
 a1         | 15 | 15 | 
 a2         | 20 | 20 | 
 a3         | 30 | 30 | d1        
 a4         |  0 |    | d2        

(4 rows)



WANT:  
(a4, 0, NULL, NULL)
 a4			| 0  |    | 

 			| 	 |    | d2


     a      | b  | c  |     d      
------------+----+----+------------
 a3         | 30 | 30 | d1        
 a4         |  0 |    | d1        
 a1         | 15 | 15 | d2        
 a2         | 20 | 20 | d2        
 a3         | 30 | 30 | d2        
 a4         |  0 |    | d2        
(6 rows)

SELECT R.a, R.b, R.c, S.d
FROM R LEFT OUTER JOIN S
ON (R.c = S.c and (R.c is NULL and S.c is NULL))


insert into results VALUES ("E81", "EGBELAAR01", "GOLD", 15.14)

   			INSERT INTO teammedals (country_id, event_id, medal, result) VALUES (temp1.country_id, temp1.event_id, temp1.medal, temp1.result);


//// QUESTION 3 /////
CREATE OR REPLACE FUNCTION update_team_table() RETURNS TRIGGER as $team_table$
    BEGIN
        IF (SELECT COUNT(*) FROM events e WHERE e.event_id = NEW.event_id and e.is_team_event = 1) > 0 THEN 

        	CREATE TEMP TABLE temp1 AS
        		SELECT e.event_id, medal, result, country_id
				FROM results r, events e, players p
				WHERE e.event_id = NEW.event_id 
				AND r.player_id = NEW.player_id;
			INSERT INTO teammedals (country_id, event_id, medal, result) FROM temp1;

            RAISE NOTICE 'GOOD';
        ELSE
            RAISE NOTICE 'BAD';
        END IF;
    END;
$team_table$ LANGUAGE plpgsql;



CREATE TRIGGER check_update
	AFTER INSERT ON results
		FOR EACH ROW EXECUTE PROCEDURE update_team_table();


	select e.event_id, medal, result, country_id
	from results r, events e, players p
	where r.event_id = e.event_id and r.player_id = p.player_id and is_team_event = 1

	DECLARE @C text
	select @C=country_id
	from players p
	where NEW.player_id = p.player_id;




CREATE OR REPLACE FUNCTION update_team_table() RETURNS TRIGGER as $team_table$
    BEGIN
        IF (SELECT COUNT(*) FROM events e WHERE e.event_id = NEW.event_id and e.is_team_event = 1) > 0 THEN 

			INSERT INTO teammedals
        		SELECT p.country_id, e.event_id, medal, result
				FROM results r, events e, players p
				WHERE e.event_id = NEW.event_id 
				AND r.player_id = NEW.player_id AND p.player_id = NEW.player_id;

            RAISE NOTICE 'GOOD';
            return NEW;
        ELSE
            RAISE NOTICE 'BAD';
        END IF;
    END;
$team_table$ LANGUAGE plpgsql;




/* Problem 4 */

select player_id, event_id, medal from results r where r.medal = 'GOLD' and event_id IN (select event_id from events where olympic_id = 'ATH2004');




CREATE OR REPLACE FUNCTION generate_xml() RETURNS integer AS $$
DECLARE
    rec RECORD;
BEGIN
    RAISE NOTICE 'Generating xml...';

    FOR rec IN select player_id, event_id, medal from results r where r.medal = 'GOLD' and event_id IN (select event_id from events where olympic_id = 'ATH2004') LOOP

        format('Hello %s, %1$s', 'World')
    END LOOP;

    RAISE NOTICE 'Done with loop.';
    RETURN 1;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION generate_xml() RETURNS text AS $$
DECLARE
    rec RECORD;
DECLARE
	pla RECORD;
DECLARE
	xml varchar := '';
BEGIN
    FOR rec IN select DISTINCT r.event_id, medal, is_team_event, e.name from results r, events e where r.medal = 'GOLD' 
    and r.event_id IN (select event_id from events where olympic_id = 'ATH2004') 
    and r.event_id = e.event_id
    LOOP
    	xml := xml || '<medal>' || E'\n';
    	xml := xml || E'\t<event>' ||  rec.name || '/<event>' || E'\n';

    	IF rec.is_team_event = 1
    	THEN
	    	xml := xml || E'\t<players>\n';
	    	FOR pla IN select r.player_id, p.name from results r, players p 
	    	where r.medal = rec.medal and r.event_id = rec.event_id  and p.player_id = r.player_id
	    	LOOP
	    	      	xml = xml || E'\t\t<player>' || pla.name || '</player>' || E'\n';
	    	END LOOP;
    		xml := xml || E'\t</players>' || E'\n';
    	ELSE
    		FOR pla IN select r.player_id, p.name from results r, players p
	    	where r.medal = rec.medal and r.event_id = rec.event_id  and p.player_id = r.player_id
    		LOOP
	    		xml = xml || E'\t<player>' || pla.name || '</player>' || E'\n';
	    	END LOOP;
	   	END IF;
    	xml := xml || '</medal>' || E'\n';
    END LOOP;
    RETURN xml;
END;
$$ LANGUAGE plpgsql;


with temp1 as (
select event_id, medal from results r where r.medal = 'GOLD' and event_id IN (select event_id from events where olympic_id = 'ATH2004')
)
select player_id from results r, temp1 where r.medal = temp1.medal and r.event_id = temp1.event_id;

select player_id, r.event_id, medal from results r where r.medal = 'GOLD' 
and r.event_id IN (select event_id from events where olympic_id = 'ATH2004')
group by r.event_id, player_id;



CREATE OR REPLACE FUNCTION generate_xml() RETURNS text AS $$
DECLARE
    rec RECORD;
DECLARE
	pla RECORD;
DECLARE
	xml varchar := '';
BEGIN
    FOR rec IN select coalesce(event_id,'') as event, coalesce(medal,'') as 
    med from results r where r.medal = 'GOLD' and event_id 
    IN (select event_id from events where olympic_id = 'ATH2004') LOOP
    	xml := xml || '<medal>' || E'\r\n';
    END LOOP;
    RETURN xml;
END;
$$ LANGUAGE plpgsql IMMUTABLE;


CREATE OR REPLACE FUNCTION generate_xml() RETURNS text AS $$
DECLARE
    rec RECORD;
DECLARE
	pla RECORD;
DECLARE
	xml text := '';
BEGIN
    FOR rec IN select event_id, medal from results r where r.medal = 'GOLD' and event_id IN (select event_id from events where olympic_id = 'ATH2004') LOOP
    	RAISE NOTICE 'iter';
    END LOOP;
    RETURN xml;
END;
$$ LANGUAGE plpgsql IMMUTABLE;
