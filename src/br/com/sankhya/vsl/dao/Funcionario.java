package br.com.sankhya.vsl.dao;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

import java.math.BigDecimal;

public class Funcionario {

    public static DynamicVO getFuncionarioByPK(Object codParc, Object matricula) throws MGEModelException {
        DynamicVO funcionarioVO = null;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper funcionarioDAO = JapeFactory.dao("FUNCIONARIO");
            funcionarioVO = funcionarioDAO.findByPK(codParc, matricula);
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
        return funcionarioVO;
    }

    public static BigDecimal getCodCenCus(Object codParc, Object matricula) throws MGEModelException {
        return getFuncionarioByPK(codParc, matricula).asBigDecimalOrZero("CODCENCUS");
    }

    public static String getNome(Object codParc, Object matricula) throws MGEModelException {
        return getFuncionarioByPK(codParc, matricula).asString("NOMEFUNC");
    }

    public static void inativaFuncionario(Object codParc, Object matricula, Object motivo) throws MGEModelException {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();

            JapeFactory.dao("FUNCIONARIO").
                    prepareToUpdateByPK(codParc, matricula)
                    .set("MOTIVO", motivo)
                    .set("ATIVO", "N")
                    .update();

        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
    }
}
