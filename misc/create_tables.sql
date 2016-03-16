DROP TABLE account, childaccount, effect, item, order_, orderline, rating, photoconfiguration, photo, school, userright CASCADE;

CREATE TABLE school (
  id       INTEGER PRIMARY KEY,
  name     TEXT NOT NULL,
  location TEXT,
  country  TEXT
);

CREATE TABLE account (
  id            UUID PRIMARY KEY,
  oauthkey      TEXT UNIQUE,
  oauthprovider TEXT,
  name          TEXT        NOT NULL,
  email         TEXT UNIQUE NOT NULL,
  hash          TEXT,
  active        BOOLEAN,
  type          TEXT
);

CREATE TABLE childaccount (
  id                UUID PRIMARY KEY,
  identifiernumber INTEGER NOT NULL,
  uniquecode        TEXT    NOT NULL UNIQUE,
  parentid          UUID REFERENCES account (id)
);


CREATE TABLE photo (
  id             UUID PRIMARY KEY,
  price          INTEGER NOT NULL,
  capturedate    INTEGER NOT NULL,
  pathtophoto    TEXT    NOT NULL,
  photographerid UUID REFERENCES account (id),
  childid        UUID REFERENCES childaccount (id),
  schoolid       INTEGER REFERENCES school (id)
);

CREATE TABLE rating (
  id        INTEGER PRIMARY KEY,
  points    INTEGER NOT NULL,
  accountid UUID REFERENCES account (id),
  photoid   UUID REFERENCES photo (id)
);

CREATE TABLE item (
  id            INTEGER PRIMARY KEY,
  price         INTEGER NOT NULL,
  type          TEXT    NOT NULL,
  description   TEXT,
  thumbnailpath TEXT
);

CREATE TABLE effect (
  id          INTEGER PRIMARY KEY,
  type        TEXT    NOT NULL,
  description TEXT    NOT NULL,
  price       INTEGER NOT NULL
);

CREATE TABLE userright (
  createrights    BOOLEAN,
  updaterights    BOOLEAN,
  destroyrights   BOOLEAN,
  accountidrights UUID REFERENCES account (id)
);

CREATE TABLE photoconfiguration (
  id       INTEGER PRIMARY KEY,
  effectid INTEGER REFERENCES effect (id),
  itemid   INTEGER REFERENCES item (id),
  photoid  UUID REFERENCES photo (id)
);

CREATE TABLE order_ (
  id                   INTEGER PRIMARY KEY,
  orderdate            TIMESTAMP NOT NULL,
  accountid            UUID REFERENCES account (id),
  photoconfigurationid INTEGER REFERENCES photoconfiguration (id)
);

CREATE TABLE orderline (
  id                   INTEGER PRIMARY KEY,
  orderid              INTEGER REFERENCES order_ (id),
  photoconfigurationid INTEGER REFERENCES photoconfiguration (id)
);

-- DUMMY DATA
INSERT INTO account (id, name, email, active)
VALUES ('602a4264-cf81-4ad3-aa6e-13cf8578320f', 'Norma Jones', 'njones0@amazonaws.com', TRUE);
INSERT INTO account (id, name, email, active)
VALUES ('2dae97cc-b0a3-4123-90ee-359e0528d371', 'Martin Perkins', 'mperkins1@wikipedia.org', TRUE);
INSERT INTO account (id, name, email, active)
VALUES ('41edb0b8-1f1a-4d9f-993d-f151c2b2ae4b', 'Raymond Woods', 'rwoods2@sitemeter.com', TRUE);
INSERT INTO account (id, name, email, active)
VALUES ('8574966d-fb6a-4bdf-bd59-2de3c1edcc19', 'Sean Hernandez', 'shernandez3@yellowbook.com', FALSE);
INSERT INTO account (id, name, email, active)
VALUES ('e208462f-411f-4222-b957-0bddabe0fb99', 'Joan Rogers', 'jrogers4@shinystat.com', TRUE);
INSERT INTO account (id, name, email, active)
VALUES ('11cbd1da-5358-4789-95e5-891fe72c34e6', 'Daniel Austin', 'daustin5@howstuffworks.com', FALSE);
INSERT INTO account (id, name, email, active)
VALUES ('b793c950-55b3-404d-9018-50e3f2c5f37c', 'Donald Montgomery', 'dmontgomery6@newyorker.com', TRUE);
INSERT INTO account (id, name, email, active)
VALUES ('371c7b83-bdfb-4845-814f-98bc6fdd2445', 'Eric Black', 'eblack7@cisco.com', TRUE);
INSERT INTO account (id, name, email, active)
VALUES ('1b2bb37b-5666-4e4b-9d95-83ee41520423', 'Shirley Sanchez', 'ssanchez8@vinaora.com', FALSE);
INSERT INTO account (id, name, email, active, type)
VALUES
  ('c525a7c3-717f-4e6e-8e4d-9c31e09f03ad', 'Kathryn Marshall', 'kmarshall9@barnesandnoble.com', TRUE, 'photographer');

INSERT INTO childaccount (id,parentid, identifiernumber, uniquecode)
VALUES ('48e7b3ae-c2bc-46b2-a845-1a0d9ad156b4', '602a4264-cf81-4ad3-aa6e-13cf8578320f', '3541437', '3541437563728116');
INSERT INTO childaccount (id,parentid, identifiernumber, uniquecode)
VALUES ('9e7b523f-bbf1-4050-aea3-07ba87473568', '602a4264-cf81-4ad3-aa6e-13cf8578320f', '374288', '374288485129711');
INSERT INTO childaccount (id,parentid, identifiernumber, uniquecode)
VALUES ('1dd56a99-ac84-4f56-b091-ec07bdbc4ad1', '602a4264-cf81-4ad3-aa6e-13cf8578320f', '30259182', '30259188914242');
