package controllers;

import models.Login;
import play.mvc.Controller;

public class Logado extends Controller {

    public static void logar(String login, String senha) {

        Login usuario = new Login();

        usuario.login = login;
        usuario.senha = senha;

        String resultado = usuario.autenticar();

        if (resultado != null) {

            session.put("usuario", resultado);

        } else {

            flash.error("Login ou senha inválidos");

        }
    }
}