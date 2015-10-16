package br.com.alura.loja;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.modelo.Projeto;

public class ClienteTest {
	
	private HttpServer server;
	private WebTarget target;
	
	@Before
	public void startaServidor() {		
		server = Servidor.inicializaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		Client client = ClientBuilder.newClient(config);
		target = client.target("http://localhost:8080");
	}

	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {		
		Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);		
		assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
	
	@Test
	public void testaQueInsereUmCarrinhoAtravesDePost() {		
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(314l, "Tablet", 1000, 2));
		carrinho.setRua("Rua Tibiri 282");
		carrinho.setCidade("São Paulo");
		Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);
		Response response = target.path("/carrinhos").request().post(entity);
		assertEquals(201, response.getStatus());		
	}
	
	@Test
	public void testaQueAtualizaProdutoDoCarrinho() {		
		Produto videogame = new Produto(6237, "Videogame 4", 4000, 20);
		Entity<Produto> entity = Entity.entity(videogame, MediaType.APPLICATION_XML);
		Response response = target.path("/carrinhos/1/produtos/" + videogame.getId()).request().put(entity);
		assertEquals(200, response.getStatus());		
	}
	
	@Test
	public void testaRemocaoProdutoDoCarrinho() {		
		Response resposta = target.path("/carrinhos/1/produtos/6237").request(MediaType.APPLICATION_XML).delete();
		System.out.println(resposta);		
		assertEquals(200, resposta.getStatus());
	}
	
	@Test
	public void testaQueInsereUmProjetoAtravesDePost() {
		Projeto projeto = new Projeto();
		projeto.setNome("Minha loja");
		projeto.setAnoDeInicio(2015);
		Entity<Projeto> entity = Entity.entity(projeto, MediaType.APPLICATION_XML);
		Response response = target.path("/projetos").request().post(entity);
		assertEquals(201, response.getStatus());
	}
	
	@Test
	public void testaQueBuscarUmProjetoTrazOProjetoEsperado() {		
		Projeto projeto = target.path("/projetos/1").request().get(Projeto.class);
		assertEquals("Minha loja", projeto.getNome());
	}
	
	@Test
	public void testaRemocaoProjeto() {		
		Response resposta = target.path("/projetos/1").request(MediaType.APPLICATION_XML).delete();
		System.out.println(resposta);		
		assertEquals(200, resposta.getStatus());
	}
	
	@After
	public void desligaServidor() {
		server.stop();
	}
}
