package urjc.isi.servidor;

import static spark.Spark.*;
//import spark.Request;
//import spark.Response;
import urjc.isi.servidor.App;


//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//import java.sql.Statement;
//import java.sql.PreparedStatement;
import java.util.Random;
import java.util.StringTokenizer;

import javax.servlet.MultipartConfigElement;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URI;


public class App 
{
	static String cosa = null;

	public createtables() throws URISyntaxException {
        try {
            if(c!=null) return;

            String username = "ktzfqcfutjkpgx";
            String password = "9fc28af010f8656f32da4d6d315de56c424f9620350a22c5a376529ef03bd3a2";
	    	String host = "ec2-54-216-155-253.eu-west-1.compute.amazonaws.com";
	    	String port = "5432";
	    	String database = "d1nf4olvlgr26a";
            String dbUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            
        	c = DriverManager.getConnection(dbUrl,username,password);
            c.setAutoCommit(false);
            
			c.prepareStatement("drop table if exists Examenes CASCADE").execute();
			c.prepareStatement("drop table if exists Alumnos CASCADE").execute();
			c.prepareStatement("drop table if exists RealizaExamen CASCADE").execute();
			c.prepareStatement("CREATE TABLE Examenes (IdExamen	INTEGER NOT NULL UNIQUE,Fecha	DATE NOT NULL,Asignatura VARCHAR(50) NOT NULL,PRIMARY KEY(IdExamen))").execute();
			c.prepareStatement("CREATE TABLE Alumnos (idAlumno	VARCHAR(50) NOT NULL UNIQUE,Nombre	VARCHAR(50) NOT NULL,Puerto	INTEGER,IP	VARCHAR(50),PRIMARY KEY(idAlumno))").execute();
			c.prepareStatement("CREATE TABLE RealizaExamen (idExamen INTEGER NOT NULL,idAlumno varchar(50) NOT NULL,Path varchar(50),FOREIGN KEY(idExamen) REFERENCES Examenes(IdExamen),FOREIGN KEY(idAlumno) REFERENCES Alumnos(idAlumno),PRIMARY KEY(idExamen,idAlumno))").execute();
			
            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

	public static void main(String[] args) throws 
	ClassNotFoundException, SQLException, URISyntaxException {
		port(getHerokuAssignedPort());
		
		createtables();
		examenDao examenDao = new examenDao();
		alumnoDao alumnoDao = new alumnoDao();
		realizaExamenDao realizaExamenDao = new realizaExamenDao();

		
	//	int examen=0;//Si es 0 el examen esta finalizado, 1 está activo.

		Random rnd = new Random();
		
		get("/", (req, res) -> {
			String result = "<form method=\"get\" action=\"/profesor\">"
			+ "<p>Profesor</p>\n"
			+ "<button type=\"summit\" value=\"profesor\">"	  
		    + "</form>";
			
			return result;
		});

		get("/profesor", (req, res) -> {
			int random = rnd.nextInt()*(-1);
			String result = "<form action='/"+random+  "' method='post'>"
			+ "<fieldset>"
			+ "<p>INTRODUZCA LOS DATOS:</p>\n"
			+ "<p>Asignatura</p>\n"
			+ "<input type='text' name='asignatura' required='true'>"
			+ "<input type=\"submit\" value=\"Comenzar examen\">"	  
		    + "</fieldset>" 
		    + "</form>";
			
			return result;
		});
		
		post("/alumno", (req, res) -> {
			
			String result = req.queryParams("nombre")+ " " +
			req.queryParams("dni")+ " " +
			req.queryParams("idex");
			System.out.println(result);
			cosa = result;
			String dni = req.queryParams("dni");
			String nombre = req.queryParams("nombre");
			int id_ex = Integer.parseInt(req.queryParams("idex"));
			String ip = req.ip();
			
			alumno alumnoObject = new alumno(dni,nombre, 4568, ip);
			alumnoDao.save(alumnoObject);
			realizaExamen realizaExamenObject = new realizaExamen(id_ex,dni, null);
			realizaExamenDao.save(realizaExamenObject);
			return result;
		});
		
		
		get("/cosa", (req, res) -> 
			"<h1> El examen con ID "+ cosa + "</h1>"
		);
		
		
		post("/:random", (req, res) -> { // Revisar si es get o post
			String path = req.uri();
			String aux = path.substring(1,path.length());
			//Añadido
			int id_examen = Math.abs(rnd.nextInt());
			String asignatura = req.queryParams("asignatura");
			//-Añadido
			String result ="<h1> El examen con ID "+id_examen+" se ha iniciado con el numero generado: "+ aux + "</h1>"
			+ "<form action='/finalizar' method='post'>"		
		    + "<input type=\"submit\" value=\"Finalizar examen\">"	    
		    + "</form>";
			
		
			//Añadido
			Date fecha = new Date();
			long lnMilisegundos = fecha.getTime();
			java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
				
			examen examenObject = new examen(id_examen, sqlDate, asignatura);
			examenDao.save(examenObject);
			//-Añadido
				
			
			return result;
		});
	

	    }
		
//		get("/alumnos", (req, res) -> 
//			String result = "<form action='/examinar' method='post'>"
//			+ "<fieldset>"
//			+ "<p>INTRODUZCA LOS DATOS:</p>\n"
//			+ "<p>Nombre: <input type='text' name='nombre_alumno' required='true'></p>\n"
//			+ "<p>Apellidos: <input type='text' name='apellido_alumno' required='true'></p>\n"
//			+ "<p>ID de examen: <input type='text' name='id_examen_alumno' required='true'></p>\n"
//			+ "<input type=\"submit\" value=\"Comenzar examen\"></fieldset></form>";
//			
//			return result;
//		);
//		
//		
//		
//		post("/examinar", (req, res) -> {
//			//Añadido
//			String nombre = req.queryParams("nombre_alumno");
//			String apellido = req.queryParams("apellido_alumno");
//			String id_alumno = req.queryParams("id_examen_alumno");
//			//-Añadido
//			String result = nombre + " " + apellido  + " has iniciado el examen con ID: " +"<strong>"+ id_alumno +"</strong>";
//			String nombre_alumno = nombre + " " + apellido;
//			
//			//Añadido
//			String IP = "1";
//			String puerto = "22";
//			base_datos.insert_alumno(connection, nombre_alumno, IP, puerto);
//			//-Añadido
//			
//			//EJECUTAR CODIGO DE COMMITS
//			
//			return result;
//		    });
	
	
	
	static int getHerokuAssignedPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
		    return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567; // return default port if heroku-port isn't set (i.e. on localhost)
	    }
}
