package br.edu.tcc.api;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.edu.tcc.api.Contratos.EventoConcluido;
@Component
@org.springframework.context.annotation.Profile("kafka")
public class ConsumidorResultados {
 private final RegistroBuscas registro; private final ObjectMapper json;
 public ConsumidorResultados(RegistroBuscas registro,ObjectMapper json) { this.registro=registro; this.json=json; }
 @KafkaListener(topics="produto.busca.concluida") public void consumir(String mensagem) throws Exception { registro.concluir(json.readValue(mensagem,EventoConcluido.class)); }
}
