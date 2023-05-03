create database musicdb;

use musicdb;

CREATE TABLE music_table (
  notes VARCHAR(255) NOT NULL,
  chords VARCHAR(255) NOT NULL,
  frequency INT,
  UNIQUE KEY unique_notes_chords (notes, chords)
);

select * from music_table;