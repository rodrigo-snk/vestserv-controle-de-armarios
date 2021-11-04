package br.com.sankhya.vsl.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.vsl.dao.Armario;

public class PossuiArmario implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        if (persistenceEvent.getModifingFields().isModifing("MATRICULA")) {
            Object matricula = persistenceEvent.getModifingFields().getOldValue("MATRICULA");
            Object codParc = persistenceEvent.getModifingFields().getOldValue("CODPARC");

            //Verifica se funcionário já possui armário
           // if (Armario.getArmarioByPK(matricula,codParc) != null) throw new MGEModelException("Funcionário já possui armário.");
        }
    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        if (persistenceEvent.getModifingFields().isModifing("MATRICULA")) {
            Object matricula = persistenceEvent.getModifingFields().getOldValue("MATRICULA");
            Object codParc = persistenceEvent.getModifingFields().getOldValue("CODPARC");

            //Verifica se funcionário já possui armário
            //if (Armario.getArmarioByPK(matricula,codParc) != null) throw new MGEModelException("Funcionário já possui armário.");
        }

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
