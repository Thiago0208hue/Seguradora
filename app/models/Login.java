package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import play.db.jpa.Model;

@Entity
public class Login extends Model {

    public String senha;
    public String perfil;
    public String nome;
    @Column(unique = true, nullable = false, length = 50)
    public String login;
    public String autenticar() {

        Login usuario = Login.find(
            "login = ?1 and senha = ?2",
            login,
            senha
        ).first();

        if (usuario != null) {
            return usuario.login;
        }

        return null;
    }
    
}