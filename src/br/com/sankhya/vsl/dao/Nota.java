package br.com.sankhya.vsl.dao;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.util.troubleshooting.SKError;
import br.com.sankhya.util.troubleshooting.TSLevel;
import com.sankhya.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class Nota {

    public static CabecalhoNotaVO lancaCabecalhoNota(Object codParc, Object codCenCus, Object numNota, Timestamp dtNeg) throws Exception {
        JdbcWrapper jdbc = null;
        CabecalhoNotaVO notaVO;

        try {
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            jdbc = dwfFacade.getJdbcWrapper();
            jdbc.openSession();

            notaVO = (CabecalhoNotaVO) dwfFacade.getDefaultValueObjectInstance("CabecalhoNota", CabecalhoNotaVO.class);
            notaVO.setCODEMP(BigDecimal.ONE);
            notaVO.setCODPARC((BigDecimal) codParc);
            notaVO.setCODTIPOPER(BigDecimal.valueOf(600)); // TOP 600
            notaVO.setCODTIPVENDA(BigDecimal.valueOf(16));
            try {
                notaVO.setNUMNOTA(BigDecimalUtil.valueOf(numNota.toString()));
            } catch (Exception e) {
                throw (IllegalArgumentException) SKError.registry(TSLevel.ERROR, "CORE_E02656", new IllegalArgumentException("Impossível converter o valor: " + numNota + " para um tipo numérico."));
            }
            if (dtNeg == null) dtNeg = Timestamp.valueOf(LocalDateTime.now());
            notaVO.setDTNEG(dtNeg);
            notaVO.setDTENTSAI(dtNeg);
            notaVO.setProperty("NUMPEDIDO2", numNota);
            notaVO.setProperty("AD_CODCENCUS", codCenCus);

            /*notaVO.setProperty("AD_CODCENCUS", codCenCus);
            notaVO.setProperty("CODEMP", BigDecimal.ONE);
            notaVO.setProperty("CODPARC", codParc);
            notaVO.setProperty("CODTIPOPER", BigDecimal.valueOf(600));
            notaVO.setProperty("CODTIPVENDA", BigDecimal.valueOf(16));
            notaVO.setProperty("NUMNOTA", numNota);
            notaVO.setProperty("DTNEG", Timestamp.valueOf(LocalDateTime.now()));
            notaVO = (DynamicVO) dwfFacade.createEntity("CabecalhoNota", (EntityVO)notaVO);*/

            dwfFacade.createEntity("CabecalhoNota", notaVO);

        } finally {
            if (jdbc != null) jdbc.closeSession();
        }
        return notaVO;
    }

    public static ItemNotaVO montaItemNota(CabecalhoNotaVO notaVO, Object codProd, Object qtdNeg, Object matricula, Object codParc) throws Exception {

        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();

        ItemNotaVO itemVO = (ItemNotaVO) dwfFacade.getDefaultValueObjectInstance("ItemNota", ItemNotaVO.class);
        itemVO.setNUNOTA(notaVO.getNUNOTA());
        itemVO.setCODPROD((BigDecimal) codProd);
        itemVO.setCODEMP( BigDecimal.ONE);
        itemVO.setQTDNEG((BigDecimal) qtdNeg);
        itemVO.setCODVOL("UN");
        itemVO.setCONTROLE(" ");
        itemVO.setUSOPROD("R");
        itemVO.setRESERVA("N");
        itemVO.setCODLOCALORIG(BigDecimal.valueOf(101));
        itemVO.setATUALESTOQUE(BigDecimal.ZERO);
        itemVO.setProperty("AD_CODPARC", codParc);
        itemVO.setProperty("AD_MATRICULA", matricula);
        itemVO.setProperty("AD_NOMEFUNC", Funcionario.getNome(codParc, matricula));

        jdbc.closeSession();

        return itemVO;
    }



}
