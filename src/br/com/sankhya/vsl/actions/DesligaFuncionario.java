package br.com.sankhya.vsl.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgecomercial.model.facades.helpper.ItemNotaHelpper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.CentralFaturamento;
import br.com.sankhya.modelcore.comercial.ConfirmacaoNotaHelper;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.vsl.dao.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class DesligaFuncionario implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        final boolean devolveuChave = contextoAcao.getParam("DEVCHAVE").toString().equalsIgnoreCase("S");
        final String devolucaoChave = contextoAcao.getParam("DEVCHAVE").toString();
        final String motivoInativacao = (String) contextoAcao.getParam("MOTIVO");
        Timestamp dtNeg = (Timestamp) contextoAcao.getParam("DTNEG");
        if (dtNeg == null) dtNeg = Timestamp.valueOf(LocalDateTime.now());

        String numNota = "";
        if (contextoAcao.getParam("NUMNOTA") != null) {
            numNota = (String) contextoAcao.getParam("NUMNOTA");
        }

        // Verifica se mais de um registro (funcionário) foi selecionada
        if (linhas.length > 1) contextoAcao.mostraErro("Mais de um funcionário selecionado. Para inativar, selecione apenas um funcionário.");

        for (Registro linha: linhas) {
            BigDecimal matricula = (BigDecimal) linha.getCampo("MATRICULA");
            BigDecimal codParc = (BigDecimal) linha.getCampo("CODPARC");

            final boolean isAtivo = linha.getCampo("ATIVO").toString().equalsIgnoreCase("S");
            if (!isAtivo) contextoAcao.mostraErro("Funcionário já está inativo.");

            BigDecimal codCenCus = null;

            if (contextoAcao.confirmarSimNao("Confirma ação?", "Confirma inativação do funcionário " + Funcionario.getNome(codParc, matricula) + " do Parceiro " + Parceiro.getNomeParc(codParc) + "?", 1)){
                DynamicVO armarioVO = Armario.buscaArmarioPorFuncionario(codParc,matricula);
                Funcionario.inativaFuncionario(codParc, matricula, motivoInativacao);

                // Se o motivo da inativação for Desligamento
                if (motivoInativacao.equals("1") && armarioVO != null) {
                    Armario.geraHistorico(contextoAcao, armarioVO.asBigDecimalOrZero("CODARM"), armarioVO.asBigDecimalOrZero("GAVETA"), armarioVO.asBigDecimalOrZero("CODPARC"), "D", devolucaoChave);
                    Armario.liberaArmario(armarioVO.asBigDecimalOrZero("CODARM"), armarioVO.asBigDecimalOrZero("GAVETA"), armarioVO.asBigDecimalOrZero("CODPARC"));
                    //Se não devolveu chave lança nota de compra
                    if (!devolveuChave) {
                            // Se Parceiro não controla área (TGFPAR.AD_NCRTLAREA = 'S', então TFGCAB.AD_CODCENCUS = 90999
                            // Senão, TGFCAB.AD_CODCENCUS = AD_FUNCIONARIO.CODCENCUS
                            codCenCus = Parceiro.naoControlaArea(codParc) ? BigDecimal.valueOf(90999) : Funcionario.getCodCenCus(codParc, matricula);
                            CabecalhoNotaVO notaVO = Nota.lancaCabecalhoNota(codParc,codCenCus, numNota, dtNeg);
                            ItemNotaVO itemVO = Nota.montaItemNota(notaVO,BigDecimal.valueOf(8600101), BigDecimal.ONE, matricula, codParc);
                            Collection<ItemNotaVO> itens = new ArrayList<>();
                            itens.add(itemVO);
                            ItemNotaHelpper.saveItensNota(itens,notaVO);
                            //Confirma a nota
                            BarramentoRegra regra = BarramentoRegra.build(CentralFaturamento.class, "regrasConfirmacaoSilenciosa.xml", AuthenticationInfo.getCurrent());
                            regra.setValidarSilencioso(true);
                            ConfirmacaoNotaHelper.confirmarNota(notaVO.getNUNOTA(), regra, true);
                    }
                }
                contextoAcao.setMensagemRetorno("Funcionário " +Funcionario.getNome(codParc,matricula) + " ("+Funcionario.getPrefixMatricula(codParc, matricula)+") inativado.");
            }
        }
    }
}
