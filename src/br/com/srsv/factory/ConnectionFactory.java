package br.com.srsv.factory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 
 * Classe responsável por conter os metodos criar e fechar o banco de dados.
 *
 * @author Leandro Marques Rezende <leandro08rezende@gmail.com>
 * @since 04/07/2015 22:22:02
 * @version 1.0
 */
public class ConnectionFactory {

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://labsoft.muz.ifsuldeminas.edu.br:3306/srsv";
	private static final String USUARIO = "srsv";
	private static final String SENHA = "srsv*2015";
	private static Connection connection;

	public static Connection getConnection() {
		if (connection == null) {
			synchronized (Connection.class) {
				if (connection == null) {
					try {
						Class.forName(DRIVER);
						connection = DriverManager.getConnection(URL, USUARIO, SENHA);
					} catch (Exception e) {
						System.out.println("Erro ao criar conexão com o banco: " + URL);
						e.printStackTrace();
					}
				}
			}
		}
		return connection;
	}
}
