package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Seguro;
import models.Segurado;
import models.Status;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;

@With(Autenticador.class)
public class Seguros extends Controller {

	public static void form() {
		Seguro seguro = new Seguro();
		List<Segurado> segurados = Segurado.findAll();
		render(seguro, segurados);
	}

	public static void editar(Long id) {
		Seguro seguro = Seguro.findById(id);
		if (seguro == null) {
			flash.error("Apólice não encontrada");
			listar(null);
		}
		List<Segurado> segurados = Segurado.findAll();
		renderTemplate("Seguros/form.html", seguro, segurados);
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

	public static void salvar(@Valid Seguro seguro) {
		if (validation.hasErrors()) {
			List<Segurado> segurados = Segurado.findAll();
			renderTemplate("Seguros/form.html", seguro, segurados);
		}

		if (!validation.hasError("seguro.placa")) {
			String placaFormatada = seguro.placa.toUpperCase();
			Seguro existente = Seguro.find("placa = ?1 and status = ?2", placaFormatada, Status.ATIVO).first();
			if (existente != null && !existente.id.equals(seguro.id)) {
				validation.addError("seguro.placa", "Já existe uma apólice ativa cadastrada para essa placa.");
			}
		}

		if (validation.hasErrors()) {
			List<Segurado> segurados = Segurado.findAll();
			renderTemplate("Seguros/form.html", seguro, segurados);
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