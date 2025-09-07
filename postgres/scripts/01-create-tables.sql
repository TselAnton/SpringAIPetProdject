-- Расширение для работы с UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- Подготовка основных сущностей
-- =============================================

DROP TABLE IF EXISTS public.tb_message;
DROP TABLE IF EXISTS public.tb_chat_message;

CREATE TABLE public.tb_chat(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    title               TEXT NOT NULL,
    created_at          TIMESTAMP NOT NULL
);

CREATE TABLE public.tb_chat_message(
    id          UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    content     TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    role        TEXT NOT NULL,
    chat_id     UUID NULL REFERENCES tb_chat (id)
);

ALTER TABLE public.tb_chat OWNER TO postgres;
ALTER TABLE public.tb_chat_message OWNER TO postgres;

-- =============================================
-- Подготовка RAG (Retrieval-Augmented Generation) Tables
-- =============================================

-- Таблица загруженных документов
CREATE TABLE IF NOT EXISTS tb_document (
    id            UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    filename      VARCHAR(255) NOT NULL,
    content_hash  VARCHAR(64) NOT NULL,
    document_type VARCHAR(10) NOT NULL,
    chunk_count   INTEGER,
    loaded_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_document UNIQUE (filename, content_hash)
);

ALTER TABLE public.tb_document OWNER TO postgres;

-- Индекс для быстрого поиска по имени файла
CREATE INDEX IF NOT EXISTS idx_loaded_documents_filename ON tb_document(filename);

-- -- Таблица для векторного хранилища
-- CREATE TABLE IF NOT EXISTS vector_store (
--                                             id        VARCHAR(255) PRIMARY KEY,
--     content   TEXT,
--     metadata  JSON,
--     embedding VECTOR(1024)
--     );
--
-- -- Индекс HNSW для быстрого векторного поиска
-- CREATE INDEX IF NOT EXISTS vector_store_hnsw_index
--     ON vector_store USING hnsw (embedding vector_cosine_ops);
--
-- -- =============================================
-- -- Комментарии по типам индексов:
-- -- =============================================
-- -- HNSW (Hierarchical Navigable Small World) - эффективный для высокоразмерных векторов
-- -- IVFFlat - Inverted File с плоским хранением кластеров, хорош для больших объемов данных
-- -- Без индекса - прямой перебор всех векторов (медленно, но точно)