package br.edu.tcc.api;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import br.edu.tcc.api.Contratos.*;
@RestController @RequestMapping("/api")
public class ControladorBuscas {
 private final CoordenadorBuscas coordenador; private final RegistroBuscas registro;
 public ControladorBuscas(CoordenadorBuscas coordenador,RegistroBuscas registro) { this.coordenador=coordenador; this.registro=registro; }
 @PostMapping("/buscas/sincrona") public Resposta sincrona(@Valid @RequestBody Pedido pedido) { return coordenador.buscar(pedido.termo(),false); }
 @PostMapping("/buscas/assincrona") public ResponseEntity<Resposta> assincrona(@Valid @RequestBody Pedido pedido) { var resposta=coordenador.buscar(pedido.termo(),true); return ResponseEntity.status(resposta.status().equals("FALHOU")?HttpStatus.SERVICE_UNAVAILABLE:HttpStatus.ACCEPTED).body(resposta); }
 @GetMapping("/buscas/{id}/resultados") public Resposta resultados(@PathVariable UUID id) { return registro.consultar(id); }
 @GetMapping("/experimentos/resultados") public List<Resposta> experimentos() { return registro.listar(); }
 @GetMapping("/experimentos/resumo") public Map<String,Object> resumo() { return registro.resumo(); }
}
