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
        session.put("nome", u.nome != null ? u.nome : u.login);
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
    session.remove("nome");
    form();
}
   public static void registrar() {
	   render();
   }
   public static void criar(String nome, String login, String senha) {
	    if (nome == null || nome.trim().isEmpty() || login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
	        flash.error("Preencha nome, login e senha");
	        registrar();
	    }
	   if (Login.count("login = ?1", login) > 0) {
		   flash.error("Esse login já existe!!");
		   registrar();
	   }
	   Login novo = new Login();
	   novo.login = login;
	   novo.senha = senha;
	   novo.nome = nome.trim();
	   novo.perfil = "USUARIO";
	   novo.save();
	   session.put("usuario", novo.login);
	   session.put("perfil", novo.perfil);
	   session.put("nome", novo.nome);
	   flash.success("Conta criada! Bem-vindo, " + novo.nome + ".");
	   redirect("/");
   }
}