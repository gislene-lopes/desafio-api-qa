package br.com.serverest.api.carrinhos;

import br.com.serverest.api.utils.BaseTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CarrinhoTest extends BaseTest {

	public String criarUsuarioComum(String email) {

		String payload = """
				    {
				      "nome": "Usuario Carrinho",
				      "email": "%s",
				      "password": "1234",
				      "administrador": "false"
				    }
				""".formatted(email);

		return given().contentType("application/json").body(payload).when().post("/usuarios").then()
				.statusCode(anyOf(is(201), is(400))).extract().path("_id");
	}

	public String login(String email) {
		
		String payload = """
				    {
				        "email": "%s",
				        "password": "1234"
				    }
				""".formatted(email);
		
		return given().contentType("application/json").body(payload).when().post("/login").then().statusCode(200)
				.extract().path("authorization");
	}

	@Test
	public void criarCarrinhoComSucesso() {

		
		String email = "user_" + UUID.randomUUID() + "@qa.com";
		criarUsuarioComum(email);
		String token = login(email);
		File payload = new File("src/test/resources/payloads/carrinhoNovo.json");

		given().contentType("application/json").header("authorization", token).body(payload).when().post("/carrinhos")
				.then().statusCode(anyOf(is(201), is(200)))
				.body("message", containsString("Cadastro realizado com sucesso"));
	}

	@Test
	public void finalizarCompra() {

		String email = "user2_" + UUID.randomUUID() + "@qa.com";
		criarUsuarioComum(email);
		String token = login(email);
		File payload = new File("src/test/resources/payloads/carrinhoNovo.json");

		given().contentType("application/json").header("authorization", token).body(payload).when().post("/carrinhos")
				.then().statusCode(anyOf(is(201), is(200)));

		given().header("authorization", token).when().delete("/carrinhos/concluir-compra").then().statusCode(200).body(
				"message",
				anyOf(containsString("Compra concluída com sucesso"), containsString("Registro excluído com sucesso")));
	}
}
