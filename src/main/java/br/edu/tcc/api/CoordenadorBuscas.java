package br.edu.tcc.api;
import java.time.Instant;
import java.util.*;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.edu.tcc.api.Contratos.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@Service
public class CoordenadorBuscas {
 private final RegistroBuscas registro; private final ObjectProvider<KafkaTemplate<String,String>> kafka; private final ObjectMapper json; private final RestClient cliente;
 private final boolean usarKafka; private final ThreadPoolTaskExecutor executor;
 public CoordenadorBuscas(RegistroBuscas registro,ObjectProvider<KafkaTemplate<String,String>> kafka,ObjectMapper json,@Value("${busca.url}") String url, Environment ambiente, ThreadPoolTaskExecutor executorDemonstracao) {
  this.registro=registro; this.kafka=kafka; this.json=json;
  this.usarKafka=ambiente.acceptsProfiles(Profiles.of("kafka")); this.executor=executorDemonstracao;
  var fabrica=new JdkClientHttpRequestFactory(java.net.http.HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()); fabrica.setReadTimeout(Duration.ofSeconds(30));
  this.cliente=RestClient.builder().baseUrl(url).requestFactory(fabrica).build();
 }
 public Resposta buscar(String termo,boolean assincrona) {
  long inicio=System.nanoTime(); var id=registro.criar(termo.trim(),assincrona?(usarKafka?"ASSINCRONO":"ASSINCRONO_SIMULADO"):"SINCRONO");
  try {
   if(assincrona && usarKafka) { var evento=new EventoSolicitado(UUID.randomUUID(),id,termo.trim(),Instant.now()); kafka.getObject().send("produto.busca.solicitada",id.toString(),json.writeValueAsString(evento)).get(12,java.util.concurrent.TimeUnit.SECONDS); }
   else if(assincrona) { executor.execute(() -> executarLocal(id,termo.trim())); }
   else { executarLocal(id,termo.trim()); }
  } catch(Exception erro) { if(erro instanceof InterruptedException) Thread.currentThread().interrupt(); registro.falhar(id); }
  registro.registrarInicial(id,(System.nanoTime()-inicio)/1_000_000); return registro.consultar(id);
 }
 private void executarLocal(UUID id,String termo) {
  try {
   var evento=cliente.post().uri("/interno/buscas/sincrona").body(new Solicitacao(id,termo)).retrieve().body(EventoConcluido.class);
   if(evento==null) throw new IllegalStateException("Resposta vazia");
   registro.concluir(evento);
  } catch(RuntimeException erro) { registro.falhar(id); }
 }
}
