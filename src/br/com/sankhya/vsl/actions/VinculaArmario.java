package br.com.sankhya.vsl.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.vsl.dao.Armario;
import br.com.sankhya.vsl.dao.Funcionario;
import br.com.sankhya.vsl.dao.Parceiro;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class VinculaArmario implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao ctx) throws Exception {
        Object matricula = null;
        Object codParc = null;
        int vinculouArmario = 0;
        String msgRetorno = "";
        List<DynamicVO> armariosForaDeUso;
        Registro[] linhas = ctx.getLinhas();
        Map<BigDecimal,String> mensagens = new HashMap<>();
        Set<BigDecimal> avisouIndisponibilidade = new HashSet<>();
        int i = 0;
        if (linhas.length > 1) ctx.confirmar("Informação", "Você selecionou " +linhas.length+" funcionários para vincular armário/gaveta. Deseja continuar?", i++);


        for (Registro linha : linhas) {
            matricula = linha.getCampo("MATRICULA");
            codParc = linha.getCampo("CODPARC");
            boolean parceiroTemArmariosDisponiveis = Armario.temArmariosDisponiveis(codParc, TimeUtils.getNow()) > 0;

            if (parceiroTemArmariosDisponiveis) {
                //Verificação se funcionário já possui armário
                if (Armario.possuiArmario(codParc,matricula) && !mensagens.containsKey((BigDecimal) codParc)) {
                    ctx.confirmar("Aviso", Funcionario.getNome(codParc,matricula) +" já possui armário. Deseja prosseguir?", i++);

                } else {

                    // Armários disponíveis para o Parceiro = codParc
                    armariosForaDeUso = Armario.armariosForaDeUso(codParc);

                    for (DynamicVO armarioVO: armariosForaDeUso) {
                        Object codArm = armarioVO.asBigDecimal("CODARM");
                        Object gaveta = armarioVO.asBigDecimal("GAVETA");
                        codParc = armarioVO.asBigDecimal("CODPARC");

                        //Timestamp da liberação do armário na hora 00:00:00.0
                        Timestamp dtLib = Armario.dataLiberacao(codArm, gaveta, codParc);

                        //Se a data de liberação do armário for anterior a data/hora atual, vincula o armário e sai do loop
                        if (dtLib.before(TimeUtils.getNow())){
                            Armario.atualizaDono(codArm, gaveta, codParc, matricula);
                            //Monta mensagem de retorno
                            if (!mensagens.containsKey(codParc)) mensagens.put((BigDecimal) codParc, Parceiro.getNomeParc(codParc).concat("\n"));
                            msgRetorno = mensagens.get(codParc).concat("AR: "+codArm+ " GV: "+gaveta+" foi vinculado à " + Funcionario.getNome(codParc,matricula) + " ("+Funcionario.getPrefixMatricula(codParc, matricula)+")\n");
                            mensagens.put((BigDecimal) codParc, msgRetorno);
                            vinculouArmario++;
                            break;
                        }
                    }
                }

            } else if (mensagens.containsKey((BigDecimal) codParc)){
                msgRetorno = mensagens.get(codParc).concat("Nenhum armário/gaveta foi vinculado à " + Funcionario.getNome(codParc,matricula) + " ("+Funcionario.getPrefixMatricula(codParc, matricula)+")\n");
                mensagens.put((BigDecimal) codParc, msgRetorno);
                ctx.confirmar("Aviso", "Não há armários disponíveis para " + Funcionario.getNome(codParc, matricula)+ ". \n Deseja continuar?", i++);
            } else if (!avisouIndisponibilidade.contains((BigDecimal) codParc)){
                avisouIndisponibilidade.add((BigDecimal) codParc);
                ctx.confirmar("Aviso", "Não há armários disponíveis para " + Parceiro.getNomeParc(codParc)+ ". \n Deseja continuar?", i++);
            }


            
        }
        // Se não tiver vinculado o armário ao funcionário, exibe mensagem de armários indisponíveis.
        if (vinculouArmario > 0) {
            for (String msg: mensagens.values()) {
                msgRetorno.concat(msg);
            }
            ctx.setMensagemRetorno(msgRetorno);
        } else {
            ctx.setMensagemRetorno("Nenhum armário/gaveta foi vinculado.");
        }

    }
}
