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
import br.com.sankhya.vsl.dao.Armario;
import br.com.sankhya.vsl.dao.Funcionario;
import br.com.sankhya.vsl.dao.Nota;
import br.com.sankhya.vsl.dao.Parceiro;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PerdaChave implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {

        Registro[] linhas = ctx.getLinhas();
        BigDecimal numNota = BigDecimal.valueOf((Double) ctx.getParam("NUMNOTA"));
        Timestamp dtNeg = (Timestamp) ctx.getParam("DTNEG");
        CabecalhoNotaVO notaVO;
        BigDecimal codParc = null;
        BigDecimal matricula;
        Set<BigDecimal> parceiros = new HashSet<>();
        Set<BigDecimal> matriculas = new HashSet<>();


        int i = 0;
        for (Registro linha : linhas) {
            BigDecimal codCenCus;
            matricula = (BigDecimal) linha.getCampo("MATRICULA");
            if (matricula == null) ctx.mostraErro("Armário/gaveta selecionado não possui funcionário vinculado.");
            codParc = (BigDecimal) linha.getCampo("CODPARC");
            DynamicVO armarioVO = Armario.buscaArmarioPorFuncionario(codParc, matricula);
            if (armarioVO == null) ctx.mostraErro("Funcionário "+linha.getCampo("NOMEFUNC")+" não possui armário/gaveta vinculado.");
            assert armarioVO != null;
            BigDecimal codArm = armarioVO.asBigDecimalOrZero("CODARM");
            BigDecimal gaveta = armarioVO.asBigDecimalOrZero("GAVETA");

            if (ctx.confirmarSimNao("Confirma ação?", "Confirma perda da chave " + codArm + "/" + gaveta + "?", i++)){
                //Controle dos Parceiros
                parceiros.add(codParc);

                Armario.geraHistorico(ctx, codArm, gaveta, codParc, "P", "N");

                // Se Parceiro não controla área (TGFPAR.AD_NCRTLAREA = 'S', então TFGCAB.AD_CODCENCUS = 90999
                // Senão, TGFCAB.AD_CODCENCUS = AD_FUNCIONARIO.CODCENCUS
                if (Parceiro.naoControlaArea(codParc)) {
                     codCenCus = BigDecimal.valueOf(90999);
                     matriculas.add(matricula);
                } else {
                    codCenCus = Funcionario.getCodCenCus(codParc, matricula);
                    notaVO = Nota.lancaCabecalhoNota(codParc,codCenCus,numNota, dtNeg);
                    Collection<ItemNotaVO> itens = new ArrayList<>();
                    ItemNotaVO itemVO = Nota.montaItemNota(notaVO,BigDecimal.valueOf(8600101), BigDecimal.ONE, matricula, codParc);
                    itens.add(itemVO);
                    ItemNotaHelpper.saveItensNota(itens,notaVO);
                    //Confirma a nota
                    BarramentoRegra regra = BarramentoRegra.build(CentralFaturamento.class, "regrasConfirmacaoSilenciosa.xml", AuthenticationInfo.getCurrent());
                    regra.setValidarSilencioso(true);
                    ConfirmacaoNotaHelper.confirmarNota(notaVO.getNUNOTA(), regra, true);
                }
            }
        }

        // Se o Parceiro for o mesmo para todos os registros, lança os itens na mesma nota
        if (parceiros.size() == 1 && matriculas.size() > 0 && Parceiro.naoControlaArea(codParc)) {
            notaVO = Nota.lancaCabecalhoNota(codParc,BigDecimal.valueOf(90999), numNota, dtNeg);
            Collection<ItemNotaVO> itens = new ArrayList<>();

            for (BigDecimal mat: matriculas) {
                itens.add(Nota.montaItemNota(notaVO,BigDecimal.valueOf(8600101), BigDecimal.ONE, mat, codParc));
            }
            ItemNotaHelpper.saveItensNota(itens,notaVO);
            //Confirma a nota
            BarramentoRegra regra = BarramentoRegra.build(CentralFaturamento.class, "regrasConfirmacaoSilenciosa.xml", AuthenticationInfo.getCurrent());
            regra.setValidarSilencioso(true);
            ConfirmacaoNotaHelper.confirmarNota(notaVO.getNUNOTA(), regra, true);

        }

        }

}
