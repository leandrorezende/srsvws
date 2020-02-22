package br.com.srsv.bean;

public class BRegistro {
	private int id_registro;
	private String num_registro;
	private boolean status;
	private int usuario_id;
	private String placa;
	private int id_veiculo;
	public int getId_veiculo() {
		return id_veiculo;
	}
	public void setId_veiculo(int id_veiculo) {
		this.id_veiculo = id_veiculo;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	private String created_at;
	public int getId_registro() {
		return id_registro;
	}
	public void setId_registro(int id_registro) {
		this.id_registro = id_registro;
	}
	public String getNum_registro() {
		return num_registro;
	}
	public void setNum_registro(String num_registro) {
		this.num_registro = num_registro;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getUsuario_id() {
		return usuario_id;
	}
	public void setUsuario_id(int usuario_id) {
		this.usuario_id = usuario_id;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
}
