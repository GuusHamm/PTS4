CREATE TABLE school (
  id       INTEGER PRIMARY KEY,
  name     TEXT NOT NULL,
  location TEXT,
  country  TEXT
);

CREATE TABLE account (
  id            INTEGER PRIMARY KEY,
  oauthkey      TEXT UNIQUE,
  oauthprovider TEXT        NOT NULL,
  name          TEXT        NOT NULL,
  email         TEXT UNIQUE NOT NULL,
  hash          TEXT,
  salt          TEXT,
  active        BOOLEAN
);

CREATE TABLE childaccount (
  id                INTEGER PRIMARY KEY,
  identtifiernumber INTEGER NOT NULL,
  uniquecode        TEXT    NOT NULL UNIQUE,
  parentid          INTEGER REFERENCES account (id)
);

CREATE TABLE photo (
  id             INTEGER PRIMARY KEY,
  price          INTEGER NOT NULL,
  capturedate    INTEGER NOT NULL,
  pathtophoto    TEXT    NOT NULL,
  photographerid INTEGER REFERENCES account (id),
  childid        INTEGER REFERENCES childaccount (id),
  schoolid       INTEGER REFERENCES school (id)
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
  description TEXT    NOT NULL,
  price       INTEGER NOT NULL
);

CREATE TABLE userright (
  createrights    BOOLEAN,
  updaterights    BOOLEAN,
  destroyrights   BOOLEAN,
  accountidrights INTEGER REFERENCES account (id)
);

CREATE TABLE photoconfiguration (
  id       INTEGER PRIMARY KEY,
  effectid INTEGER REFERENCES effect (id),
  itemid   INTEGER REFERENCES item (id),
  photoid  INTEGER REFERENCES photo (id)
);

CREATE TABLE order_ (
  id                   INTEGER PRIMARY KEY,
  orderdate            TIMESTAMP NOT NULL,
  accountid            INTEGER REFERENCES account (id),
  photoconfigurationid INTEGER REFERENCES photoconfiguration (id)
);

CREATE TABLE orderline (
  id                   INTEGER PRIMARY KEY,
  orderid              INTEGER REFERENCES order_ (id),
  photoconfigurationid INTEGER REFERENCES photoconfiguration (id)
);