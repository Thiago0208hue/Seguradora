package controllers;

import models.Login;
import play.mvc.Before;
import play.mvc.Controller;

public class Autenticador extends Controller{
    
    @Before(priority = 1)
static void verificar() {
        if (!session.contains("usuario")){
            flash.error("Faça login para continuar");
            Logado.form();
        }
    }
@Before
static void somenteAdmin() {
    String acao = request.actionMethod;
    String idSegurado = params.get("segurado.id");
    String idSeguro = params.get("seguro.id");
    boolean salvandoEdicao = "salvar".equals(acao)
            && ((idSegurado != null && !idSegurado.isEmpty())
            || (idSeguro != null && !idSeguro.isEmpty()));
    boolean restrita = "editar".equals(acao) || "remover".equals(acao) || salvandoEdicao;
    if (restrita && !"ADMIN".equals(session.get("perfil"))) {
        flash.error("Acesso restrito ao Administrador");
        redirect("/");
    }
}
}
