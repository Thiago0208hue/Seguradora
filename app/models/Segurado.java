package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import play.db.jpa.Model;

@Entity
public class Segurado extends Model {

	public String cpf;
	public String email;
	public String telefone;
	@Enumerated(EnumType.STRING)
	public Status status;
	public String nome;

	public Segurado() {
		this.status = Status.ATIVO;
	}

}