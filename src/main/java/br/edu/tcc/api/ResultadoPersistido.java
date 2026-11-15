package br.edu.tcc.api;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="RESULTADOS_BUSCA")
public class ResultadoPersistido {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="busca_id") public Busca busca;
 @org.hibernate.annotations.Nationalized @Column(nullable=false,length=100) public String fonte;
 @org.hibernate.annotations.Nationalized @Column(name="nome_produto",nullable=false,length=500) public String nomeProduto;
 @Column(nullable=false,precision=18,scale=2) public BigDecimal preco;
 @Column(name="tempo_fonte_ms",nullable=false) public long tempoFonteMs;
}
