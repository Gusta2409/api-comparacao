package br.edu.tcc.api;
import java.util.*;
import java.time.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import br.edu.tcc.api.Contratos.*;
@Service @Transactional
public class RegistroBuscas {
 private final RepositorioBuscas repositorio;
 public RegistroBuscas(RepositorioBuscas repositorio) { this.repositorio=repositorio; }
 public UUID criar(String termo,String modo) { var busca=new Busca(); busca.id=UUID.randomUUID(); busca.termo=termo; busca.modo=modo; busca.status="PROCESSANDO"; busca.criadaEm=Instant.now(); repositorio.save(busca); return busca.id; }
 private Busca obter(UUID id) { return repositorio.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Busca não encontrada")); }
 public void registrarInicial(UUID id,long tempo) { repositorio.registrarTempoInicial(id,tempo); }
 public void concluir(EventoConcluido evento) {
  var busca=obter(evento.buscaId()); if (!busca.status.equals("PROCESSANDO")) return;
  busca.status=evento.status(); busca.finalizadaEm=Instant.now(); busca.tempoTotalMs=Duration.between(busca.criadaEm,busca.finalizadaEm).toMillis();
  for(var resultado:evento.resultados()) { var linha=new ResultadoPersistido(); linha.busca=busca; linha.fonte=resultado.fonte(); linha.nomeProduto=resultado.nomeProduto(); linha.preco=resultado.preco(); linha.tempoFonteMs=resultado.tempoFonteMs(); busca.resultados.add(linha); }
 }
 public void falhar(UUID id) { var busca=obter(id); if (!busca.status.equals("PROCESSANDO")) return; busca.status="FALHOU"; busca.finalizadaEm=Instant.now(); busca.tempoTotalMs=Duration.between(busca.criadaEm,busca.finalizadaEm).toMillis(); }
 private Resposta resposta(Busca busca) { return new Resposta(busca.id,busca.termo,busca.modo,busca.status,busca.tempoRespostaInicialMs,busca.tempoTotalMs,busca.resultados.stream().sorted(Comparator.comparing(r->r.preco)).map(r->new Resultado(r.fonte,r.nomeProduto,r.preco,r.tempoFonteMs)).toList()); }
 @Transactional(readOnly=true) public Resposta consultar(UUID id) { return resposta(obter(id)); }
 @Transactional(readOnly=true) public List<Resposta> listar() { return repositorio.findAll().stream().sorted(Comparator.comparing((Busca b)->b.criadaEm).reversed()).map(this::resposta).toList(); }
 @Transactional(readOnly=true) public Map<String,Object> resumo() {
  var todas=listar(); var resumo=new LinkedHashMap<String,Object>();
  for(String modo:List.of("SINCRONO","ASSINCRONO_SIMULADO","ASSINCRONO")) { var buscas=todas.stream().filter(b->b.modo().equals(modo)).toList(); var concluidas=buscas.stream().filter(b->b.status().equals("CONCLUIDA")).toList();
   resumo.put(modo,Map.of("totalRequisicoes",buscas.size(),"mediaRespostaInicialMs",buscas.stream().filter(b->b.tempoRespostaInicialMs()!=null).mapToLong(Resposta::tempoRespostaInicialMs).average().orElse(0),"mediaTempoTotalMs",concluidas.stream().mapToLong(Resposta::tempoTotalMs).average().orElse(0),"taxaSucesso",buscas.isEmpty()?0:100.0*concluidas.size()/buscas.size(),"taxaFalhas",buscas.isEmpty()?0:100.0*buscas.stream().filter(b->b.status().equals("FALHOU")).count()/buscas.size()));
  } return resumo;
 }
}
