package br.com.sankhya.vsl.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgecomercial.model.facades.helpper.ItemNotaHelpper;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.vsl.dao.Armario;
import br.com.sankhya.vsl.dao.Funcionario;
import br.com.sankhya.vsl.dao.Nota;
import br.com.sankhya.vsl.dao.Parceiro;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PerdaChave implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {

        Registro[] linhas = ctx.getLinhas();
        BigDecimal numNota = BigDecimal.valueOf((Double) ctx.getParam("NUMNOTA")) ;
        BigDecimal codParc = null;

        CabecalhoNotaVO notaVO;
        Set<BigDecimal> parceiros = new HashSet<>();
        Set<BigDecimal> matriculas = new HashSet<>();


        int i = 0;
        for (Registro linha : linhas) {
            BigDecimal codCenCus;

            if (ctx.confirmarSimNao("Confirma ação?", "Confirma perda da chave " + linha.getCampo("CODARM") + "/" + linha.getCampo("GAVETA") + "?", i++)){
                BigDecimal codArm = (BigDecimal) linha.getCampo("CODARM");
                BigDecimal gaveta = (BigDecimal) linha.getCampo("GAVETA");
                codParc = (BigDecimal) linha.getCampo("CODPARC");
                BigDecimal matricula = (BigDecimal) linha.getCampo("MATRICULA");
                parceiros.add(codParc);

                Armario.geraHistorico(ctx, codArm, gaveta, codParc, "P", "N");

                // Se Parceiro não controla área (TGFPAR.AD_NCRTLAREA = 'S', então TFGCAB.AD_CODCENCUS = 90999
                   // Senão, TGFCAB.AD_CODCENCUS = AD_FUNCIONARIO.CODCENCUS
                if (Parceiro.naoControlaArea(codParc)) {
                     codCenCus = BigDecimal.valueOf(90999);
                     matriculas.add(matricula);
                } else {
                    codCenCus = Funcionario.getCodCenCus(codParc, matricula);
                    notaVO = Nota.lancarCabecalhoNota(codParc,codCenCus, numNota);
                    Collection<ItemNotaVO> itens = new ArrayList<>();
                    ItemNotaVO itemVO = Nota.lancarItemNota(notaVO,BigDecimal.valueOf(8600101), BigDecimal.ONE, matricula, codParc);
                    itens.add(itemVO);
                    ItemNotaHelpper.saveItensNota(itens,notaVO);

                    //Força atualização de AD_MATRICULA
                    for (ItemNotaVO item : itens) {
                        Nota.atualizaAdMatricula(item.getNUNOTA(), item.getSEQUENCIA(), item.getProperty("AD_MATRICULA"));
                    }
                }


            }
        }

        // Se o Parceiro for o mesmo para todos os registros
        if (parceiros.size() == 1 && matriculas.size() > 0) {
            notaVO = Nota.lancarCabecalhoNota(codParc,BigDecimal.valueOf(90999), numNota);
            Collection<ItemNotaVO> itens = new ArrayList<>();

            for (BigDecimal mat: matriculas) {
                ItemNotaVO itemVO = Nota.lancarItemNota(notaVO,BigDecimal.valueOf(8600101), BigDecimal.ONE, mat, codParc);
                //ctx.mostraErro(itemVO.getNUNOTA().toString() + " " + itemVO.getProperty("AD_MATRICULA"));
                itens.add(itemVO);

                for (ItemNotaVO item : itens) {
                    ctx.mostraErro(item.getNUNOTA().toString() + " " + item);
                }
            }
            ItemNotaHelpper.saveItensNota(itens,notaVO);
            //Força atualização de AD_MATRICULA
            for (ItemNotaVO item : itens) {
                ctx.mostraErro(item.getNUNOTA().toString() + " " + item);
                Nota.atualizaAdMatricula(item.getNUNOTA(), item.getSEQUENCIA(), item.getProperty("AD_MATRICULA"));
            }
        }

        }

}
