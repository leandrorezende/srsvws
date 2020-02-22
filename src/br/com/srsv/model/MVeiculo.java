package br.com.srsv.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import br.com.srsv.bean.BVeiculo;
import br.com.srsv.factory.ConnectionFactory;

public class MVeiculo extends ConnectionFactory {
	Connection conn = null;

	public MVeiculo() {
		conn = ConnectionFactory.getConnection();
	}

	public ArrayList<BVeiculo> listarVeiculo(String api_key, String id_veiculo) {
		ArrayList<BVeiculo> listaVeiculos = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT veiculo.* FROM veiculo "
					+ "INNER JOIN usuario ON usuario.id_usuario = veiculo.usuario_id WHERE usuario.api_key = ?";
			if (id_veiculo != null)
				sql = sql + " AND id_veiculo = ?";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, api_key);

			if (id_veiculo != null)
				stmt.setString(2, id_veiculo);
			resultSet = stmt.executeQuery();
			listaVeiculos = new ArrayList<BVeiculo>();

			while (resultSet.next()) {
				BVeiculo veiculo = new BVeiculo();
				veiculo.setId_veiculo((resultSet.getInt("id_veiculo")));
				veiculo.setUsuario_id((resultSet.getInt("usuario_id")));
				veiculo.setPlaca(resultSet.getString("placa"));
				veiculo.setFuncionamento_carro_status(resultSet.getBoolean("funcionamento_carro_status"));
				veiculo.setFuncionamento_sistema_status(resultSet.getBoolean("funcionamento_sistema_status"));
				veiculo.setSistema_seguranaca_status(resultSet.getBoolean("sistema_seguranca_status"));
				veiculo.setPresenca_sensor(resultSet.getBoolean("presenca_sensor"));
				veiculo.setAudio_sensor(resultSet.getBoolean("audio_sensor"));
				veiculo.setNum_dispositivo(resultSet.getString("num_dispositivo"));
				listaVeiculos.add(veiculo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			listaVeiculos = null;
		}
		return listaVeiculos;
	}

	public String cadastrarVeiculo(BVeiculo veiculo) {
		String msg = null;
		int sucesso = 0;

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("INSERT INTO veiculo (usuario_id,placa,num_dispositivo) " + "VALUES(?,?,?)");

			stmt.setInt(1, veiculo.getUsuario_id());
			stmt.setString(2, veiculo.getPlaca());
			stmt.setString(3, veiculo.getNum_dispositivo());
			sucesso = stmt.executeUpdate();

			if (sucesso > 0) {
				msg = "Veículo cadastrado com sucesso!";
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Erro ao cadastrar veículo";
		}
		return msg;
	}

	public String removerVeiculo(int id_veiculo, int usuario_id) {
		int excluidos = 0;
		String msg;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("DELETE FROM veiculo WHERE id_veiculo = ? and usuario_id = ?");
			stmt.setInt(1, id_veiculo);
			stmt.setInt(2, usuario_id);
			excluidos = stmt.executeUpdate();

			if (excluidos > 0) {
				msg = "Veículo excluído com sucesso!";
			} else {
				msg = "Veículo não existe";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			msg = "Erro ao excluir veículo!";
		} 
		return msg;
	}

	public String alterarVeiculo(BVeiculo veiculo) {
		PreparedStatement stmt = null;
		int sucesso = 0;
		String msg;
		try {
			stmt = conn.prepareStatement("UPDATE veiculo SET placa = ?, num_dispositivo = ? WHERE id_veiculo = ?");
			stmt.setString(1, veiculo.getPlaca());
			stmt.setString(2, veiculo.getNum_dispositivo());
			stmt.setInt(3, veiculo.getId_veiculo());

			sucesso = stmt.executeUpdate();

			if (sucesso > 0) {
				msg = "Veículo editado com sucesso!";
			} else {
				msg = "Veículo não existe";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			msg = "Erro ao editar o veículo";
		} 
		return msg;
	}

	public String alterarControle(boolean valor, String campo, int id_veiculo) {
		PreparedStatement stmt = null;
		String msg;
		try {
			stmt = conn.prepareStatement("UPDATE veiculo SET " + campo + " = ? WHERE id_veiculo = ?");
			stmt.setBoolean(1, valor);
			stmt.setInt(2, id_veiculo);
			stmt.executeUpdate();
			msg = null;
		} catch (SQLException e) {
			e.printStackTrace();
			msg = "Erro ao executar a operação!";
		} 
		return msg;
	}

	public void alterarSensores(BVeiculo veiculo) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(
					"UPDATE veiculo SET presenca_sensor = ?, audio_sensor = ?, movimento = ?, indice = ? WHERE id_veiculo = ?");
			stmt.setBoolean(1, veiculo.isPresenca_sensor());
			stmt.setBoolean(2, veiculo.isAudio_sensor());
			stmt.setBoolean(3, veiculo.isMovimento());
			stmt.setString(4, veiculo.getIndice());
			stmt.setInt(5, veiculo.getId_veiculo());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
}
