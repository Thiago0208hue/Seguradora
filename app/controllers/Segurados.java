package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Segurado;
import models.Status;
import play.mvc.Controller;
import play.mvc.With;

@With(Autenticador.class)
public class Segurados extends Controller {

	public static void form() {
		Segurado segurado = new Segurado();
		render(segurado);
	}

	public static void editar(Long id) {
		Segurado segurado = Segurado.findById(id);
		if (segurado == null) {
			flash.error("Segurado não encontrado");
			listar(null);
		}
		renderTemplate("Segurados/form.html", segurado);
	}

	public static void listar(String termo) {
		List<Segurado> segurados = Segurado.find("status != ?1", Status.INATIVO).fetch();
		if (termo != null && !termo.trim().isEmpty()) {
			String cpfBusca = termo.replaceAll("[^0-9]", "");
			List<Segurado> filtrados = new ArrayList<Segurado>();
			for (Segurado s : segurados) {
				if (s.cpf != null && s.cpf.replaceAll("[^0-9]", "").contains(cpfBusca)) {
					filtrados.add(s);
				}
			}
			segurados = filtrados;
		}
		render(segurados, termo);
	}

	public static void detalhar(Long id) {
		Segurado segurado = Segurado.findById(id);
		render(segurado);
	}


public static void salvar(Segurado segurado) {

    String cpfLimpo = segurado.cpf == null
        ? ""
        : segurado.cpf.replaceAll("[^0-9]", "");

    String telefoneLimpo = segurado.telefone == null
        ? ""
        : segurado.telefone.replaceAll("[^0-9]", "");

    segurado.cpf = cpfLimpo;
    segurado.telefone = telefoneLimpo;

    if (segurado.nome != null) {
        segurado.nome = segurado.nome.trim();
    }

    if (segurado.email != null) {
        segurado.email = segurado.email.trim().toLowerCase();
    }

    validation.valid("segurado", segurado);

    if (validation.hasErrors()) {
        renderTemplate("Segurados/form.html", segurado);
        return;
    }

    
    List<Segurado> todos = Segurado.findAll();

    for (Segurado existente : todos) {

        if (segurado.id != null &&
            segurado.id.equals(existente.id)) {
            continue;
        }

        String cpfExistente = existente.cpf == null
            ? ""
            : existente.cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.equals(cpfExistente)) {
            validation.addError(
                "segurado.cpf",
                "Já existe um segurado com esse CPF."
            );

            renderTemplate("Segurados/form.html", segurado);
            return;
        }
    }

    segurado.nome = segurado.nome.toUpperCase();
    segurado.cpf = cpfLimpo.replaceAll(
        "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
        "$1.$2.$3-$4"
    );
    segurado.telefone = telefoneLimpo.replaceAll(
        "(\\d{2})(\\d{9})",
        "($1)$2"
    );

    segurado.save();

    flash.success("Segurado cadastrado com sucesso!");
    listar(null);
}

	public static void remover(Long id) {
		Segurado segurado = Segurado.findById(id);
		segurado.status = Status.INATIVO;
		segurado.save();

		flash.success("Segurado removido com sucesso!");
		listar(null);
	}

}