package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Seguro;
import models.Segurado;
import models.Status;
import play.mvc.Controller;

public class Seguros extends Controller {

	public static void form() {
		Seguro s = new Seguro();
		List<Segurado> segurados = Segurado.findAll();
		render(s, segurados);
	}

	public static void editar(Long id) {
		Seguro s = Seguro.findById(id);
		List<Segurado> segurados = Segurado.findAll();
		renderTemplate("Seguros/form.html", s, segurados);
	}

	public static void listar(String termo) {
		List<Seguro> seguros = Seguro.find("status != ?1", Status.INATIVO).fetch();
		if (termo != null && !termo.trim().isEmpty()) {
			String cpfBusca = termo.replaceAll("[^0-9]", "");
			List<Seguro> filtrados = new ArrayList<Seguro>();
			for (Seguro s : seguros) {
				if (s.segurado != null && s.segurado.cpf != null
						&& s.segurado.cpf.replaceAll("[^0-9]", "").contains(cpfBusca)) {
					filtrados.add(s);
				}
			}
			seguros = filtrados;
		}
		render(seguros, termo);
	}

	public static void detalhar(Long id) {
		Seguro seguro = Seguro.findById(id);
		render(seguro);
	}

	public static void salvar(Seguro seguro) {
		if ((seguro.placa == null || seguro.modelo == null || seguro.segurado == null)
				|| (seguro.placa.trim().isEmpty() || seguro.modelo.trim().isEmpty())) {
			flash.error("Cadastro inválido, selecione o segurado ou digite a placa ou o modelo corretamente!");
			form();
		}
		if (!seguro.placa.trim().matches("[A-Za-z]{3}[0-9][A-Za-z][0-9]{2}")) {
			flash.error("Placa em formato inválido. Use o padrão Mercosul (ex: ABC1D23).");
			form();
		}
		seguro.placa = seguro.placa.toUpperCase();
		seguro.modelo = seguro.modelo.toUpperCase();
		seguro.save();
		flash.success("Apólice cadastrada com sucesso!");
		listar(null);
	}

	public static void remover(Long id) {
		Seguro seguro = Seguro.findById(id);
		seguro.status = Status.INATIVO;
		seguro.save();

		flash.success("Apólice removida com sucesso!");
		listar(null);
	}

}