package net.ausiasmarch.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.ausiasmarch.bean.FacturaBean;
import net.ausiasmarch.bean.ResponseBean;
import net.ausiasmarch.connection.ConnectionInterface;
import net.ausiasmarch.dao.FacturaDao;
import net.ausiasmarch.factory.ConnectionFactory;
import net.ausiasmarch.factory.GsonFactory;
import net.ausiasmarch.setting.ConnectionSettings;

public class FacturaService implements ServiceInterface {

    HttpServletRequest oRequest = null;

    public FacturaService(HttpServletRequest oRequest) {
        this.oRequest = oRequest;
    }

    @Override
    public String get() throws Exception {
        ConnectionInterface oConnectionImplementation = null;
        Connection oConnection = null;
        try {
            oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            oConnection = oConnectionImplementation.newConnection();
            int id = Integer.parseInt(oRequest.getParameter("id"));
            FacturaDao oFacturaDao = new FacturaDao(oConnection);
            FacturaBean oFacturaBean = oFacturaDao.get(id);
            Gson oGson = GsonFactory.getGson();
            String strJson = oGson.toJson(oFacturaBean);
            return "{\"status\":200,\"message\":" + strJson + "}";
        } catch (Exception ex) {
            String msg = this.getClass().getName() + " get method ";
            throw new Exception(msg, ex);
        } finally {
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        }
    }

    @Override
    public String getPage() throws Exception {
        ConnectionInterface oConnectionImplementation = null;
        Connection oConnection = null;
        try {
            oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            oConnection = oConnectionImplementation.newConnection();
            int iRpp = Integer.parseInt(oRequest.getParameter("rpp"));
            int iPage = Integer.parseInt(oRequest.getParameter("page"));
            List<String> orden = null;
            if (oRequest.getParameter("order") != null) {
                orden = Arrays.asList(oRequest.getParameter("order").split("\\s*,\\s*"));
            }
            FacturaDao oFacturaDao = new FacturaDao(oConnection);
            ArrayList alFacturaBean = oFacturaDao.getPage(iPage, iRpp, orden);
            Gson oGson = GsonFactory.getGson();
            String strJson = oGson.toJson(alFacturaBean);
            return "{\"status\":200,\"message\":" + strJson + "}";
        } catch (Exception ex) {
            String msg = this.getClass().getName() + " get method ";
            throw new Exception(msg, ex);
        } finally {
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        }
    }

    @Override
    public String getCount() throws Exception {
        ConnectionInterface oConnectionImplementation = null;
        Connection oConnection = null;
        try {
            oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            oConnection = oConnectionImplementation.newConnection();
            ResponseBean oResponseBean;
            Gson oGson = GsonFactory.getGson();
            FacturaDao oFacturaDao = new FacturaDao(oConnection);
            Integer iCount = oFacturaDao.getCount();
            if (iCount < 0) {
                oResponseBean = new ResponseBean(500, iCount.toString());
            } else {
                oResponseBean = new ResponseBean(200, iCount.toString());
            }
            return oGson.toJson(oResponseBean);
        } catch (Exception ex) {
            String msg = this.getClass().getName() + " get method ";
            throw new Exception(msg, ex);
        } finally {
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        }
    }

    @Override
    public String update() throws Exception {
        HttpSession oSession = oRequest.getSession();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();
        if (oSession.getAttribute("usuario") != null) {
            ConnectionInterface oConnectionImplementation = null;
            Connection oConnection = null;
            try {
                oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
                oConnection = oConnectionImplementation.newConnection();
                FacturaBean oFacturaBean = new FacturaBean();
                String data = oRequest.getParameter("data");
                oFacturaBean = oGson.fromJson(data, FacturaBean.class);
                FacturaDao oFacturaDao=  new FacturaDao(oConnection);
                if (oFacturaDao.update(oFacturaBean) == 0) {
                    oResponseBean = new ResponseBean(500, "KO");
                } else {
                    oResponseBean = new ResponseBean(200, "OK");
                }
                return oGson.toJson(oResponseBean);
            } catch (Exception ex) {
                String msg = this.getClass().getName() + " get method ";
                throw new Exception(msg, ex);
            } finally {
                if (oConnection != null) {
                    oConnection.close();
                }
                if (oConnectionImplementation != null) {
                    oConnectionImplementation.disposeConnection();
                }
            }
        } else {
            oResponseBean = new ResponseBean(401, "Error: No session");
            return oGson.toJson(oResponseBean);
        }

    }

