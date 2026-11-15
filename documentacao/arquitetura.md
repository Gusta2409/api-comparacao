# Arquitetura

Síncrono: interface → API → REST interno do serviço → três fontes sequenciais → API persiste e ordena → interface.

Assíncrono: interface → API persiste → tópico solicitado → serviço consulta fontes → tópico concluído → API persiste e ordena. A interface consulta a API a cada 1,5 segundo.

Somente a API acessa SQL Server. Os contratos de eventos são duplicados nos dois módulos para permitir evolução independente; mantenha-os compatíveis. São serializados como JSON textual, sem cabeçalhos de tipo Java. As conclusões repetidas não substituem uma busca terminal; controle otimista de versão protege atualizações concorrentes.

Três partições permitem evolução para vários consumidores, mas este início mantém um consumidor por serviço. Não há garantia transacional entre SQL e Kafka. Documentar e resolver essa lacuna faz parte da evolução experimental.
