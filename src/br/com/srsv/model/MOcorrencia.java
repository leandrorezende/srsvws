package br.com.srsv.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.srsv.bean.BOcorrencia;
import br.com.srsv.bean.BVeiculo;
import br.com.srsv.factory.ConnectionFactory;

public class MOcorrencia extends ConnectionFactory {
	Connection conn = null;

	public MOcorrencia() {
		conn = ConnectionFactory.getConnection();
	}
	
	public void cadastrarOcorrencia(String num_dispositivo, double latitude, double longitude) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("INSERT INTO ocorrencia (num_dispositivo, latitude, longitude) VALUES(?,?,?)");
			stmt.setString(1, num_dispositivo);
			stmt.setDouble(2, latitude);
			stmt.setDouble(3, longitude);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public BOcorrencia listarOcorrencia(String placa, int usuario_id) {

		BOcorrencia ocorrencia = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT ocorrencia.* FROM ocorrencia "
					+ "INNER JOIN veiculo on veiculo.num_dispositivo = ocorrencia.num_dispositivo WHERE veiculo.placa = ? "
					+ "AND usuario_id = ? ORDER BY id_ocorrencia DESC LIMIT 0, 1";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, placa);
			stmt.setInt(2, usuario_id);
			resultSet = stmt.executeQuery();

			if(resultSet.next()) {
				ocorrencia = new BOcorrencia();
				ocorrencia.setId_ocorrencia(resultSet.getInt("id_ocorrencia"));
				ocorrencia.setLatitude(resultSet.getString("latitude"));
				ocorrencia.setLongitude(resultSet.getString("longitude"));
				ocorrencia.setCreated_at(resultSet.getString("created_at"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ocorrencia;
	}

	public BVeiculo listarOcorrenciaAcoes(String id_veiculo) {
		BVeiculo veiculo = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			String sql = "select veiculo.*, ((NOW() - MAX(ocorrencia.created_at)) / 100) AS falha FROM veiculo "
					+ "INNER join ocorrencia ON ocorrencia.num_dispositivo = veiculo.num_dispositivo WHERE id_veiculo = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, id_veiculo);

			resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				veiculo = new BVeiculo();
				veiculo.setId_veiculo(resultSet.getInt("id_veiculo"));
				veiculo.setUsuario_id((resultSet.getInt("usuario_id")));
				veiculo.setPlaca(resultSet.getString("placa"));
				veiculo.setNum_dispositivo(resultSet.getString("num_dispositivo"));
				veiculo.setFuncionamento_carro_status(resultSet.getBoolean("funcionamento_carro_status"));
				veiculo.setFuncionamento_sistema_status(resultSet.getBoolean("funcionamento_sistema_status"));
				veiculo.setSistema_seguranaca_status(resultSet.getBoolean("sistema_seguranca_status"));
				veiculo.setPresenca_sensor(resultSet.getBoolean("presenca_sensor"));
				veiculo.setAudio_sensor(resultSet.getBoolean("audio_sensor"));
				veiculo.setMovimento(resultSet.getBoolean("movimento"));
				veiculo.setFalha((resultSet.getInt("falha")) > 5 ? true : false);
				veiculo.setSaldo_simcard(resultSet.getFloat("saldo_simcard"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return veiculo;
	}

	public BVeiculo listarSensores(String num_dispositivo) {
		BVeiculo veiculo = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			String sql = "select * FROM veiculo  WHERE num_dispositivo = ?";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, num_dispositivo);

			resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				veiculo = new BVeiculo();
				veiculo.setId_veiculo(resultSet.getInt("id_veiculo"));
				veiculo.setUsuario_id((resultSet.getInt("usuario_id")));
				veiculo.setPlaca(resultSet.getString("placa"));
				veiculo.setFuncionamento_carro_status(resultSet.getBoolean("funcionamento_carro_status"));
				veiculo.setFuncionamento_sistema_status(resultSet.getBoolean("funcionamento_sistema_status"));
				veiculo.setSistema_seguranaca_status(resultSet.getBoolean("sistema_seguranca_status"));
				veiculo.setPresenca_sensor(resultSet.getBoolean("presenca_sensor"));
				veiculo.setAudio_sensor(resultSet.getBoolean("audio_sensor"));
				veiculo.setIndice(resultSet.getString("indice"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return veiculo;
	}
}
