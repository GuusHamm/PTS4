DROP TABLE user_cookie, account, childaccount, effect, item, order_, orderline, rating, photoconfiguration, photo, school, userright CASCADE;

CREATE TABLE user_cookie (
  id      UUID PRIMARY KEY DEFAULT uuid_generate_v1(),
  account UUID NOT NULL
);

CREATE TABLE school (
  id       SERIAL PRIMARY KEY,
  name     TEXT NOT NULL,
  location TEXT,
  country  TEXT
);

CREATE TABLE account (
  id            UUID PRIMARY KEY DEFAULT uuid_generate_v1(),
  oauthkey      TEXT UNIQUE,
  oauthprovider TEXT,
  name          TEXT        NOT NULL,
  email         TEXT UNIQUE NOT NULL,
  hash          TEXT,
  active        BOOLEAN          DEFAULT TRUE,
  type          TEXT             DEFAULT 'customer'
    CHECK (hash IS NOT NULL OR account.oauthkey IS NOT NULL )
);

CREATE TABLE childaccount (
  id               UUID PRIMARY KEY DEFAULT uuid_generate_v1(),
  identifiernumber INTEGER NOT NULL,
  uniquecode       TEXT    NOT NULL UNIQUE,
  parentid         UUID REFERENCES account (id)
);


CREATE TABLE photo (
  id             UUID PRIMARY KEY DEFAULT uuid_generate_v1(),
  price          INTEGER NOT NULL,
  capturedate    DATE    NOT NULL,
  pathtophoto    TEXT    NOT NULL,
  photographerid UUID REFERENCES account (id),
  childid        UUID REFERENCES childaccount (id),
  schoolid       INTEGER REFERENCES school (id)
);

CREATE TABLE rating (
  id        SERIAL PRIMARY KEY,
  points    INTEGER NOT NULL,
  accountid UUID REFERENCES account (id),
  photoid   UUID REFERENCES photo (id)
);

CREATE TABLE item (
  id            SERIAL PRIMARY KEY,
  price         INTEGER NOT NULL,
  type          TEXT    NOT NULL,
  description   TEXT,
  thumbnailpath TEXT
);

CREATE TABLE effect (
  id          SERIAL PRIMARY KEY,
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
  id       SERIAL PRIMARY KEY,
  effectid INTEGER REFERENCES effect (id),
  itemid   INTEGER REFERENCES item (id),
  photoid  UUID REFERENCES photo (id)
);

CREATE TABLE order_ (
  id        SERIAL PRIMARY KEY,
  orderdate TIMESTAMP NOT NULL,
  accountid UUID REFERENCES account (id)
);

CREATE TABLE orderline (
  id                   SERIAL PRIMARY KEY,
  orderid              INTEGER REFERENCES order_ (id),
  photoconfigurationid INTEGER REFERENCES photoconfiguration (id),
  amount               INTEGER NOT NULL
);

-- ACCOUNT MOCK DATA
INSERT INTO account (id, name, email, active, hash)
VALUES ('602a4264-cf81-4ad3-aa6e-13cf8578320f', 'Norma Jones', 'njones0@amazonaws.com', TRUE,
        '$s0$e0801$UXWHFDS7gqHNjtlnbXG0fg==$MC6JpBSIMH5phCn6ypeWsFw3/e4jZuy5lqOUmjvx2Yc=');
INSERT INTO account (id, name, email, active, hash)
VALUES ('2dae97cc-b0a3-4123-90ee-359e0528d371', 'Martin Perkins', 'mperkins1@wikipedia.org', TRUE,
        '$s0$e0801$ZHp8J5exL2SRKQ25Yux6EQ==$nYbsBiSo0+ihGvtrbXT60VgS+s1Oz6xdxSxNt7EgdH4=');
INSERT INTO account (id, name, email, active, hash)
VALUES ('41edb0b8-1f1a-4d9f-993d-f151c2b2ae4b', 'Raymond Woods', 'rwoods2@sitemeter.com', TRUE,
        '$s0$e0801$hqOter8wmgkA4pEtfd0iJA==$oOBlfhfRckbsSnlmYcXJSteJOMrlcf3YhD5oHSiV8Ks=');
INSERT INTO account (id, name, email, active, hash)
VALUES ('8574966d-fb6a-4bdf-bd59-2de3c1edcc19', 'Sean Hernandez', 'shernandez3@yellowbook.com', FALSE,
        '$s0$e0801$qzqKmkNfg+3ItOzb1F+6Mg==$RmFJUh7+KVo1yVsUkqPg96FUDJNQB16NQGWme9E3RB4=');
INSERT INTO account (id, name, email, active, hash)
VALUES ('e208462f-411f-4222-b957-0bddabe0fb99', 'Joan Rogers', 'jrogers4@shinystat.com', TRUE,
        '$s0$e0801$0mhCj97s1gG8FqbDtTSNqA==$YyXz8FOMQaomX2lBkAu2dsloixR2/t5IJGtMG6+6TAg=');
