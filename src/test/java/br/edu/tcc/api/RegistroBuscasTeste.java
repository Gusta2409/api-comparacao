package br.edu.tcc.api;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.time.Instant;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import br.edu.tcc.api.Contratos.*;
class RegistroBuscasTeste {
 @Test void ordenaPrecosEIgnoraConclusaoDuplicada() {
  var repositorio=mock(RepositorioBuscas.class); var registro=new RegistroBuscas(repositorio);
  var busca=new Busca(); busca.id=UUID.randomUUID(); busca.termo="controle"; busca.modo="ASSINCRONO"; busca.status="PROCESSANDO"; busca.criadaEm=Instant.now();
  when(repositorio.findById(busca.id)).thenReturn(Optional.of(busca));
  var evento=new EventoConcluido(UUID.randomUUID(),busca.id,busca.termo,Instant.now(),"CONCLUIDA",List.of(new Resultado("A","produto A",new BigDecimal("319.90"),0),new Resultado("B","produto B",new BigDecimal("289.90"),0)),null);
  registro.concluir(evento); registro.concluir(evento);
  var resposta=registro.consultar(busca.id);
  assertThat(resposta.resultados()).hasSize(2);
  assertThat(resposta.resultados().getFirst().fonte()).isEqualTo("B");
  assertThat(resposta.status()).isEqualTo("CONCLUIDA");
 }
}
