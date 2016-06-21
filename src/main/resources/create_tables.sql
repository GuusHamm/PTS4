DROP TABLE user_cookie, account, childaccount, childaccount_account, effect, item, order_, orderline, rating, photoconfiguration, photo, school, userright CASCADE;

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
  type          TEXT             DEFAULT 'customer',
  theme         TEXT
  address       TEXT,
    CHECK (hash IS NOT NULL OR account.oauthkey IS NOT NULL )
);

CREATE TABLE childaccount (
  id         UUID PRIMARY KEY DEFAULT uuid_generate_v1(),
  uniquecode TEXT NOT NULL UNIQUE
);

CREATE TABLE childaccount_account (
  childaccount_ID UUID REFERENCES childaccount (id),
  account_ID      UUID REFERENCES account (id)
);

CREATE TABLE photo (
  id                UUID PRIMARY KEY DEFAULT uuid_generate_v1(),
  price             INTEGER NOT NULL,
  capturedate       DATE    NOT NULL,
  pathtophoto       TEXT    NOT NULL,
  pathtolowresphoto TEXT,
  photographerid    UUID REFERENCES account (id),
  childid           UUID REFERENCES childaccount (id),
  schoolid          INTEGER REFERENCES school (id),
  points            INTEGER DEFAULT 0
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

CREATE TABLE public.link
         (
         key TEXT PRIMARY KEY NOT NULL,
         link TEXT NOT NULL,
         authorizedUser UUID,
         CONSTRAINT link_authorizedUser_account_fk FOREIGN KEY (authorizedUser) REFERENCES account (id)
         );
CREATE UNIQUE INDEX link_key_uindex ON public.link (key);

-- ACCOUNT MOCK DATA
INSERT INTO account (id, name, email, active, hash,address)
VALUES ('602a4264-cf81-4ad3-aa6e-13cf8578320f', 'Norma Jones', 'njones0@amazonaws.com', TRUE,
        '$s0$e0801$UXWHFDS7gqHNjtlnbXG0fg==$MC6JpBSIMH5phCn6ypeWsFw3/e4jZuy5lqOUmjvx2Yc=','Rachelsmolen 1, 1234AB Eindhoven');

INSERT INTO public.school (id, name, location, country)
VALUES (1, 'Basisschool Bert', 'Eindhoven', 'Netherlands');

