ALTER TABLE goods DROP COLUMN id;

ALTER TABLE goods
ADD CONSTRAINT goods_pk
PRIMARY KEY (owner, product);

ALTER TABLE goods
    ADD COLUMN price FLOAT NOT NULL;