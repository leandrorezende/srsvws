package br.com.srsv.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.gson.Gson;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;

import br.com.srsv.bean.BVeiculo;
import br.com.srsv.controller.CVeiculo;

@Path("/veiculo")
public class Veiculo {
	private Gson gson = new Gson();
	private CVeiculo cveiculo = new CVeiculo();
	
	@POST
	@Path("/listarVeiculo")
	@Produces("application/json")
	public String listarVeiculo(@HeaderParam("autorizacao") String autorizacao,
			@FormParam("id_veiculo") String id_veiculo) {
		return gson.toJson(cveiculo.listarVeiculo(autorizacao, id_veiculo));
	}

	@POST
	@Path("/cadastrarVeiculo")
	@Produces("application/json")
	@Consumes("application/json")
	public String cadastrarVeiculo(@HeaderParam("autorizacao") String autorizacao, String veiculo) {
		return gson.toJson(cveiculo.cadastrarVeiculo(autorizacao, gson.fromJson(veiculo, BVeiculo.class)));
	}

	@POST
	@Path("/removerVeiculo")
	@Produces("application/json")
	public String removerVeiculo(@HeaderParam("autorizacao") String autorizacao,
			@FormParam("id_veiculo") int id_veiculo) {
		return gson.toJson(cveiculo.removerVeiculo(autorizacao, id_veiculo));
	}

	@POST
	@Path("/alterarVeiculo")
	@Produces("application/json")
	@Consumes("application/json")
	public String alterarVeiculo(@HeaderParam("autorizacao") String autorizacao, String novoVeiculo) {
		return gson.toJson(cveiculo.alterarVeiculo(autorizacao, gson.fromJson(novoVeiculo, BVeiculo.class)));
	}

	@POST
	@Path("/alterarControle")
	@Produces("application/json")
	public String alterarControle(@HeaderParam("autorizacao") String autorizacao, @FormParam("valor") boolean valor,
			@FormParam("campo") String campo, @FormParam("id_veiculo") int id_veiculo) {
		return gson.toJson(cveiculo.alterarControle(autorizacao, valor, campo, id_veiculo));
	}
}
