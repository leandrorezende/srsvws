package br.com.srsv.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import com.google.gson.Gson;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import br.com.srsv.controller.COcorrencia;

@Path("/ocorrencia")
public class Ocorrencia {
	private Gson gson = new Gson();
	private COcorrencia cocorrencia = new COcorrencia();

	@GET
	@Path("/cadastrarOcorrencia/{latitude}/{longitude}/{num_dispositivo}/{indice}")
	public String cadastrarOcorrencia(@PathParam("latitude") String latitude, @PathParam("longitude") String longitude,
			@PathParam("num_dispositivo") String num_dispositivo, @PathParam("indice") String indice) {
		cocorrencia.comparaIndice(num_dispositivo, indice, latitude, longitude);
		if (!latitude.equals("inf") && !longitude.equals("inf"))
			cocorrencia.cadastrarOcorrencia(num_dispositivo, Double.parseDouble(latitude), Double.parseDouble(longitude));
		return gson.toJson(cocorrencia.listarSensores(num_dispositivo));
	}

	@POST
	@Path("/listarOcorrencia")
	@Produces("application/json")
	public String listarOcorrencia(@HeaderParam("autorizacao") String autorizacao, @FormParam("placa") String placa) {
		return gson.toJson(cocorrencia.listarOcorrencia(autorizacao, placa));
	}

	@POST
	@Path("/listarOcorrenciaAcoes")
	@Produces("application/json")
	public String listarOcorrenciaAcoes(@HeaderParam("autorizacao") String autorizacao,
			@FormParam("id_veiculo") String id_veiculo) {
		return gson.toJson(cocorrencia.listarOcorrenciaAcoes(autorizacao, id_veiculo));
	}	
}
