package br.com.sankhya.vsl.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.vsl.dao.Armario;

public class PossuiArmario implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO armarioVO = (DynamicVO) persistenceEvent.getVo();
        Object matricula = armarioVO.asBigDecimalOrZero("MATRICULA");
        Object codParc = armarioVO.asBigDecimalOrZero("CODPARC");

            //Verifica se funcionário já possui armário
            if (Armario.possuiArmario(codParc,matricula)) throw new MGEModelException("Funcionário já possui armário.");
    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        if (persistenceEvent.getModifingFields().isModifing("MATRICULA")) {
            Object matricula = persistenceEvent.getModifingFields().getNewValue("MATRICULA");
            Object matriculaOld = persistenceEvent.getModifingFields().getOldValue("MATRICULA");
            Object codParc = persistenceEvent.getModifingFields().getOldValue("CODPARC");

            //Verifica se funcionário já possui armário
            if (matricula != null) {
                if (Armario.possuiArmario(codParc,matricula)) throw new MGEModelException("Funcionário já possui armário.");
            }
            if (matriculaOld != null) throw new MGEModelException("Não é possível alterar matrícula sem desvincular o armário/gaveta.");

        }

        /*if (persistenceEvent.getModifingFields().isModifing("EMUSO")) {

            boolean desativando = persistenceEvent.getModifingFields().getNewValue("EMUSO").toString().equalsIgnoreCase("N");

            //if (desativando) throw new MGEModelException("");

        }*/

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
