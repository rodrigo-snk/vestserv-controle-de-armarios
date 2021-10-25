package br.com.sankhya.vsl.dao;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.mgecomercial.model.facades.helpper.ItemNotaHelpper;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class Nota {

    public static CabecalhoNotaVO lancarCabecalhoNota(Object codParc, Object codCencus, Object numNota) throws Exception {
        JdbcWrapper jdbc = null;
        DynamicVO notaVO;

        try {
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            jdbc = dwfFacade.getJdbcWrapper();
            jdbc.openSession();

            notaVO = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("CabecalhoNota");
            notaVO.setProperty("CODEMP", BigDecimal.ONE);
            notaVO.setProperty("CODPARC", codParc);
            notaVO.setProperty("CODTIPOPER", BigDecimal.valueOf(600));
            notaVO.setProperty("CODTIPVENDA", BigDecimal.valueOf(16));
            notaVO.setProperty("AD_CODCENCUS", codCencus);
            notaVO.setProperty("NUMNOTA", numNota);
            notaVO.setProperty("NUMPEDIDO2", numNota.toString());
            notaVO.setProperty("DTNEG", Timestamp.valueOf(LocalDateTime.now()));

            notaVO = (DynamicVO) dwfFacade.createEntity("CabecalhoNota", (EntityVO)notaVO);

        } finally {
            if (jdbc != null) jdbc.closeSession();

        }

        return (CabecalhoNotaVO) notaVO;
    }

    public static void lancarItemNota(CabecalhoNotaVO notaVO, Object codProd, Object qtdNeg, Object matricula) throws Exception {

        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();

        ItemNotaVO itemVO = (ItemNotaVO) dwfFacade.getDefaultValueObjectInstance("ItemNota");
        itemVO.setNUNOTA(notaVO.getNUNOTA());
        itemVO.setCODPROD((BigDecimal) codProd);
        itemVO.setCODEMP( BigDecimal.ONE);
        itemVO.setProperty("AD_MATRICULA", matricula);
        itemVO.setQTDNEG((BigDecimal) qtdNeg);
        itemVO.setCODVOL("UN");
        itemVO.setCONTROLE("  ");
        itemVO.setUSOPROD("R");
        itemVO.setRESERVA("N");
        itemVO.setCODLOCALORIG(BigDecimal.valueOf(101));
        itemVO.setATUALESTOQUE(BigDecimal.ZERO);

        Collection<ItemNotaVO> itens = new ArrayList<>();
        itens.add(itemVO);

        ItemNotaHelpper.saveItensNota(itens,notaVO);


        jdbc.closeSession();

    }


}
