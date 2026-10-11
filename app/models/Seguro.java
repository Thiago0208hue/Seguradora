package models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import play.data.binding.As;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Seguro extends Model {

	@Required(message = "É obrigatório especificar a placa do veículo.")
	@Match(value = "[A-Za-z]{3}[0-9][A-Za-z][0-9]{2}", message = "Placa em formato inválido. Use o padrão Mercosul (ABC1D23).")
	public String placa;

	@Required(message = "Informe a marca do veículo.")
	public String marca;

	@Required(message = "É obrigatório especificar o modelo do veículo.")
	public String modelo;

	@Required(message = "Informe o ano de fabricação.")
	@Min(value = 1900, message = "Ano de fabricação inválido.")
	@CheckWith(value = AnoNaoFuturoCheck.class, message = "O ano de fabricação não pode ser futuro.")
	public Integer anoFabricacao;

	public static class AnoNaoFuturoCheck extends Check {
		@Override
		public boolean isSatisfied(Object objetoValidado, Object valor) {
			if (valor == null) {
				return true;
			}

			int anoInformado = ((Number) valor).intValue();
			int anoAtual = Calendar.getInstance().get(Calendar.YEAR);

			return anoInformado <= anoAtual;
		}
	};

	@Enumerated(EnumType.STRING)
	@Required(message = "Informe o tipo de veículo.")
	public TipoVeiculo tipoVeiculo;

	@Enumerated(EnumType.STRING)
	@Required(message = "Informe a cobertura do seguro.")
	public Cobertura cobertura;

	public BigDecimal valorFranquia;

	public BigDecimal valorAnual;

	@Enumerated(EnumType.STRING)
	public NivelRisco nivelRisco;

	@Enumerated(EnumType.STRING)
	public TipoFranquia tipoFranquia;

	@As("dd/MM/yyyy")
	@Required(message = "Informe a data de contratação.")
	@CheckWith(value = DataContratacaoNaoFuturaCheck.class, message = "A data de contratação não pode ser futura.")
	public Date dataContratacao;

	public static class DataContratacaoNaoFuturaCheck extends Check {
		@Override
		public boolean isSatisfied(Object objetoValidado, Object valor) {
			if (valor == null) {
				return true;
			}

			Calendar dataContratacao = Calendar.getInstance();
			dataContratacao.setTime((Date) valor);
			dataContratacao.set(Calendar.HOUR_OF_DAY, 0);
			dataContratacao.set(Calendar.MINUTE, 0);
			dataContratacao.set(Calendar.SECOND, 0);
			dataContratacao.set(Calendar.MILLISECOND, 0);

			Calendar hoje = Calendar.getInstance();
			hoje.set(Calendar.HOUR_OF_DAY, 0);
			hoje.set(Calendar.MINUTE, 0);
			hoje.set(Calendar.SECOND, 0);
			hoje.set(Calendar.MILLISECOND, 0);

			return !dataContratacao.after(hoje);
		}
	}

	@ManyToOne
	@Required(message = "É obrigatório que uma apólice tenha um segurado.")
	public Segurado segurado;

	@Required(message = "Informe o valor do veículo.")
	@Min(value = 1, message = "O valor do veículo deve ser maior que zero.")
	public BigDecimal valorVeiculo;

	@Enumerated(EnumType.STRING)
	public Status status;

	private static final BigDecimal VALOR_ATE_PONTO_0 = new BigDecimal("60000");
	private static final BigDecimal VALOR_ATE_PONTO_1 = new BigDecimal("150000");
	private static final BigDecimal FRANQUIA_BASE = new BigDecimal("0.03");

	public void calcular() {
		this.nivelRisco = calcularRisco();
		this.tipoFranquia = definirTipoFranquia(this.nivelRisco);
		this.valorFranquia = calcularFranquia();
		this.valorAnual = calcularAnuidade();
	}

	public NivelRisco calcularRisco() {
		int pontos = pontosTipo() + pontosValor() + pontosCobertura();
		if (pontos == 0) {
			return NivelRisco.MUITO_BAIXO;
		} else if (pontos == 1) {
			return NivelRisco.BAIXO;
		} else if (pontos == 2) {
			return NivelRisco.MEDIO;
		} else if (pontos == 3) {
			return NivelRisco.ALTO;
		}
		return NivelRisco.MUITO_ALTO;
	}

	private static TipoFranquia definirTipoFranquia(NivelRisco risco) {
		if (risco == NivelRisco.MUITO_BAIXO || risco == NivelRisco.BAIXO) {
			return TipoFranquia.OBRIGATORIA;
		} else if (risco == NivelRisco.MEDIO) {
			return TipoFranquia.MODERADA;
		}
		return TipoFranquia.MAJORADA;
	}

	public BigDecimal calcularFranquia() {
		BigDecimal multiplicador = new BigDecimal("2.0");
		if (this.tipoFranquia == TipoFranquia.OBRIGATORIA) {
			multiplicador = new BigDecimal("1.0");
		} else if (this.tipoFranquia == TipoFranquia.MODERADA) {
			multiplicador = new BigDecimal("1.5");
		}
		return this.valorVeiculo.multiply(FRANQUIA_BASE).multiply(multiplicador)
				.setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal calcularAnuidade() {
		BigDecimal taxa = new BigDecimal("0.100");
		if (this.nivelRisco == NivelRisco.MUITO_BAIXO) {
			taxa = new BigDecimal("0.035");
		} else if (this.nivelRisco == NivelRisco.BAIXO) {
			taxa = new BigDecimal("0.045");
		} else if (this.nivelRisco == NivelRisco.MEDIO) {
			taxa = new BigDecimal("0.060");
		} else if (this.nivelRisco == NivelRisco.ALTO) {
			taxa = new BigDecimal("0.080");
		}
		return this.valorVeiculo.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
	}

	private int pontosTipo() {
		if (this.tipoVeiculo == TipoVeiculo.CARRO) {
			return 0;
		} else if (this.tipoVeiculo == TipoVeiculo.MOTO) {
			return 2;
		}
		return 1; 
	}

	private int pontosValor() {
		if (this.valorVeiculo.compareTo(VALOR_ATE_PONTO_0) <= 0) {
			return 0;
		} else if (this.valorVeiculo.compareTo(VALOR_ATE_PONTO_1) <= 0) {
			return 1;
		}
		return 2;
	}

	private int pontosCobertura() {
		return this.cobertura == Cobertura.TOTAL ? 1 : 0;
	}

	public Seguro() {
		this.status = Status.ATIVO;
	}
}