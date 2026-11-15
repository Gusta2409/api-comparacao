package br.edu.tcc.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import br.edu.tcc.api.Contratos.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class CoordenadorBuscasTeste {
 @Test void simulacaoAgendaTrabalhoSemAcessarKafkaEConcluiPorRest() throws Exception {
  var id=UUID.randomUUID();
  var registro=mock(RegistroBuscas.class);
  when(registro.criar("controle","ASSINCRONO_SIMULADO")).thenReturn(id);
  when(registro.consultar(id)).thenReturn(new Resposta(id,"controle","ASSINCRONO_SIMULADO","PROCESSANDO",1L,null,List.of()));
  @SuppressWarnings("unchecked") ObjectProvider<KafkaTemplate<String,String>> kafka=mock(ObjectProvider.class);
  var executor=mock(ThreadPoolTaskExecutor.class);
  var servidor=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
  servidor.createContext("/interno/buscas/sincrona", troca -> {
   var corpo=("{\"eventoId\":\""+UUID.randomUUID()+"\",\"buscaId\":\""+id+"\",\"termo\":\"controle\",\"concluidoEm\":null,\"status\":\"CONCLUIDA\",\"resultados\":[],\"erro\":null}").getBytes(StandardCharsets.UTF_8);
   troca.getRequestBody().readAllBytes();
   troca.getResponseHeaders().set("Content-Type","application/json");
   troca.sendResponseHeaders(200,corpo.length);
   troca.getResponseBody().write(corpo); troca.close();
  });
  servidor.start();
  try {
   var ambiente=new MockEnvironment(); ambiente.setActiveProfiles("demonstracao");
   var coordenador=new CoordenadorBuscas(registro,kafka,new ObjectMapper(),"http://127.0.0.1:"+servidor.getAddress().getPort(),ambiente,executor);
   var resposta=coordenador.buscar("controle",true);
   assertThat(resposta.status()).isEqualTo("PROCESSANDO");
   verify(registro,never()).concluir(any());
   var tarefa=ArgumentCaptor.forClass(Runnable.class);
   verify(executor).execute(tarefa.capture());
   tarefa.getValue().run();
   verify(registro).concluir(argThat(evento -> evento.buscaId().equals(id) && evento.status().equals("CONCLUIDA")));
   verifyNoInteractions(kafka);
  } finally { servidor.stop(0); }
 }

 @Test void filaCheiaRegistraFalhaSemPublicarKafka() {
  var id=UUID.randomUUID(); var registro=mock(RegistroBuscas.class);
  when(registro.criar("controle","ASSINCRONO_SIMULADO")).thenReturn(id);
  when(registro.consultar(id)).thenReturn(new Resposta(id,"controle","ASSINCRONO_SIMULADO","FALHOU",1L,1L,List.of()));
  @SuppressWarnings("unchecked") ObjectProvider<KafkaTemplate<String,String>> kafka=mock(ObjectProvider.class);
  var executor=mock(ThreadPoolTaskExecutor.class);
  doThrow(new java.util.concurrent.RejectedExecutionException()).when(executor).execute(any(Runnable.class));
  var ambiente=new MockEnvironment(); ambiente.setActiveProfiles("demonstracao");
  var coordenador=new CoordenadorBuscas(registro,kafka,new ObjectMapper(),"http://localhost:8081",ambiente,executor);
  assertThat(coordenador.buscar("controle",true).status()).isEqualTo("FALHOU");
  verify(registro).falhar(id); verifyNoInteractions(kafka);
 }
}
