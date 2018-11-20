CREATE TABLE examinations_by_month (
	examination_id			IDENTITY,
	incoming_examinations	INTEGER DEFAULT 0,
	examination_year		INTEGER,
	examination_month		INTEGER
);
