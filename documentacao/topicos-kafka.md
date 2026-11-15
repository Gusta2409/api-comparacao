# Tópicos Kafka

`produto.busca.solicitada`: API produz; serviço de busca consome.

```json
{"eventoId":"UUID","buscaId":"UUID","termo":"controle xbox","solicitadoEm":"2026-10-10T13:00:00Z"}
```

`produto.busca.concluida`: serviço produz; API consome.

```json
{"eventoId":"UUID","buscaId":"UUID","termo":"controle xbox","concluidoEm":"2026-10-10T13:00:03Z","status":"CONCLUIDA","resultados":[{"fonte":"LOJA_A","nomeProduto":"controle xbox - LOJA_A","preco":299.90,"tempoFonteMs":300}],"erro":null}
```

Falha: `status=FALHOU`, lista vazia e mensagem em `erro`. Chave Kafka: UUID da busca. Datas: UTC ISO 8601. Grupos: `api-comparacao`, `servico-busca`. Três partições, réplica única para desenvolvimento. Retry usa comportamento padrão Spring Kafka; após esgotamento, não existe recuperação persistente/DLT configurada. Eventos inválidos e interrupções devem ser investigados nos logs.