    @Override
    public String insert() throws Exception {
        HttpSession oSession = oRequest.getSession();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();
        if (oSession.getAttribute("usuario") != null) {
            ConnectionInterface oConnectionImplementation = null;
            Connection oConnection = null;
            try {
                oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
                oConnection = oConnectionImplementation.newConnection();
                final GsonBuilder builder = new GsonBuilder();
                builder.excludeFieldsWithoutExposeAnnotation();
                FacturaBean oFacturaBean = oGson.fromJson(oRequest.getParameter("data"), FacturaBean.class);
                FacturaDao oFacturaDao = new FacturaDao(oConnection);
                if (oFacturaDao.insert(oFacturaBean) == 0) {
                    oResponseBean = new ResponseBean(500, "KO");
                } else {
                    oResponseBean = new ResponseBean(200, "OK");
                };
                return oGson.toJson(oResponseBean);
            } catch (Exception ex) {
                String msg = this.getClass().getName() + " get method ";
                throw new Exception(msg, ex);
            } finally {
                if (oConnection != null) {
                    oConnection.close();
                }
                if (oConnectionImplementation != null) {
                    oConnectionImplementation.disposeConnection();
                }
            }
        } else {
            oResponseBean = new ResponseBean(401, "Error: No session");
            return oGson.toJson(oResponseBean);
        }
    }

    @Override
    public String remove() throws Exception {
        HttpSession oSession = oRequest.getSession();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();
        if (oSession.getAttribute("usuario") != null) {
            ConnectionInterface oConnectionImplementation = null;
            Connection oConnection = null;
            try {
                oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
                oConnection = oConnectionImplementation.newConnection();
                FacturaDao oFacturaDao = new FacturaDao(oConnection);
                int id = Integer.parseInt(oRequest.getParameter("id"));
                if (oFacturaDao.remove(id) == 0) {
                    oResponseBean = new ResponseBean(500, "KO");
                } else {
                    oResponseBean = new ResponseBean(200, "OK");
                }
                return oGson.toJson(oResponseBean);
            } catch (Exception ex) {
                String msg = this.getClass().getName() + " get method ";
                throw new Exception(msg, ex);
            } finally {
                if (oConnection != null) {
                    oConnection.close();
                }
                if (oConnectionImplementation != null) {
                    oConnectionImplementation.disposeConnection();
                }
            }
        } else {
            oResponseBean = new ResponseBean(401, "Error: No session");
            return oGson.toJson(oResponseBean);
        }
    }
    
        public String fill() throws Exception {
        ConnectionInterface oConnectionImplementation = ConnectionFactory
                .getConnection(ConnectionSettings.connectionPool);
        Connection oConnection = oConnectionImplementation.newConnection();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();

        FacturaDao oFacturaDao = new FacturaDao(oConnection);
        FacturaBean oFacturaBean = new FacturaBean();
        Integer number = Integer.parseInt(oRequest.getParameter("number"));
        Date date1 = new GregorianCalendar(2014, Calendar.JANUARY, 1).getTime();
        Date date2 = new GregorianCalendar(2019, Calendar.DECEMBER, 31).getTime();
        int iva = 21;
        for (int i = 0; i < number; i++) {
            int tipoFacturaUsuario = usuarioIdFacturaRandom();
            Date randomDate = new Date(ThreadLocalRandom.current()
                            .nextLong(date1.getTime(), date2.getTime()));
            oFacturaBean.setIva(iva);
            oFacturaBean.setFecha(randomDate);
            oFacturaBean.setUsuario_id(tipoFacturaUsuario);
            oFacturaDao.insert(oFacturaBean);
        }
        oResponseBean = new ResponseBean(200, "Se ha añadido correctamente.");
        if (oConnection != null) {
            oConnection.close();
        }
        if (oConnectionImplementation != null) {
            oConnectionImplementation.disposeConnection();
        }

        return oGson.toJson(oResponseBean);
    }
    
    private int usuarioIdFacturaRandom() {
        return (int) (Math.random() * (1 - 25));
    }
}
