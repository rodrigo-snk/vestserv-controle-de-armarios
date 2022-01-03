package br.com.sankhya.vsl.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.data.vo.RelatorioVO;
import br.com.sankhya.modelcore.facades.VisualizadorRelatoriosSP;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class GeraRelatorio implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        JdbcWrapper jdbcWrapper = null;
        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        jdbcWrapper = dwfFacade.getJdbcWrapper();
        RelatorioVO relatorioVO = (RelatorioVO) dwfFacade.findEntityByPrimaryKeyAsVO(DynamicEntityNames.RELATORIO, 188);
        /*VisualizadorRelatoriosSP.
        VisualizadorArquivosServlet visualizadorArquivosServlet = new VisualizadorArquivosServlet();
        visualizadorArquivosServlet.doGet();*/
        jdbcWrapper.openSession();
        jdbcWrapper.closeSession();
    }
}
