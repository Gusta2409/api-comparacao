package br.edu.tcc.api;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RepositorioBuscas extends JpaRepository<Busca,UUID> {
 @org.springframework.data.jpa.repository.Modifying
 @org.springframework.data.jpa.repository.Query("update Busca b set b.tempoRespostaInicialMs = :tempo where b.id = :id")
 void registrarTempoInicial(@org.springframework.data.repository.query.Param("id") UUID id, @org.springframework.data.repository.query.Param("tempo") long tempo);
}
