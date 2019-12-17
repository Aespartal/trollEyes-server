package net.ausiasmarch.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.ausiasmarch.exceptions.MyException;

public interface BeanInterface {

    Integer getId();

    void setId(Integer id);

    public BeanInterface fill(ResultSet oResultSet, Connection oConnection, int spread,UsuarioBean oUsuarioBeanSession) throws MyException, SQLException;

    public PreparedStatement orderSQL(List<String> orden, PreparedStatement oPreparedStatement) throws MyException, SQLException;

    public String getFieldInsert();

    public String getFieldConcat();
    
    public String getFieldLink();
    
    public String getFieldId(String filter);
    
    public PreparedStatement setFieldId(int numparam,PreparedStatement oPreparedStatement, int id, int rpp,int offset) throws MyException, SQLException;
    
    public PreparedStatement setFilter(int numparam, PreparedStatement oPreparedStatement, String word, int rpp, int offset) throws MyException, SQLException;
    
    public PreparedStatement setFieldInsert(BeanInterface oBeanParam, PreparedStatement oPreparedStatement) throws MyException, SQLException;

    public String getFieldUpdate();

    public PreparedStatement setFieldUpdate(BeanInterface oBeanParam, PreparedStatement oPreparedStatement) throws MyException, SQLException;
    
    public String getFieldOrder(String orden);

}
