--- LMD (langage de manipulation de données)

--- Contenu des tables (tests)

--- Table joueurs

INSERT INTO joueurs(nom, parties, victoires) VALUES ('FAURIE Christophe', 0, 0);
INSERT INTO joueurs(nom, parties, victoires) VALUES ('COLDRICK Paul', 0, 0);
INSERT INTO joueurs(nom, parties, victoires) VALUES ('LELONG Kevin', 0, 0);
INSERT INTO joueurs(nom, parties, victoires) VALUES ('LAMBERT Christophe', 0, 0);
INSERT INTO joueurs(nom, parties, victoires) VALUES ('JANNIN Jean-Baptiste', 0, 0);
INSERT INTO joueurs(nom, parties, victoires) VALUES ('RAMIER Sébastien', 0, 0);
INSERT INTO joueurs(nom, parties, victoires) VALUES ('TRICHET Clément', 0, 0);
INSERT INTO joueurs(nom, parties, victoires) VALUES ('GAUME Benjamin', 0, 0);

--- Table manches

INSERT INTO manches(horodatage, gagnantId, perdantId, numeroTable) VALUES ('2023-05-05 08:00:00',1,2,1);

--- Table tours

CREATE TABLE IF NOT EXISTS tours (id INTEGER PRIMARY KEY AUTOINCREMENT, joueurId INTEGER, mancheId INTEGER, FOREIGN KEY (joueurId) REFERENCES joueurs(id) ON DELETE CASCADE, FOREIGN KEY (mancheId) REFERENCES manches(id) ON DELETE CASCADE);

INSERT INTO tours(joueurId, mancheId) VALUES (1,1);
INSERT INTO tours(joueurId, mancheId) VALUES (2,1);

--- Table empoches

INSERT INTO empoches(tourId, poche, couleur) VALUES (1,1,2);
INSERT INTO empoches(tourId, poche, couleur) VALUES (2,2,3);

