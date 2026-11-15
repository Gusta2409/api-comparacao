# Modelo do banco

Banco SQL Server `COMPARADOR_PRECO`, criado manualmente ou pelo contêiner opcional `preparar-banco`. Execute manualmente `banco-dados/criar-tabelas.sql` antes de iniciar a API. Não há Flyway nem execução automática de SQL. JPA apenas valida o esquema existente (`ddl-auto: validate`).

- `BUSCAS`: UUID, termo, modo, estado, criação, conclusão, resposta inicial, tempo total e versão otimista.
- `RESULTADOS_BUSCA`: identidade, FK de busca, fonte, produto, preço decimal e tempo por fonte.
- `EXECUCOES_EXPERIMENTO`: cenário, modo, carga, repetição, médias e taxas; reservada, ainda sem preenchimento automático.

Cada busca contém zero ou mais resultados. Valores monetários usam decimal(18,2); horários são UTC em datetime2. Resultados são ordenados ao montar o contrato da API. O serviço de busca não mantém banco.
