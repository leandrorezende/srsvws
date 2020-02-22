package br.com.srsv.controller;

import java.util.ArrayList;

import br.com.srsv.bean.BUsuario;
import br.com.srsv.bean.BVeiculo;
import br.com.srsv.model.MUsuario;
import br.com.srsv.model.MVeiculo;

public class CVeiculo {
	
	private MVeiculo mveiculo = null;
	private MUsuario musuario = null;

	public CVeiculo() {
		mveiculo = new MVeiculo();
		musuario = new MUsuario();
	}
	public ArrayList<BVeiculo> listarVeiculo(String autorizacao, String id_veiculo) {
		return mveiculo.listarVeiculo(autorizacao, id_veiculo);
	}
	
	public String cadastrarVeiculo(String autorizacao,BVeiculo veiculo) {
		BUsuario usuario = musuario.valida_api_key(autorizacao);
		if(usuario != null){
			veiculo.setUsuario_id(usuario.getId_usuario());
			return mveiculo.cadastrarVeiculo(veiculo);
		} else
			return "Erro ao cadastrar veículo";
		
	}
	
	public String removerVeiculo(String api_key,int id_veiculo) {
		BUsuario usuario = musuario.valida_api_key(api_key);
		if(usuario != null){
			return mveiculo.removerVeiculo(id_veiculo, usuario.getId_usuario());
		} else
			return "Erro ao cadastrar veículo";
	}
	
	public String alterarVeiculo(String api_key, BVeiculo veiculo) {
		BUsuario usuario = musuario.valida_api_key(api_key);
		if(usuario != null){
			return mveiculo.alterarVeiculo(veiculo);
		} else
			return "Erro ao editar o veículo";
	}
	
	public String alterarControle(String autorizacao, boolean valor, String campo, int id_veiculo) {
		BUsuario usuario = musuario.valida_api_key(autorizacao);
		if(usuario != null){
			return mveiculo.alterarControle(valor, campo, id_veiculo);
		} else
			return "Erro ao executar a operação!";
	}
}
