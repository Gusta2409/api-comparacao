# API de comparação de preços

Backend do TCC que compara busca síncrona (REST) e assíncrona (Kafka). Coordena o microsserviço de busca, persiste buscas e ofertas no SQL Server e disponibiliza métricas por API. Requer Java 21 e Maven.

## Executar localmente

1. Inicie SQL Server e Kafka. Se usar o Compose deste repositório, copie o arquivo `.env.exemplo` para `.env`, defina uma senha forte em `SENHA_SQLSERVER` e execute `docker compose up -d`. O serviço `preparar-banco` cria o banco `COMPARADOR_PRECO`; confira que ele terminou com código 0. As portas padrão são SQL Server 1433, Kafka 9092 e Kafka UI 8082. Se usar uma instalação de SQL Server separada, crie o banco manualmente.
2. Execute [banco-dados/criar-tabelas.sql](banco-dados/criar-tabelas.sql) conectado ao banco `COMPARADOR_PRECO`. A API valida o esquema, mas não cria tabelas.
3. Configure no terminal `BANCO_USUARIO` e `SENHA_SQLSERVER`. Para o Compose, use `sa` e a mesma senha definida no `.env`; o Spring Boot não lê esse arquivo diretamente. Se necessário, ajuste `BANCO_URL`, `KAFKA_SERVIDORES` e `BUSCA_URL`. Os endereços padrão são `localhost:1433`, `localhost:9092` e `http://localhost:8081`. Para autenticação integrada do Windows, configure `BANCO_URL` com `integratedSecurity=true` e disponibilize a DLL de autenticação do driver JDBC compatível com o ambiente.
4. Inicie o [serviço de busca](https://github.com/Gusta2409/servico-busca) e execute, neste repositório:

```sh
mvn verify
mvn spring-boot:run
```

A API usa a porta 8080 por padrão. Os dois serviços Java usam o perfil `kafka` por padrão; o perfil opcional `demonstracao` deve ser selecionado em ambos para executar sem broker.

## Endpoints

| Método | Caminho | Função |
|---|---|---|
| POST | `/api/buscas/sincrona` | Realizar busca e retornar o resultado |
| POST | `/api/buscas/assincrona` | Iniciar busca via Kafka |
| GET | `/api/buscas/{id}/resultados` | Consultar estado e resultados |
| GET | `/api/experimentos/resultados` | Consultar buscas e métricas |
| GET | `/api/experimentos/resumo` | Consultar médias e taxas por modo |

Consulte os [contratos REST](documentacao/contratos-api.md), os [tópicos Kafka](documentacao/topicos-kafka.md), a [arquitetura](documentacao/arquitetura.md) e o [modelo do banco](documentacao/modelo-banco.md).

## Repositórios do TCC

- [api-comparacao](https://github.com/Gusta2409/api-comparacao): API e infraestrutura local.
- [servico-busca](https://github.com/Gusta2409/servico-busca): microsserviço das fontes simuladas.
- [interface-web](https://github.com/Gusta2409/interface-web): interface Angular.
