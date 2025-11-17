package br.com.serverest.api.produtos;

import br.com.serverest.api.utils.BaseTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProdutosTest extends BaseTest {

	public String obterTokenAdm() {
		File payload = new File("src/test/resources/payloads/loginAdm.json");

		return given().contentType("application/json").body(payload).when().post("/login").then().statusCode(200)
				.extract().path("authorization");
	}

	@Test
	public void cadastrarProdutoComSucesso() {

		String token = obterTokenAdm();
		String nomeDinamico = "Produto QA " + UUID.randomUUID();
		String payloadComNome = """
				{
				    "nome": "%s",
				    "preco": 50,
				    "descricao": "Produto teste",
				    "quantidade": 10
				}
				""".formatted(nomeDinamico);

		given().contentType("application/json").header("authorization", token).body(payloadComNome).when()
				.post("/produtos").then().statusCode(201).body("message", equalTo("Cadastro realizado com sucesso"));
	}

	@Test
	public void cadastrarProdutoSemToken() {

		File payload = new File("src/test/resources/payloads/produtoSemToken.json");

		given().contentType("application/json").body(payload).when().post("/produtos").then().statusCode(401)
				.body("message", containsString("Token de acesso"));
	}
}
