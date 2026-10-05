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
        Login u = Login.find("login =?1", resultado).first();
        session.put("perfil", u.perfil);
        redirect("/");
    } else {
        flash.error("Login ou senha inválidos");
        form();
    }
}
    
    public static void form() {
     render();
    }
   public static void sair() {
    session.remove("usuario");
    session.remove("perfil");
    form();
}
   public static void registrar() {
	   render();
   }
   public static void criar(String login, String senha) {
	   if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
		   flash.error("Preencha login e senha");
		   registrar();
	   }
	   if (Login.count("login = ?1", login) > 0) {
		   flash.error("Esse login já existe!!");
		   registrar();
	   }
	   Login novo = new Login();
	   novo.login = login;
	   novo.senha = senha;
	   novo.perfil = "USUARIO";
	   novo.save();
	   flash.success("Cadastro realizado! Faça login.");
	   form();
   }
}