INSERT INTO account (id, name, email, active, hash)
VALUES ('11cbd1da-5358-4789-95e5-891fe72c34e6', 'Daniel Austin', 'daustin5@howstuffworks.com', FALSE,
        '$s0$e0801$AktkNjS7to7yTtp18ekKkg==$+sx5fw7G0pwZYpH2GdhXIZSda2mdSRbZ0ttPRgiMXPM=');
INSERT INTO account (id, name, email, active, hash)
VALUES ('b793c950-55b3-404d-9018-50e3f2c5f37c', 'Donald Montgomery', 'dmontgomery6@newyorker.com', TRUE,
        '$s0$e0801$vW6f0ZQT+4t3dNRxkh2bMg==$D0QOuCMbW4zDQqLutqgORnNvg+PtaNf267cQB8hNbe8=');
INSERT INTO account (id, name, email, active, hash)
VALUES ('371c7b83-bdfb-4845-814f-98bc6fdd2445', 'Eric Black', 'eblack7@cisco.com', TRUE,
        '$s0$e0801$i/rxogkVjjoaCWfu1VU3fg==$wtldNMnnQ8GGsBuA7GvET3WtXs4NCitE6kfg5BVfCjQ=');
INSERT INTO account (id, name, email, active, hash)
VALUES ('1b2bb37b-5666-4e4b-9d95-83ee41520423', 'Shirley Sanchez', 'ssanchez8@vinaora.com', FALSE,
        '$s0$e0801$5Jw8uceQqEXAPlZuPa1VFA==$N8iGauTvLedMwbFIVAZ0shm5JdBMMqjAHQQgCosoALA=');
INSERT INTO account (id, name, email, active, type, hash)
VALUES
  ('c525a7c3-717f-4e6e-8e4d-9c31e09f03ad', 'Kathryn Marshall', 'kmarshall9@barnesandnoble.com', TRUE, 'photographer',
   '$s0$e0801$GiLRxbgkcOf64oe0J60eww==$kg5gx7gGyoIU9BvO6HD3zzYhhDdugn6p8O/X5TLLrTs=');

-- CHILD ACCOUNT MOCK DATA
INSERT INTO childaccount (id, parentid, identifiernumber, uniquecode)
VALUES ('48e7b3ae-c2bc-46b2-a845-1a0d9ad156b4', '602a4264-cf81-4ad3-aa6e-13cf8578320f', '3541437', '3541437563728116');
INSERT INTO childaccount (id, parentid, identifiernumber, uniquecode)
VALUES ('9e7b523f-bbf1-4050-aea3-07ba87473568', '602a4264-cf81-4ad3-aa6e-13cf8578320f', '374288', '374288485129711');
INSERT INTO childaccount (id, parentid, identifiernumber, uniquecode)
VALUES ('1dd56a99-ac84-4f56-b091-ec07bdbc4ad1', '602a4264-cf81-4ad3-aa6e-13cf8578320f', '30259182', '30259188914242');

-- ORDER MOCK DATA
INSERT INTO order_ (orderdate, accountid) VALUES ('2016-03-16 14:50:34.372000', '602a4264-cf81-4ad3-aa6e-13cf8578320f');
INSERT INTO order_ (orderdate, accountid) VALUES ('2016-03-16 14:50:34.372000', '11cbd1da-5358-4789-95e5-891fe72c34e6');
INSERT INTO order_ (orderdate, accountid) VALUES ('2016-03-16 14:50:34.372000', 'e208462f-411f-4222-b957-0bddabe0fb99');

-- ITEM MOCK DATA
INSERT INTO item (price, type, description, thumbnailpath)
VALUES (500, 'mug', 'A mug that can hold liquids', '/mug');
INSERT INTO item (price, type, description, thumbnailpath)
VALUES (1000, 'mousemat', 'A mousemat', '/mousemat');

INSERT INTO public.school (id, name, location, country)
VALUES (1, 'Basisschool Bert', 'Eindhoven', 'Netherlands');

INSERT INTO public.photo (id, price, capturedate, pathtophoto, photographerid, childid, schoolid)
VALUES ('b34e0301-cf1a-4145-8b78-9e13a8633657', 500, '2016-03-2 14:50:34.372000', '/photo1',
        'c525a7c3-717f-4e6e-8e4d-9c31e09f03ad', '1dd56a99-ac84-4f56-b091-ec07bdbc4ad1', 1);

INSERT INTO public.photoconfiguration (effectid, itemid, photoid)
VALUES (NULL, 1, 'b34e0301-cf1a-4145-8b78-9e13a8633657');

INSERT INTO public.orderline (orderid, photoconfigurationid, amount) VALUES (1, 1, 1);
INSERT INTO public.orderline (orderid, photoconfigurationid, amount) VALUES (1, 1, 1);
INSERT INTO public.orderline (orderid, photoconfigurationid, amount) VALUES (3, 1, 1);