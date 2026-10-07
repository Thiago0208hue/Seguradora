package controllers;

import models.Login;
import play.mvc.Before;
import play.mvc.Controller;

public class Autenticador extends Controller{
    
    @Before 
    static void verificar(){
        if (!session.contains("usuario")){
            flash.error("Faça login para continuar");
            Logado.form();
        }
    }
  @Before
static void somenteAdmin() {
    String acao = request.actionMethod;
    boolean restrita = "editar".equals(acao) || "remover".equals(acao);
    if (restrita && !"ADMIN".equals(session.get("perfil"))) {
        flash.error("Acesso restrito ao Administrador");
        redirect("/");
    }
}
    
}
