package br.com.srsv.controller;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

import br.com.srsv.bean.BRegistro;
import br.com.srsv.bean.BUsuario;
import br.com.srsv.model.MUsuario;
import sun.misc.BASE64Decoder;

public class CUsuario {
	private MUsuario musuario = null;

	public CUsuario() {
		musuario = new MUsuario();
	}

	public BUsuario login(String autorizacao, String id_registro, boolean flag) {
		String credenciais[] = decodificar(autorizacao);
		return musuario.login(credenciais[0], credenciais[1], id_registro, flag);
	}

	public String cadastrarUsuario(BUsuario usuario) {
		String msg = musuario.cadastrarUsuario(usuario);
		if (msg.equals("Usuário cadastrado com sucesso"))
			enviarEmail(usuario.getEmail(), "Cadastro SRSV",
					"Olá, " + usuario.getNome() + "." + "<br>Seja bem vindo ao sistema SRSV.<br>O Sistema pretende "
							+ "atender a todas suas expectativas de segurança aos seus veículos."
							+ "<br><br>Atenciosamente, <br> Elda Software Solutions <br> +55 35 32154265"
							+ "<br> www.elda.com");
		return msg;
	}
	
	public String recuperarSenha(String email) {
		BUsuario usuarioRetornado = musuario.recuperarSenha(email);
		if (usuarioRetornado != null){ 
			enviarEmail(email, "Recuperação de senha",
					"Olá, " + usuarioRetornado.getNome() + ".<br>Você está recebendo este e-mail pois "
							+ "solicitou a recuperação da sua senha de acesso ao sistema SRSV.<br><br>" + "usuário: "
							+ usuarioRetornado.getUsuario() + "<br>" + "senha: " + usuarioRetornado.getSenha()
							+ "<br><br>Atenciosamente, <br> Elda Software Solutions <br> +55 35 32154265 <br> www.elda.com");
			return "Email enviado com sucesso ! \nVerifique sua caixa de entrada.";
		}
		else 
		    return "Email fornecido não é válido";
	}

	public String trocarSenha(String autorizacao, String usuario, String senhaAtual, String novaSenha) {
		BUsuario OBJusuario = musuario.valida_api_key(autorizacao);
		if(OBJusuario != null){
			return musuario.trocarSenha(usuario, senhaAtual, novaSenha, OBJusuario.getId_usuario(), autorizacao);
		} else
			return "Erro ao executar operação";
	}

	private String[] decodificar(String authorization) {
		String dados = authorization.substring(authorization.indexOf(" ") + 1);
		byte[] bytes;
		try {
			bytes = new BASE64Decoder().decodeBuffer(dados);
			String decoded = new String(bytes);
			String credenciais[] = decoded.split(":");
			return credenciais;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Boolean removerAssocicao(String autorizacao, String registro, boolean flag) {
		BUsuario OBJusuario = musuario.valida_api_key(autorizacao);
		if(OBJusuario != null){
			return musuario.removerAssocicao(registro, flag, OBJusuario.getId_usuario());
		} else
			return false;
	}
	
	public Boolean removerAssocicaoDispositivo(String autorizacao, String registro) {
		BUsuario OBJusuario = musuario.valida_api_key(autorizacao);
		if(OBJusuario != null){
			return musuario.removerAssocicaoDispositivo(registro);
		} else
			return false;
	}
	
	public ArrayList<BRegistro> recuperar_associacoes(int id) {
		return musuario.recuperar_associacoes(id);		
	}
	
	public void enviarEmail(String destinatario, String assunto, String msg) {
		try {
			HtmlEmail email = new HtmlEmail();

			email.setHostName("smtp.googlemail.com");
			email.setSmtpPort(465);
			email.setAuthenticator(new DefaultAuthenticator("sistema.srsv@gmail.com", "srsvelda"));
			email.setSSLOnConnect(true);
			email.setFrom("sistema.srsv@gmail.com");
			email.setSubject(assunto);
			email.setHtmlMsg(msg);
			email.addTo(destinatario);
			email.send();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
