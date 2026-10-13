package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.Match;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Segurado extends Model {

	@Required(message = "Informe o CPF do segurado.")
	@CheckWith(value = CpfValidoCheck.class, message = "CPF inválido.")
	@Match(value = "(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})", message = "O CPF deve conter 11 dígitos.")
	public String cpf;

	public static class CpfValidoCheck extends Check {
		@Override
		public boolean isSatisfied(Object objetoValidado, Object valor) {
			if (valor == null) {
				return true;
			}
			String d = valor.toString().replaceAll("[^0-9]", "");
			if (d.length() != 11 || d.matches("(\\d)\\1{10}")) {
				return false;
			}
			return digito(d, 9) == d.charAt(9) - '0' && digito(d, 10) == d.charAt(10) - '0';
		}

		private static int digito(String d, int tamanho) {
			int soma = 0;
			for (int i = 0; i < tamanho; i++) {
				soma += (d.charAt(i) - '0') * (tamanho + 1 - i);
			}
			int resto = (soma * 10) % 11;
			return resto == 10 ? 0 : resto;
		}
	}

	@Required(message = "Informe o e-mail do segurado.")
	@Email(message = "Informe um e-mail válido.")
	public String email;

	@Required(message = "Informe o telefone do segurado.")
	@Match(value = "(\\d{11}|\\(\\d{2}\\)\\d{9})", message = "O telefone deve conter DDD e 9 dígitos.")
	public String telefone;

	@Enumerated(EnumType.STRING)
	public Status status;

	@Required(message = "Informe o nome do segurado.")
	@Match(value = "[\\p{L} ]+", message = "O nome deve conter apenas letras e espaços.")
	public String nome;

	public Segurado() {
		this.status = Status.ATIVO;
	}
}