package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import play.data.binding.As;
import play.data.validation.Match;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Seguro extends Model {

	@Required(message = "É obrigatorio especificar a placa do veículo.")
	@Match(value = "[A-Za-z]{3}[0-9][A-Za-z][0-9]{2}", message = "Placa em formato inválido, use o padrão mercosul ex:(ABC1D23).")
	public String placa;
	@Required(message = "É obrigatorio especificar o modelo do veículo.")
	public String modelo;
	@As("dd/MM/yyyy")
	public Date dataContratacao;
	@ManyToOne
	@Required(message = "É obrigatório que uma apólice tenha um segurado.")
	public Segurado segurado;

	@Enumerated(EnumType.STRING)
	public Status status;

	public Seguro() {
		this.status = Status.ATIVO;
	}

}