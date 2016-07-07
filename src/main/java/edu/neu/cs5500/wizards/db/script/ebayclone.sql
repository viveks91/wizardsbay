DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS bids;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

CREATE TABLE items (
  id SERIAL NOT NULL,
  itemname varchar NOT NULL,
  itemdesc varchar NOT NULL,
  buyer int DEFAULT NULL,
  seller int NOT NULL,
  auctionstarttime timestamp NOT NULL DEFAULT now(),
  auctionendtime timestamp NULL DEFAULT NULL,
  minprice int NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE users (
  id SERIAL,
  username varchar NOT NULL,
  password varchar NOT NULL,
  firstname varchar NOT NULL,
  lastname varchar NOT NULL,
  address varchar NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE bids (
  id SERIAL NOT NULL,
  bidder int NOT NULL references users(id),
  itemid int NOT NULL references items(id),
  bidamount int NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE feedback (
  id SERIAL,
  userid int NOT NULL references users(id),
  feedbackdesc varchar NOT NULL,
  PRIMARY KEY (id)
);
