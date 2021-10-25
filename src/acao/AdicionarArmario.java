package acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import dao.Armario;

import java.math.BigDecimal;

public class AdicionarArmario implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {


        Object codParc = contextoAcao.getParam("CODPARC");
        int qtdArmarios = (int) contextoAcao.getParam("QTDARM");
        int qtdGavetas = (int) contextoAcao.getParam("QTDGAVETAS");
        String prefixo = (String) contextoAcao.getParam("PRARM");
        int codArm = Armario.ultimoArmario(codParc);

        for (int i = 1; i <= qtdArmarios; i++) {
            for (int j = 1; j <= qtdGavetas; j++) {
                Registro armario = contextoAcao.novaLinha("AD_ARMARIO");
                armario.setCampo("CODPARC",codParc);
                armario.setCampo("CODARM", codArm + i);
                armario.setCampo("GAVETA", j);
                armario.setCampo("PRARM", prefixo);
                armario.setCampo("EMUSO", "N");
                armario.save();
            }
        }

        StringBuffer mensagem = new StringBuffer();
        mensagem.append("Foram incluídos ");
        mensagem.append(qtdArmarios);
        mensagem.append(" armário(s) com ");
        mensagem.append(qtdGavetas);
        mensagem.append(" gavetas cada para o Parceiro ");
        mensagem.append(codParc);

        contextoAcao.setMensagemRetorno(mensagem.toString());
    }
}
