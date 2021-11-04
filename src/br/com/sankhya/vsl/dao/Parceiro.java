package br.com.sankhya.vsl.dao;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

import java.math.BigDecimal;

public class Parceiro {

    public static DynamicVO getParceiroByPK(Object codParc) throws MGEModelException {
        DynamicVO parceiroVO = null;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper parceiroDAO = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
            parceiroVO = parceiroDAO.findByPK(codParc);
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
        return parceiroVO;
    }

    public static BigDecimal diasReserva(Object codParc) throws MGEModelException {
        return getParceiroByPK(codParc).asBigDecimalOrZero("AD_RSVARM");
    }

    public static String getNomeParc(Object codParc) throws MGEModelException {
        return getParceiroByPK(codParc).asString("NOMEPARC");
    }

    public static boolean utilizaArmarioGaveta(Object codParc) throws MGEModelException {
        return Parceiro.getParceiroByPK(codParc).asString("AD_UTLARGV").equalsIgnoreCase("S");
    }

    public static boolean naoControlaArea(Object codParc) throws MGEModelException {
        return getParceiroByPK(codParc).asString("AD_NCTRLAREA") != null && getParceiroByPK(codParc).asString("AD_NCTRLAREA").equalsIgnoreCase("S");
    }
}
