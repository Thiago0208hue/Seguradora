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
		String cpfLimpo = segurado.cpf == null ? "" : segurado.cpf.replaceAll("[^0-9]", "");
		String telefoneLimpo = segurado.telefone == null ? "" : segurado.telefone.replaceAll("[^0-9]", "");

		validation.required("segurado.nome", segurado.nome)
			.message("É obrigatório informar o nome do segurado.");
		validation.match("segurado.nome", segurado.nome, "[A-Za-z ]+")
			.message("Nome deve conter apenas letras e espaços.");
		validation.required("segurado.cpf", cpfLimpo)
			.message("É obrigatório especificar o CPF do segurado.");
		validation.match("segurado.cpf", cpfLimpo, "[0-9]{11}")
			.message("O CPF deve conter 11 dígitos numéricos.");
		validation.required("segurado.telefone", telefoneLimpo)
			.message("É obrigatorio especificar o telefone do cliente.");
		validation.match("segurado.telefone", telefoneLimpo, "[0-9]{11}")
			.message("O telefone deve conter 11 dígitos ex:00123456789");
		validation.required("segurado.email", segurado.email)
			.message("É obrigatório informar o email do segurado.");
		validation.email("segurado.email", segurado.email)
			.message("Email em formato inválido.");

		if (validation.hasErrors()) {
			renderTemplate("Segurados/form.html", segurado);
		}

		segurado.nome = segurado.nome.toUpperCase();
		segurado.email = segurado.email.toLowerCase();
		segurado.cpf = cpfLimpo.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
		segurado.telefone = telefoneLimpo.replaceAll("(\\d{2})(\\d{9})", "($1)$2");
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