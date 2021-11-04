package br.com.sankhya.vsl.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.vsl.dao.Armario;
import br.com.sankhya.vsl.dao.Parceiro;

public class AdicionaArmario implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {

        Object codParc = ctx.getParam("CODPARC");
        int qtdArmarios = (int) ctx.getParam("QTDARM");
        int qtdGavetas = (int) ctx.getParam("QTDGAVETAS");
        String prefixo = (String) ctx.getParam("PRARM");
        int codArm = Armario.ultimoArmario(codParc);

        for (int i = 1; i <= qtdArmarios; i++) {
            for (int j = 1; j <= qtdGavetas; j++) {
                Registro armario = ctx.novaLinha("AD_ARMARIO");
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
        mensagem.append(" gavetas cada para ");
        mensagem.append(Parceiro.getNomeParc(codParc));

        ctx.setMensagemRetorno(mensagem.toString());
    }
}
