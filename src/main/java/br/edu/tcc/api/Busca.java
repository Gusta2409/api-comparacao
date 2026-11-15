package br.edu.tcc.api;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
@Entity @Table(name="BUSCAS")
public class Busca {
 @Id public UUID id;
 @org.hibernate.annotations.Nationalized @Column(nullable=false,length=255) public String termo;
 @Column(nullable=false,length=20) public String modo;
 @Column(nullable=false,length=30) public String status;
 @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.TIMESTAMP) @Column(name="criada_em",nullable=false) public Instant criadaEm;
 @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.TIMESTAMP) @Column(name="finalizada_em") public Instant finalizadaEm;
 @Column(name="tempo_resposta_inicial_ms") public Long tempoRespostaInicialMs;
 @Column(name="tempo_total_ms") public Long tempoTotalMs;
 @Version public long versao;
 @OneToMany(mappedBy="busca",cascade=CascadeType.ALL,orphanRemoval=true) public List<ResultadoPersistido> resultados=new ArrayList<>();
}
