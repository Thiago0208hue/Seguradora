
package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import play.data.validation.Email;
import play.data.validation.Match;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Segurado extends Model {

    @Required(message = "Informe o CPF do segurado.")
    @Match(
        value = "(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})",
        message = "O CPF deve conter 11 dígitos."
    )
    public String cpf;

    @Required(message = "Informe o e-mail do segurado.")
    @Email(message = "Informe um e-mail válido.")
    public String email;

    @Required(message = "Informe o telefone do segurado.")
    @Match(
        value = "(\\d{11}|\\(\\d{2}\\)\\d{9})",
        message = "O telefone deve conter DDD e 9 dígitos."
    )
    public String telefone;

    @Enumerated(EnumType.STRING)
    public Status status;

    @Required(message = "Informe o nome do segurado.")
    @Match(
        value = "[\\p{L} ]+",
        message = "O nome deve conter apenas letras e espaços."
    )
    public String nome;

    public Segurado() {
        this.status = Status.ATIVO;
    }
}