package br.com.sankhya.vsl.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.vsl.dao.Armario;
import br.com.sankhya.vsl.dao.Parceiro;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class VincularArmario implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {
        Object matricula;
        Object codParc;
        boolean vinculouArmario = false;
        List<DynamicVO> armariosDisponiveis;

        Registro[] linhas = ctx.getLinhas();

        for (Registro linha : linhas) {
            matricula = linha.getCampo("MATRICULA");
            codParc = linha.getCampo("CODPARC");

            //Verificação se funcionário já possui armário
            if (Armario.buscaArmario(matricula,codParc) != null) ctx.mostraErro("Funcionário já possui armário.");

            // Armários disponíveis para o Parceiro = codParc
            armariosDisponiveis = Armario.armariosDisponiveis(codParc);

            for (DynamicVO armarioVO: armariosDisponiveis) {
                Object codArm = armarioVO.asBigDecimal("CODARM");
                Object gaveta = armarioVO.asBigDecimal("GAVETA");
                codParc = armarioVO.asBigDecimal("CODPARC");

                //Timestamp da liberação do armário na hora 00:00:00.0
                Timestamp dtLib = Armario.dataLiberacao(codArm, gaveta, codParc);

                //Se a data de liberação do armário for anterior a data/hora atual, vincula o armário e sai do loop
                if (dtLib.before(Timestamp.valueOf(LocalDateTime.now()))) {
                    Armario.atualizarDono(codArm, gaveta, codParc, matricula);
                    ctx.setMensagemRetorno("Armario " +codArm.toString()+ " gaveta " +gaveta.toString()+ " Parceiro " +codParc.toString()+ " matricula " +matricula.toString());
                    vinculouArmario = true;
                    break;
                }

            }
            // Se não tiver vinculado o armário ao funcionário, exibe mensagem de armários indisponíveis.
            if (!vinculouArmario) ctx.setMensagemRetorno("Não há armários disponíveis para " + Parceiro.getNomeParc(codParc)+ ".");


            //ctx.setMensagemRetorno("DEU RUIM Parceiro " +codParc.toString()+ " matricula " +matricula.toString()+   " armariosdisponiveis " +armariosDisponiveis.toString());




        }

    }
}
