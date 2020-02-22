package br.com.srsv.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.HeaderParam;
import com.google.gson.Gson;
import br.com.srsv.bean.BUsuario;
import br.com.srsv.controller.CUsuario;

@Path("/usuario")
public class Usuario {
	private Gson gson = new Gson();
	private CUsuario cusuario = new CUsuario();	
	
	@POST
	@Path("/cadastrarUsuario")
	@Produces("application/json")
	@Consumes("application/json")
	public String cadastrarUsuario(String usuario) {
		return gson.toJson(cusuario.cadastrarUsuario(gson.fromJson(usuario, BUsuario.class)));
	}
	
	@POST
	@Path("/login")
	@Produces("application/json")
	public String login(@HeaderParam("autorizacao") String autorizacao, @FormParam("id_registro") String id_registro,
			@FormParam("flag") boolean flag) {
		return gson.toJson(cusuario.login(autorizacao, id_registro, flag));
	}

	@POST
	@Path("/recuperarSenha")
	@Produces("application/json")
	public String recuperarSenha(@FormParam("email") String email) {
		return gson.toJson(cusuario.recuperarSenha(email));
	}

	@POST
	@Path("/trocarSenha")
	@Produces("application/json")
	public String trocarSenha(@HeaderParam("autorizacao") String autorizacao, @FormParam("usuario") String usuario,
			@FormParam("senhaAtual") String senhaAtual, @FormParam("novaSenha") String novaSenha) {
		return gson.toJson(cusuario.trocarSenha(autorizacao, usuario, senhaAtual, novaSenha));
	}

	@POST
	@Path("/removerAssocicao")
	@Produces("application/json")
	public String removerAssocicao(@HeaderParam("autorizacao") String autorizacao,
			@FormParam("registro") String registro, @FormParam("flag") boolean flag) {
		return gson.toJson(cusuario.removerAssocicao(autorizacao, registro, flag));
	}
	
	@POST
	@Path("/removerAssocicaoDispositivo")
	@Produces("application/json")
	public String removerAssocicaoDispositivo(@HeaderParam("autorizacao") String autorizacao,
			@FormParam("registro") String registro) {
		return gson.toJson(cusuario.removerAssocicaoDispositivo(autorizacao, registro));
	}
}
