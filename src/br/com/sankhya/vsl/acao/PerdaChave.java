package br.com.sankhya.vsl.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.vsl.dao.Nota;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PerdaChave implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {

        Registro[] linhas = ctx.getLinhas();
        BigDecimal numNota = BigDecimal.valueOf((Double) ctx.getParam("NUMNOTA")) ;


        for (int i = 0; i < linhas.length; i++) {

                if (ctx.confirmarSimNao("Confirma ação?", "Confirma perda da chave " + linhas[i].getCampo("CODARM") + "/" + linhas[i].getCampo("GAVETA") + "?", i)){
                    Registro historico = ctx.novaLinha("AD_HISTORICO_ARMARIO");
                    historico.setCampo("MATRICULA", linhas[i].getCampo("MATRICULA"));
                    historico.setCampo("CODPARC", linhas[i].getCampo("CODPARC"));
                    historico.setCampo("CODARM", linhas[i].getCampo("CODARM"));
                    historico.setCampo("GAVETA", linhas[i].getCampo("GAVETA"));
                    historico.setCampo("MOTIVO", "P"); // Perda
                    historico.setCampo("DEVCHAVE", "N"); // Não devolveu chave
                    historico.setCampo("DTINI", linhas[i].getCampo("DTINI"));
                    historico.setCampo("DTDEV", Timestamp.valueOf(LocalDateTime.now()));
                    historico.save();

                    CabecalhoNotaVO notaVO = Nota.lancarCabecalhoNota(historico.getCampo("CODPARC"), BigDecimal.valueOf(90999), numNota);

                    Nota.lancarItemNota(notaVO,BigDecimal.valueOf(8600101), BigDecimal.ONE, historico.getCampo("MATRICULA"));

                }
        }

        }

}
