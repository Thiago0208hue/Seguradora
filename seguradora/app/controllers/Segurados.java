package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Segurado;
import models.Status;
import play.mvc.Controller;

public class Segurados extends Controller {

	public static void form() {
		Segurado s = new Segurado();
		render(s);
	}

	public static void editar(Long id) {
		Segurado s = Segurado.findById(id);
		renderTemplate("Segurados/form.html", s);
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
		if ((segurado.nome == null || segurado.cpf == null)
				|| (segurado.nome.trim().isEmpty() || segurado.cpf.trim().isEmpty())) {
			flash.error("Cadastro inválido, digite outro nome ou cpf para finalizar o processo");
			form();
		}
		if (!segurado.nome.trim().matches("[A-Za-z ]+")) {
			flash.error("Nome em formato inválido, digite apenas letras");
			form();
		}
		String cpfSonumeros = segurado.cpf.replaceAll("[^0-9]", "");
		if (!cpfSonumeros.trim().matches("[0-9]{11}")) {
			flash.error("CPF em formato inválido, digite apenas números");
			form();
		}
		String telefoneSonumeros = segurado.telefone.replaceAll("[^0-9]", "");
		if (!telefoneSonumeros.trim().matches("[0-9]{11}")) {
			flash.error("Telefone em formato inválido, digite apenas números formato brasileiro(ex: 84123456789).");
			form();
		}

		segurado.nome = segurado.nome.toUpperCase();
		segurado.email = segurado.email.toLowerCase();
		segurado.cpf = cpfSonumeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
		segurado.telefone = telefoneSonumeros.replaceAll("(\\d{2})(\\d{9})", "($1)$2");
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