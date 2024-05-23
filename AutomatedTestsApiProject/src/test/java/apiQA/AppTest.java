package apiQA;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void buscarAcoes() {
        System.out.println("oi teste");
        RestAssured.baseURI = "http://127.0.0.1:5000";
        given().
            contentType(ContentType.JSON).
        when().
            get("stocks").
        then().
            assertThat().
                statusCode(200).
                log().all().
                body("Message", Matchers.equalTo("there are stocks in the database")).
                body("Content.Stocks[0].Name", Matchers.equalTo("CSN MINERACAO"));

    }

    @Test
    public void buscaProdutosCadastradosFakerGPT() {
//Gerar valores dinâmicos para cadastrar ação
        Faker faker = new Faker();
        String fakeName = faker.company().name();
        float fakePrice = faker.number().randomNumber(2, true);
        String fakeSymbol = faker.regexify("[A-Z]{4}[0-9]{1,2}");
//Cadastrar ação
        String json = "{\n" +
        "    \"Name\": \""+fakeName+"\",\n" +
        "    \"Symbol\": \""+fakeSymbol+"\",\n" +
        "    \"Price\": "+fakePrice+"\n" +
        "}";
  
        RestAssured.baseURI = "http://127.0.0.1:5000";
        given().
            contentType(ContentType.JSON).
            body(json).
            log().all().
        when().
            post("new/stock/"). 
        then().
            assertThat().
                statusCode(201);
       
//Procurar ações
        RestAssured.baseURI = "http://127.0.0.1:5000/";
        Response response = given().
            contentType(ContentType.JSON).
            log().all(). /*Validar o Faker na request*/
        when().  
            get("stocks").
        then().
            assertThat().
                statusCode(200).
                log().all().
                body("Message", equalTo("there are stocks in the database"))
                .body("Content.Stocks.Name", hasItem(fakeName))
                .extract().response();
    }

    @Test
    public void cadastrarAcao() {
       
        String json = "{\n" +
              "    \"Name\": \"ITI BANCO DIGITAL 43\",\n" +
              "    \"Symbol\": \"ITIU01042\",\n" +
              "    \"Price\": 6\n" +
              "}";
        
        RestAssured.baseURI = "http://127.0.0.1:5000";
        given().
            contentType(ContentType.JSON).
            body(json).
            log().all().
        when().
            post("new/stock/").
          
        then().
            assertThat().
                statusCode(201).
                log().all().
                body("Message", Matchers.equalTo("new stock created"));
        }

     
    @Test
    public void procurarAcoes() {
        
        System.out.println("oi teste 3");
        RestAssured.baseURI = "http://127.0.0.1:5000";

            given().
                contentType(ContentType.JSON).
            when().
                get("stock/ITIU01042/").
            then().
                assertThat().
                        statusCode(200).
                        log().all().
                        body("Message", Matchers.equalTo("there are a stock with symbol ITIU01042 in database")).
                        body("Content.Stock[0].Name", Matchers.equalTo("ITI BANCO DIGITAL 43"));
     }        
    
    @Test
    public void cadastraracaoDuplicada() {
                           
            String json = "{\n" +
                    "    \"Name\": \"ITI BANCO DIGITAL 43\",\n" +
                     "    \"Symbol\": \"ITIU01042\",\n" +
                    "    \"Price\": 6\n" +
             "}";
                            
            RestAssured.baseURI = "http://127.0.0.1:5000";
            given().
                contentType(ContentType.JSON).
                body(json).
                log().all().
            when().
                post("new/stock/").
                              
            then().
                assertThat().
                        log().all().
                        statusCode(409).
                        body("Message", Matchers.equalTo("it was not possible to create a new action because the symbol is already in use"));
    }
    
    @Test
    public void deletarAcoes() {
                            
        System.out.println("oi teste 4");
        RestAssured.baseURI = "http://127.0.0.1:5000";
            given().
                    contentType(ContentType.JSON).
            when().
                    delete("stock/delete/ITIU01042/").
            then().
                    assertThat().
                    statusCode(200).
                        log().all().
                        body("Message", Matchers.equalTo("the stock has been deleted"));
    }
}
