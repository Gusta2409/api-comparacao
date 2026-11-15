# Contratos da API

No perfil padrão `kafka`, `/api/buscas/assincrona` publica no broker e retorna modo `ASSINCRONO`. Ao selecionar o perfil `demonstracao` explicitamente, o endpoint usa tarefas locais e retorna `ASSINCRONO_SIMULADO`. O contrato de campos e os estados são os mesmos.

POST `/api/buscas/sincrona` e `/api/buscas/assincrona`:

```json
{"termo":"controle xbox"}
```

Resposta/GET `/api/buscas/{id}/resultados`:

```json
{"buscaId":"d176e4d0-3d34-437b-8a4e-d76440f2c888","termo":"controle xbox","modo":"ASSINCRONO","status":"CONCLUIDA","tempoRespostaInicialMs":50,"tempoTotalMs":2700,"resultados":[{"fonte":"LOJA_C","nomeProduto":"controle xbox - LOJA_C","preco":289.90,"tempoFonteMs":1500}]}
```

Tempos ilustrativos; lista real inclui três lojas quando há sucesso. Modos: `SINCRONO`, `ASSINCRONO`. Estados: `PROCESSANDO`, `CONCLUIDA`, `FALHOU`. Tempos ainda indisponíveis são `null`; resultados pendentes/falhos são `[]`.

Termo obrigatório, não vazio, até 255 caracteres: inválidos retornam 400. UUID inválido retorna 400; busca ausente retorna 404. Publicação Kafka falha retorna 503 com busca falha. Síncrono retorna 200 com estado terminal, inclusive `FALHOU`; clientes devem verificar o estado.

REST interno: POST `/interno/buscas/sincrona`, corpo `{"buscaId":"UUID","termo":"controle xbox"}`. Retorna o contrato de conclusão descrito em `topicos-kafka.md`. Os endpoints de experimentos estão no README.
