

CREATE TABLE BUSCAS (
 id uniqueidentifier NOT NULL PRIMARY KEY, termo nvarchar(255) NOT NULL,
 modo varchar(20) NOT NULL CONSTRAINT CK_BUSCAS_MODO CHECK (modo IN ('SINCRONO','ASSINCRONO','ASSINCRONO_SIMULADO')),
 status varchar(30) NOT NULL CHECK (status IN ('PROCESSANDO','CONCLUIDA','FALHOU')),
 criada_em datetime2 NOT NULL, finalizada_em datetime2 NULL,
 tempo_resposta_inicial_ms bigint NULL, tempo_total_ms bigint NULL, versao bigint NOT NULL DEFAULT 0
);

CREATE TABLE RESULTADOS_BUSCA (
 id bigint IDENTITY PRIMARY KEY, busca_id uniqueidentifier NOT NULL REFERENCES BUSCAS(id),
 fonte nvarchar(100) NOT NULL, nome_produto nvarchar(500) NOT NULL,
 preco decimal(18,2) NOT NULL, tempo_fonte_ms bigint NOT NULL
);

CREATE INDEX ix_resultados_busca ON RESULTADOS_BUSCA(busca_id);
CREATE TABLE EXECUCOES_EXPERIMENTO (
 id bigint IDENTITY PRIMARY KEY, nome_cenario nvarchar(100), modo varchar(20) NOT NULL,
 quantidade_requisicoes int, numero_execucao int, media_resposta_inicial_ms decimal(18,2),
 media_tempo_total_ms decimal(18,2), taxa_sucesso decimal(5,2), taxa_falhas decimal(5,2), criada_em datetime2 NOT NULL
);
