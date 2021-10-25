package br.com.sankhya.vsl.dao;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Armario {

    /**
     * Método que consulta o último armário cadastrado para o Parceiro
     * @param codParc
     * @return int
     * @throws MGEModelException
     */

    public static int ultimoArmario(Object codParc) throws MGEModelException {
        JdbcWrapper jdbc = null;
        NativeSql sql = null;
        ResultSet rset;
        JapeSession.SessionHandle hnd = null;
        int ultArmario = 0;

        try {
            hnd = JapeSession.open();
            EntityFacade entity = EntityFacadeFactory.getDWFFacade();
            jdbc = entity.getJdbcWrapper();
            jdbc.openSession();

            sql = new NativeSql(jdbc);
            sql.appendSql("SELECT NVL(MAX(CODARM),0) ULTARM FROM AD_ARMARIO WHERE CODPARC = :CODPARC");
            sql.setNamedParameter("CODPARC", codParc);

            rset = sql.executeQuery();
            while (rset.next()) {
                ultArmario = rset.getInt("ULTARM");
            }
        }catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            //JdbcUtils.closeResultSet(rset);
            NativeSql.releaseResources(sql);
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
        }
        
        return ultArmario;

    }

    public static DynamicVO buscaArmario(Object matricula, Object codParc) throws MGEModelException {

        DynamicVO armarioVO = null;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper armarioDAO = JapeFactory.dao("AD_ARMARIO");
            armarioVO = armarioDAO.findOne(" CODPARC = ? AND MATRICULA = ?", codParc, matricula);
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }

        return armarioVO;
    }

    public static List<DynamicVO> armariosDisponiveis(Object codParc) throws MGEModelException {

        List<DynamicVO> armarios = new ArrayList<>();

        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper armarioDAO = JapeFactory.dao("AD_ARMARIO");
            //Collection<DynamicVO> dynamicVOs = armarioDAO.find("EMUSO = 'N' AND CODPARC = ? ORDER BY CODARM, GAVETA", codParc.toString());
            Collection<DynamicVO> dynamicVOs = armarioDAO.find("EMUSO = 'N' AND CODPARC = ?", codParc);
            armarios.addAll(dynamicVOs);
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
        return armarios;
    }

    //Retorna a VO do armario pela PK
    public static DynamicVO retornaArmario(Object codArm, Object gaveta) throws MGEModelException {
        DynamicVO armarioVO = null;

        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper armarioDAO = JapeFactory.dao("AD_ARMARIO");
            armarioVO = armarioDAO.findByPK(codArm, gaveta);
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }

        return armarioVO;
    }
    public static boolean isLiberado(Object codArm, Object gaveta) throws MGEModelException {

        DynamicVO armarioVO = retornaArmario(codArm, gaveta);
        return armarioVO.asString("EMUSO").equals("S");
    }

    public static void atualizarDono(Object codArm, Object gaveta, Object codParc, Object matricula) throws MGEModelException {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeFactory.dao("AD_ARMARIO").
                    prepareToUpdateByPK(codArm, gaveta, codParc)
                    .set("MATRICULA", matricula)
                    .set("EMUSO", "S")
                    .set("DTINI", Timestamp.valueOf(LocalDateTime.now()))
                    .update();
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
    }

    public static Timestamp dataLiberacao(Object codArm, Object gaveta, Object codParc) throws MGEModelException {
        Timestamp dtLib = null;

        JdbcWrapper jdbc = null;
        NativeSql sql = null;
        JapeSession.SessionHandle hnd = null;

        try {
            hnd = JapeSession.open();
            hnd.setFindersMaxRows(-1);
            EntityFacade entity = EntityFacadeFactory.getDWFFacade();
            jdbc = entity.getJdbcWrapper();
            jdbc.openSession();

            sql = new NativeSql(jdbc);

            sql.appendSql("SELECT MAX(HIS.dtdev) + PAR.AD_RSVARM DTLIB\n" +
                    "FROM AD_HISTORICO_ARMARIO HIS \n" +
                    "JOIN TGFPAR PAR ON HIS.CODPARC = PAR.CODPARC\n" +
                    "WHERE CODARM = :CODARM AND GAVETA = :GAVETA AND PAR.CODPARC = :CODPARC\n" +
                    "GROUP BY CODARM, GAVETA, HIS.CODPARC, PAR.AD_RSVARM");
            sql.setNamedParameter("CODARM", codArm);
            sql.setNamedParameter("GAVETA", gaveta);
            sql.setNamedParameter("CODPARC", codParc);

            ResultSet rs = sql.executeQuery();

            if (rs.next()) dtLib = rs.getTimestamp("DTLIB");

        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            //JdbcUtils.closeResultSet(rset);
            NativeSql.releaseResources(sql);
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
        }

        // VERIFICAR PARA RETORNAR DATA ATUAL
        if (dtLib == null) dtLib = Timestamp.valueOf(LocalDateTime.now().with(LocalTime.MIN));

        return dtLib;
    }
    private static void stringToTimestamp(String dateString) {
        String dateFormatPattern = "yyyy-MM-dd hh:mm:ss.SSS";
        //String dateString = dateString;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
            Date parsedDate = dateFormat.parse(dateString);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            System.out.println(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    }
