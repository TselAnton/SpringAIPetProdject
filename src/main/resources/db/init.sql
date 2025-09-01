CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS tb_message;
DROP TABLE IF EXISTS tb_chat;

CREATE TABLE tb_chat(
 id UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
 title TEXT NOT NULL,
 created_at BIGINT NOT NULL
);

CREATE TABLE tb_message(
  id UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
  content TEXT NOT NULL,
  created_at BIGINT NOT NULL,
  role TEXT NOT NULL,
  chat_id UUID NOT NULL REFERENCES tb_chat (id)
);