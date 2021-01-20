package urjc.isi.servidor;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class examenDao {
	
	private static Connection c;

	// Con este método creamos la conexión con la bbdd.
    public examenDao() throws URISyntaxException {
        try {
            if(c!=null) return;

            String username = "ktzfqcfutjkpgx";
            String password = "9fc28af010f8656f32da4d6d315de56c424f9620350a22c5a376529ef03bd3a2";
	    String host = "ec2-54-216-155-253.eu-west-1.compute.amazonaws.com";
	    String port = "5432";
	    String database = "d1nf4olvlgr26a";
            String dbUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            
        	c = DriverManager.getConnection(dbUrl,username,password);
                //c = DriverManager.getConnection("jdbc:postgresql:proyecto.db");
            	c.setAutoCommit(false);
            

            c.prepareStatement("drop table if exists Examenes CASCADE").execute();
            c.prepareStatement("CREATE TABLE Examenes (IdExamen	INTEGER NOT NULL UNIQUE,Fecha	DATE NOT NULL,Asignatura VARCHAR(50) NOT NULL,PRIMARY KEY(IdExamen))").execute();
            
            	c.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    // Con este método vamos a poder obtener todos exámenes de la tabla Exámen.
    public List<examen> all() {

        List<examen> allExamenes = new ArrayList<examen>();

        try {
            PreparedStatement ps = c.prepareStatement("select * from Examenes");

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int idExamen = rs.getInt("idExamen");
                Date Fecha = rs.getDate("Fecha");
                String Asignatura = rs.getString("Asignatura");
                allExamenes.add(new examen(idExamen, Fecha, Asignatura));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            return allExamenes;
        }
    }
    
    public Date getFecha(int idExamen) {
    	Date fecha = null;

        try {
        	String query = "SELECT * from Examenes WHERE idExamen = " + idExamen;
            PreparedStatement ps = c.prepareStatement(query);
            
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
            	fecha = rs.getDate("Fecha");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            return fecha;
        }
    }
    
    // Con este método insertamos el objeto examen recibido en la tabla de nuestra bbdd Examenes.
    public void save(examen examen) {
        try {
            PreparedStatement ps = c.prepareStatement("insert into Examenes(idExamen, Fecha , Asignatura) VALUES(?,?,?)");
            ps.setInt(1, examen.getIdExamen());
    		System.out.println("sql.Date insert: "+  examen.getFecha());
            ps.setDate(2, examen.getFecha());
            ps.setString(3, examen.getAsignatura());
            ps.execute();

            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }

    // Cerramos la conexión con la bbdd
    public void close() {
        try {
            c.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
