package br.com.srsv.model;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import br.com.srsv.bean.BRegistro;
import br.com.srsv.bean.BUsuario;
import br.com.srsv.factory.ConnectionFactory;

public class MUsuario extends ConnectionFactory {

	Connection conn = null;

	public MUsuario() {
		conn = ConnectionFactory.getConnection();
	}

	public BUsuario login(String usuario, String senha, String id_registro, boolean flag) {
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet resultSet = null;
		BUsuario OBJusuario = null;
		try {
			String sql = "SELECT id_usuario, nome, api_key, created_at FROM usuario WHERE usuario = ? and senha = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, usuario);
			stmt.setString(2, senha);

			resultSet = stmt.executeQuery();
			if (resultSet.next() == true && !id_registro.equals("")) {
				OBJusuario = new BUsuario();
				OBJusuario.setId_usuario(resultSet.getInt("id_usuario"));
				OBJusuario.setNome(resultSet.getString("nome"));
				OBJusuario.setApi_key(resultSet.getString("api_key"));
				OBJusuario.setCreated_at(resultSet.getString("created_at"));

				sql = "INSERT INTO registro_dispositivos (num_registro,status,usuario_id) VALUES(?,?,?) ";
				if (flag == true)
					sql = sql + " ,(?,?,?);";

				stmt2 = conn.prepareStatement(sql);

				stmt2.setString(1, id_registro);
				stmt2.setBoolean(2, false);
				stmt2.setInt(3, resultSet.getInt("id_usuario"));

				if (flag == true) {
					stmt2.setString(4, id_registro);
					stmt2.setBoolean(5, true);
					stmt2.setInt(6, resultSet.getInt("id_usuario"));
				}
				stmt2.executeUpdate();
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return OBJusuario;
	}

	public int verificaDuplicidadeCadastrado(String usuario, String senha, String email) {

		int id = 0;
		ResultSet resultSet = null;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(
					"SELECT usuario, senha, email from usuario WHERE usuario = ? OR  senha = ? OR email = ?");
			stmt.setString(1, usuario);
			stmt.setString(2, senha);
			stmt.setString(3, email);
			resultSet = stmt.executeQuery();

			if (resultSet.next() == false) {
				id = 0;
			} else {
				if (resultSet.getString("usuario").toUpperCase().equals(usuario.toUpperCase()))
					id = 1;
				else if (resultSet.getString("senha").toUpperCase().equals(senha.toUpperCase()))
					id = 2;
				else if (resultSet.getString("email").toUpperCase().equals(email.toUpperCase()))
					id = 3;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return id;
	}

	private static String generateApiKey(final int keyLen) throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(keyLen);
		SecretKey secretKey = keyGen.generateKey();
		byte[] encoded = secretKey.getEncoded();
		return DatatypeConverter.printHexBinary(encoded).toLowerCase();
	}

	public String cadastrarUsuario(BUsuario usuario) {
		String msg;
		int sucesso = 0;
		int usuariosCadastrados = verificaDuplicidadeCadastrado(usuario.getUsuario(), usuario.getSenha(),
				usuario.getEmail());

		if (usuariosCadastrados == 0) {
			PreparedStatement stmt = null;
			try {
				stmt = conn
						.prepareStatement("INSERT INTO usuario (usuario,senha,nome,email,api_key) VALUES(?,?,?,?,?)");

				stmt.setString(1, usuario.getUsuario());
				stmt.setString(2, usuario.getSenha());
				stmt.setString(3, usuario.getNome());
				stmt.setString(4, usuario.getEmail());
				stmt.setString(5, generateApiKey(128));
				sucesso = stmt.executeUpdate();

				if (sucesso > 0)
					msg = "Usuário cadastrado com sucesso";
				else
					msg = "Erro ao cadastrar usuário";

			} catch (Exception e) {
				e.printStackTrace();
				msg = "Erro ao cadastrar usuário";
			} 
		} else if (usuariosCadastrados == 1)
			msg = "Usuário já cadastrado, escolha outro.";
		else if (usuariosCadastrados == 2)
			msg = "Senha já cadastrado, escolha outro.";
		else if (usuariosCadastrados == 3)
			msg = "Email já cadastrado, escolha outro.";
		else
			msg = "Erro ao cadastrar usuário";
		return msg;
	}

	public BUsuario recuperarSenha(String email) {
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		BUsuario usuario = null;
		try {
			String sql = "SELECT usuario,senha,nome FROM usuario WHERE email = ?";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);

			resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				usuario = new BUsuario();
				usuario.setUsuario(resultSet.getString("usuario"));
				usuario.setSenha(resultSet.getString("senha"));
				usuario.setNome(resultSet.getString("nome"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return usuario;
	}

	@SuppressWarnings("resource")
	public String trocarSenha(String usuario, String senhaAtual, String novaSenha, int id_usuario, String autorizacao) {
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = conn.prepareStatement(
					"SELECT usuario, senha, api_key  FROM usuario  where usuario = ? and senha = ? and api_key = ?");
			stmt.setString(1, usuario);
			stmt.setString(2, senhaAtual);
			stmt.setString(3, autorizacao);

			resultSet = stmt.executeQuery();

			if(resultSet.next()) {
				int usuariosCadastrados = verificaDuplicidadeCadastrado("", novaSenha, "");
				if (usuariosCadastrados == 0) {
					stmt = conn.prepareStatement("UPDATE usuario SET senha = ? WHERE id_usuario = ?");
					stmt.setString(1, novaSenha);
					stmt.setInt(2, id_usuario);
					stmt.executeUpdate();
					return "Senha alterada com sucesso!";
				} else {
					return "Senha já cadastrada, escolha outra.";
				}
			}
			return "Usuário e/ou Senha inválidos";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Erro ao executar operação";
		} 
	}

	public BUsuario valida_api_key(String api_key) {
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		BUsuario usuario = null;
		try {
			String sql = "SELECT id_usuario,senha FROM usuario WHERE api_key = ?";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, api_key);

			resultSet = stmt.executeQuery();

			if(resultSet.next()) {
				usuario = new BUsuario();
				usuario.setId_usuario((resultSet.getInt("id_usuario")));
				usuario.setSenha(resultSet.getString("senha"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usuario;
	}

	public Boolean removerAssocicao(String registro, boolean flag, int id_usuario) {
		int excluidos = 0;
		PreparedStatement stmt = null;
		try {
			String sql = "DELETE FROM registro_dispositivos WHERE status = ? AND usuario_id = ?";
			if (flag == false)
				sql = sql + "  AND num_registro = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, flag);
			stmt.setInt(2, id_usuario);
			if (flag == false)
				stmt.setString(3, registro);
			excluidos = stmt.executeUpdate();

			if (excluidos > 0) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public ArrayList<BRegistro> recuperar_associacoes(int id) {
		ArrayList<BRegistro> listaRegistros = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT registro_dispositivos.*,veiculo.placa,veiculo.id_veiculo FROM registro_dispositivos "
					+ "INNER JOIN usuario on usuario.id_usuario = registro_dispositivos.usuario_id "
					+ "INNER JOIN veiculo on veiculo.usuario_id = usuario.id_usuario WHERE veiculo.id_veiculo = ? "
					+ "GROUP BY registro_dispositivos.usuario_id, registro_dispositivos.num_registro";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			resultSet = stmt.executeQuery();
			listaRegistros = new ArrayList<BRegistro>();

			while (resultSet.next()) {
				BRegistro registro = new BRegistro();
				registro.setId_registro(resultSet.getInt("id_registro"));
				registro.setNum_registro(resultSet.getString("num_registro"));
				registro.setStatus(resultSet.getBoolean("status"));
				registro.setUsuario_id(resultSet.getInt("usuario_id"));
				registro.setCreated_at(resultSet.getString("create_at"));
				registro.setPlaca(resultSet.getString("placa"));
				registro.setId_registro(resultSet.getInt("id_registro"));
				registro.setId_veiculo(resultSet.getInt("id_veiculo"));
				listaRegistros.add(registro);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			listaRegistros = null;
		}
		return listaRegistros;
	}

	public Boolean removerAssocicaoDispositivo(String registro) {
		int excluidos = 0;
		PreparedStatement stmt = null;
		try {
			String sql = "DELETE FROM registro_dispositivos WHERE status = ? AND num_registro = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, true);
			stmt.setString(2, registro);
			excluidos = stmt.executeUpdate();

			if (excluidos > 0) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}