package sample;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.*;


public class SQLHandler {
    public static Connection con;

    private void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:Database.db");
    }

    private void initialise(String tableName) throws SQLException {
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+tableName+"'");
        if( !res.next() ) {
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE "+tableName+ "(id integer," + " word varchar(1000), explanation varchar(1000)," + " primary key(id));");
        }
    }

    public void addContent(String tableName, String word, String explanation) throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()){
            getConnection();
        }
        PreparedStatement prep = con.prepareStatement("INSERT INTO "+tableName+"(word, explanation) values(?, ?);");
        prep.setString(1, word);
        prep.setString(2, explanation);
        prep.execute();
        con.close();
    }

    public void removeContent(String tableName, Integer id) throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()){
            getConnection();
        }
        PreparedStatement prep = con.prepareStatement("DELETE FROM "+tableName+" WHERE id="+id+";");
        prep.execute();
        con.close();
    }
    public int getId(String tableName, String word, String explanation) throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()){
            getConnection();
        }
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT id FROM "+tableName+" WHERE word='"+word+"' AND explanation='"+explanation+"';");
        Integer result = -1;
        while(res.next()) {
            //System.out.println(res.getString("id"));
            result = res.getInt("id");
        }

        return result;
    }
    public void deleteSet(String tableName) throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()){
            getConnection();
        }
        Statement state = con.createStatement();
        String cmd = "DROP TABLE IF EXISTS "+tableName;
        state.executeUpdate(cmd);
        con.close();
    }
    public void createSet(String tableName) throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()){
            getConnection();
        }
        initialise(tableName);
        con.close();
    }
    public ResultSet getSets() throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()){
            getConnection();
        }
//        Statement state = con.createStatement();
//        ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
        ResultSet res = con.getMetaData().getTables(null, null, null, null);
        return res;
    }
    public ResultSet getWords(String tableName) throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()){
            getConnection();
        }
        Statement state = con.createStatement();
        //("IF EXISTS (SELECT name FROM sqlite_master WHERE type='table' AND name='"+tableName+"') BEGIN SELECT word, explanation FROM "+tableName);
        ResultSet rs = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+tableName+"'");
        ResultSet res = null;
        if(rs.next())
            res = state.executeQuery("SELECT word, explanation FROM "+tableName);

        if(res == null)
            con.close();
        return res;
    }

    public void editRow(String tableName, Integer id, String word, String explanation) throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()){
            getConnection();
        }

        PreparedStatement prep = con.prepareStatement("UPDATE "+tableName+" SET word='"+word+"', explanation='"+explanation+"' WHERE id="+id+";");
        prep.execute();
        con.close();
    }

    public void editSetName(String tableName, String newName) throws SQLException, ClassNotFoundException {
        if(con == null || con.isClosed()) {
            getConnection();
        }

        PreparedStatement prep = con.prepareStatement("ALTER TABLE "+tableName+" RENAME TO "+newName+";");
        prep.execute();
        con.close();
    }

}
