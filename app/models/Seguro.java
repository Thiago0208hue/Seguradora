
package models;

import java.math.BigDecimal;
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
    @Match(
        value = "[A-Za-z]{3}[0-9][A-Za-z][0-9]{2}",
        message = "Placa em formato inválido. Use o padrão Mercosul (ABC1D23)."
    )
    public String placa;

    @Required(message = "Informe a marca do veículo.")
    public String marca;

    @Required(message = "É obrigatório especificar o modelo do veículo.")
    public String modelo;

    @Required(message = "Informe o ano de fabricação.")
    @Min(value = 1900, message = "Ano de fabricação inválido.")
    @CheckWith(
        value = AnoNaoFuturoCheck.class,
        message = "O ano de fabricação não pode ser futuro."
    )
    public Integer anoFabricacao;

    public static class AnoNaoFuturoCheck extends Check {
        @Override
        public boolean isSatisfied(Object objetoValidado, Object valor) {
            if (valor == null) {
                return true; // @Required cuida do campo vazio
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

    @Required(message = "Informe o valor da franquia.")
    @Min(value = 0, message = "A franquia não pode ser negativa.")
    public BigDecimal valorFranquia;

    @Required(message = "Informe o valor anual do seguro.")
    @Min(value = 1, message = "O valor anual deve ser maior que zero.")
    public BigDecimal valorAnual;

    @As("dd/MM/yyyy")
    @Required(message = "Informe a data de contratação.")
    @CheckWith(
        value = DataContratacaoNaoFuturaCheck.class,
        message = "A data de contratação não pode ser futura."
    )
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

    public Seguro() {
        this.status = Status.ATIVO;
    }
}