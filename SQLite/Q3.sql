create table movies (id integer primary key, name text, year integer, genre text, score integer);
.separator ","
.import Q3_movies.csv movies
create table actors (id integer primary key, name text);
.separator ","
.import Q3_actors.csv actors
create table cast (movie_id integer, actor_id integer, character_name text,primary key(movie_id, actor_id));
.separator ","
.import Q3_cast.csv cast
select '';
create index movies_name_index on movies(name);
create index movies_score_index on movies(score);
select ''; 
select genre, avg(score) from movies where score > 0 group by genre;
select ''; 
select id,name,year,score from movies where year between 2011 and 2014 order by score desc,name asc limit 10;
select ''; 
select actor_id, name, count(movie_id) from 'cast' c 
inner join actors on c.actor_id = actors.id 
group by actor_id having count(movie_id) >= 10
order by name;
select ''; 
select t2.actor_id,actors.name,t2.average 
from 
(select t1.actor_id as actor_id, (sum(t1.score)*1.0)/count(t1.movie_id) as average 
from 
(select c.movie_id as movie_id,c.actor_id as actor_id,movies.score as score from cast c inner join movies on c.movie_id = movies.id) t1 
where score != 0 group by t1.actor_id having  count(t1.movie_id) >= 3) t2 
inner join actors on t2.actor_id = actors.id 
order by t2.average desc limit 10;
select '';
create view good_collaboration as select t2.actor_id1 as actor_id1,t2.actor_id2 as actor_id2, avg(t2.score) as avg_movie_score, count(t2.movie_id) as count_movies 
from (select t1.actor_id1 as actor_id1,t1.actor_id2 as actor_id2,t1.movie_id as movie_id,movies.score as score 
from (select x.actor_id as actor_id1,y.actor_id as actor_id2,x.movie_id as movie_id from cast x inner join cast y on x.movie_id = y.movie_id and x.actor_id != y.actor_id) t1 
inner join movies on t1.movie_id = movies.id)t2 
group by t2.actor_id1,t2.actor_id2 having count(t2.movie_id) > 1 and avg(t2.score) >= 75;
select '';
select actor_id1, actors.name, avg(avg_movie_score) from good_collaboration inner join actors on actor_id1 = actors.id group by actor_id1 order by avg(avg_movie_score) desc limit 5;
select '';