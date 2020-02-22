package br.com.srsv.controller;

import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

import br.com.srsv.bean.BOcorrencia;
import br.com.srsv.bean.BRegistro;
import br.com.srsv.bean.BUsuario;
import br.com.srsv.bean.BVeiculo;
import br.com.srsv.model.MOcorrencia;
import br.com.srsv.model.MUsuario;
import br.com.srsv.model.MVeiculo;

public class COcorrencia {

	private MOcorrencia mocorrencia = null;
	private MUsuario musuario = null;

	public COcorrencia() {
		mocorrencia = new MOcorrencia();
		musuario = new MUsuario();
	}

	public void cadastrarOcorrencia(String num_dispositivo, double latitude, double longitude) {
		mocorrencia.cadastrarOcorrencia(num_dispositivo, latitude, longitude);
	}

	public BOcorrencia listarOcorrencia(String autorizacao, String placa) {
		BUsuario usuario = musuario.valida_api_key(autorizacao);
		if (usuario != null) {
			return mocorrencia.listarOcorrencia(placa, usuario.getId_usuario());
		} else
			return null;
	}

	public BVeiculo listarOcorrenciaAcoes(String autorizacao, String id_veiculo) {
		BUsuario usuario = musuario.valida_api_key(autorizacao);
		if (usuario != null) {
			return mocorrencia.listarOcorrenciaAcoes(id_veiculo);
		} else
			return null;
	}

	public String listarSensores(String num_dispositivo) {
		BVeiculo veiculo = mocorrencia.listarSensores(num_dispositivo);
		String retorno = "";
		if (veiculo.isFuncionamento_carro_status())
			retorno += "1";
		else
			retorno += "0";

		if (veiculo.isSistema_seguranaca_status())
			retorno += "1";
		else
			retorno += "0";
		return retorno;
	}

	public BVeiculo comparaIndice(String num_dispositivo, String indice, String latitude, String longitude) {
		BVeiculo veiculo = mocorrencia.listarSensores(num_dispositivo);
		if (!latitude.equals("inf") && !longitude.equals("inf")) {
			BOcorrencia ocorrencia = mocorrencia.listarOcorrencia(veiculo.getPlaca(), veiculo.getUsuario_id());
			if(ocorrencia != null){
				double distancia = CalculationByDistance(Double.parseDouble(latitude), Double.parseDouble(longitude),
						Double.parseDouble(ocorrencia.getLatitude()), Double.parseDouble(ocorrencia.getLongitude()));
				if(distancia > 20)
					indice += "1";
				else 
					indice += "0";
			}
			else 
				indice += (veiculo.isMovimento() == true) ? "1" : "0";
		} else
			indice += (veiculo.isMovimento() == true) ? "1" : "0";

		if (veiculo.isFuncionamento_sistema_status()) {
			if (!indice.equals(veiculo.getIndice())) {
				if (!indice.substring(0, 1).equals(((veiculo.isPresenca_sensor() == true) ? "1" : "0"))) 
					veiculo.setPresenca_sensor((indice.substring(0, 1).equals("1") ? true : false));
		
				if (!indice.substring(1, 2).equals(((veiculo.isAudio_sensor() == true) ? "1" : "0"))) 
					veiculo.setAudio_sensor((indice.substring(1, 2).equals("1") ? true : false));
				
				if (!indice.substring(2, 3).equals(((veiculo.isMovimento() == true) ? "1" : "0"))) 
					veiculo.setMovimento((indice.substring(2, 3).equals("1") ? true : false));

				veiculo.setIndice(indice);
				new MVeiculo().alterarSensores(veiculo);
				ArrayList<BRegistro> lista = new CUsuario().recuperar_associacoes(veiculo.getId_veiculo());
				for (int i = 0; i < lista.size(); i++) {
					enviaGCM(lista.get(i).getNum_registro(),
							lista.get(i).getPlaca() + "»" + lista.get(i).getId_veiculo());
				}
			}
		}
		return null;
	}

	public double CalculationByDistance(double initialLat, double initialLong, double finalLat, double finalLong) {
		int R = 6371;
		double lat1, lat2;
		double dLat = toRadians(finalLat - initialLat);
		double dLon = toRadians(finalLong - initialLong);

		lat1 = toRadians(initialLat);
		lat2 = toRadians(finalLat);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
	}

	public double toRadians(double deg) {
		return deg * (Math.PI / 180);
	}

	public void enviaGCM(String device_registration_id, String msg) {
		Sender sender = new Sender("AIzaSyAnTwA7Vr2zUXed7hg3UqL7vQAeLOAGGPU");
		Message message = new Message.Builder().collapseKey("mobileconf").addData("msg", msg).build();
		try {
			sender.send(message, device_registration_id, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Mensagem enviada: " + message.getData().get("msg"));
	}
}
