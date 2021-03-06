package br.com.sankhya.vsl.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.vsl.dao.Parceiro;

public class DesligamentoFuncionario implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        boolean utilizaArmarioGaveta = Parceiro.utilizaArmarioGaveta(persistenceEvent.getModifingFields().getOldValue("CODPARC"));
        // Este evento impede o usuário de inativar um Funcionário caso
        // o Parceiro utilize armário/gaveta (TGFPAR.AD_UTLARMGV = 'S')
        if (persistenceEvent.getModifingFields().isModifing("ATIVO") && utilizaArmarioGaveta) {
            if (persistenceEvent.getModifingFields().getNewValue("ATIVO").toString().equalsIgnoreCase("N")){
                throw new Exception("Para parceiro que utiliza armário/gaveta, inative o funcionário pelo botão.");
            }
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
