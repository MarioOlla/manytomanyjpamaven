package it.manytomanyjpamaven.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "utente")
public class Utente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "username")
	private String username;
	@Column(name = "password")
	private String password;
	@Column(name = "nome")
	private String nome;
	@Column(name = "cognome")
	private String cognome;
	@Column(name = "dateCreated")
	private Date dateCreated;

	// se non uso questa annotation viene gestito come un intero
	@Enumerated(EnumType.STRING)
	private StatoUtente stato = StatoUtente.CREATO;

	@ManyToMany
	@JoinTable(name = "utente_ruolo", joinColumns = @JoinColumn(name = "utente_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "ruolo_id", referencedColumnName = "ID"))
	private Set<Ruolo> ruoli = new HashSet<>(0);

	public Utente() {
	}

	public Utente(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public Utente(String username, String password, String nome, String cognome, Date dateCreated) {
		this.username = username;
		this.password = password;
		this.nome = nome;
		this.cognome = cognome;
		this.dateCreated = dateCreated;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Ruolo> getRuoli() {
		return ruoli;
	}

	public void setRuoli(Set<Ruolo> ruoli) {
		this.ruoli = ruoli;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public StatoUtente getStato() {
		return stato;
	}

	public void setStato(StatoUtente stato) {
		this.stato = stato;
	}

	@Override
	public String toString() {
		return "Utente [id=" + id + ", username=" + username + ", password=" + password + ", nome=" + nome
				+ ", cognome=" + cognome + ", dateCreated=" + dateCreated + "]";
	}

}
