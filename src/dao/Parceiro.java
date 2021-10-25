package dao;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

import java.math.BigDecimal;

public class Parceiro {

    public static DynamicVO getParceiro(Object codParc) throws MGEModelException {
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
        BigDecimal dias = null;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper empresaDAO = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
            DynamicVO empresa = empresaDAO.findByPK(codParc);
            dias = empresa.asBigDecimalOrZero("AD_RSVARM");
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }

        return dias;
    }

    public static String getNomeParc(Object codParc) throws MGEModelException {
        return getParceiro(codParc).asString("NOMEPARC");
    }
}
