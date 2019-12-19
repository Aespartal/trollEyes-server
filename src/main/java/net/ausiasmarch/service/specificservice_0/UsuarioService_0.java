/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ausiasmarch.service.specificservice_0;

import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.ausiasmarch.bean.ResponseBean;
import net.ausiasmarch.bean.UsuarioBean;
import net.ausiasmarch.connection.ConnectionInterface;
import net.ausiasmarch.dao.specificdao_0.UsuarioDao_0;
import net.ausiasmarch.exceptions.MyException;
import net.ausiasmarch.factory.ConnectionFactory;
import net.ausiasmarch.factory.GsonFactory;
import net.ausiasmarch.helper.Log4jHelper;
import net.ausiasmarch.service.genericservice.GenericService;
import net.ausiasmarch.service.serviceinterface.ServiceInterface;
import net.ausiasmarch.setting.ConnectionSettings;

/**
 *
 * @author alejandro
 */
public class UsuarioService_0 extends GenericService implements ServiceInterface {

    ResponseBean oResponseBean = null;
    UsuarioBean oUsuarioBeanSession;
    ConnectionInterface oConnectionImplementation = null;
    Connection oConnection = null;
    Gson oGson = GsonFactory.getGson();
    HttpSession oSession = oRequest.getSession();

    public UsuarioService_0(HttpServletRequest oRequest) {
        super(oRequest);
        ob = oRequest.getParameter("ob");
    }

    public String login() throws Exception {
        try {
            if (oRequest.getParameter("token") == null || oRequest.getParameter("token").equalsIgnoreCase("")){
                oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
                oConnection = oConnectionImplementation.newConnection();
                UsuarioDao_0 oUsuarioDao = new UsuarioDao_0(oConnection, "usuario", oUsuarioBeanSession);
                UsuarioBean oUsuarioBean;
                String login = oRequest.getParameter("username");
                String password = oRequest.getParameter("password");
                oUsuarioBean = oUsuarioDao.get(login, password);

                if (oUsuarioBean != null) {
                    if (oRequest.getParameter("username").equals(oUsuarioBean.getLogin()) && oRequest.getParameter("password").equalsIgnoreCase(oUsuarioBean.getPassword())) {
                        oSession.setAttribute("usuario", oUsuarioBean);
                        oResponseBean = new ResponseBean(200, "Welcome");
                    } else {
                        oResponseBean = new ResponseBean(500, "Wrong password");
                    }
                } else {
                    oResponseBean = new ResponseBean(500, "Wrong password");
                }
            }else {
                String token = oRequest.getParameter("token");
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                        .setAudience(Collections.singletonList("797685668265-d6m9g3nr3f0l996p0sjmeo4riaicitks.apps.googleusercontent.com")).build();
                GoogleIdToken idToken = verifier.verify(token);
                if(idToken != null) {
                    Payload payload = idToken.getPayload();
                    //userId es el id de usuario que tratar
                    String userId = payload.getSubject();
                    oSession.setAttribute("usuario", userId);
                    oResponseBean = new ResponseBean(200, "Welcome");
                } else {
                    oResponseBean = new ResponseBean(500, "Invalid Google User");
                }
            }
            return oGson.toJson(oResponseBean);
        } catch (MyException ex) {
            String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName() + " ob:" + ob;
            Log4jHelper.errorLog(msg, ex);
            ex.addDescripcion(msg);
            throw ex;
        } finally {
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        }
    }

    public String logout() {
        oRequest.getSession().invalidate();
        oResponseBean = new ResponseBean(200, "No active session");
        return oGson.toJson(oResponseBean);
    }

    public String check() throws Exception {
        try {
            oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            oConnection = oConnectionImplementation.newConnection();
            UsuarioBean oUsuarioBean;
            oUsuarioBean = (UsuarioBean) oSession.getAttribute("usuario");
            UsuarioDao_0 oUsuarioDao = new UsuarioDao_0(oConnection, "usuario", oUsuarioBeanSession);

            if (oUsuarioBean == null) {
                oResponseBean = new ResponseBean(500, "No autorizado");
            } else {
                oUsuarioBean = oUsuarioDao.get(oUsuarioBean.getLogin());
                return "{\"status\":200,\"message\":" + oGson.toJson(oUsuarioBean) + "}";
            }

        } catch (MyException ex) {
            String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName() + " ob:" + ob;
            Log4jHelper.errorLog(msg, ex);
            ex.addDescripcion(msg);
            throw ex;
        } finally {
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        }
        return oGson.toJson(oResponseBean);
    }

    public String signup() throws Exception {
        try {
            oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            oConnection = oConnectionImplementation.newConnection();
            UsuarioDao_0 oUsuarioDao = new UsuarioDao_0(oConnection, "usuario", oUsuarioBeanSession);
            UsuarioBean oUsuarioBean = new UsuarioBean();
            oUsuarioBean.setDni(oRequest.getParameter("dni"));
            oUsuarioBean.setNombre(oRequest.getParameter("nombre"));
            oUsuarioBean.setApellido1(oRequest.getParameter("apellido1"));
            oUsuarioBean.setApellido2(oRequest.getParameter("apellido2"));
            oUsuarioBean.setLogin(oRequest.getParameter("username"));
            oUsuarioBean.setPassword(oRequest.getParameter("password"));
            oUsuarioBean.setEmail(oRequest.getParameter("email"));
            oUsuarioBean.setToken(generaToken());
            oUsuarioBean.setValidate(Boolean.FALSE);
            String token = oUsuarioBean.getToken();
            String email = oRequest.getParameter("email");
            String username = oRequest.getParameter("username");
            String emailText = "Hola, " + username + "\n Hemos recibido su solicitud para acceder a TrollEyes. Si has sido tú, accede al siguienete enlace para la confirmación: http://http://localhost:8081/trolleyes/" + token + "\n Si no has solicitado el registro, puedes ignorar este correo.";
            EmailRegister.sendEmail("trolleyesclient@gmail.com", email, "trolleyes1234", "Registro en TrollEyes, se necesita confirmación", emailText, "html");
            oUsuarioDao.register(oUsuarioBean);
            oResponseBean = new ResponseBean(200, "Usuario registrado con exito, falta validar");
         } catch (MyException ex) {
            String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName() + " ob:" + ob;
            Log4jHelper.errorLog(msg, ex);
            ex.addDescripcion(msg);
            throw ex;
        } finally {
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        }
        return oGson.toJson(oResponseBean);
    }

    private String generaToken() {
        int numAleatorio = (int) Math.floor(Math.random() * (100000 - 999999) + 999999);
        String tokenAleatorio = String.valueOf(numAleatorio) + "TROLL";
        return tokenAleatorio;
    }
}
