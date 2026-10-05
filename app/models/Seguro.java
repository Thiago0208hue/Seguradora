package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Seguro extends Model {

	public String placa;
	public String modelo;
	public Date dataContratacao;
	

	@ManyToOne
	public Segurado segurado;

	@Enumerated(EnumType.STRING)
	public Status status;

	public Seguro() {
		this.status = Status.ATIVO;
	}

}