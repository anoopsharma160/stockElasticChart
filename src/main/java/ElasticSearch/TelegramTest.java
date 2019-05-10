package ElasticSearch;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TelegramTest {
    public static void main(String[] args) {
        Response response= RestAssured.given().queryParam("chat_id","654771852").queryParam("text","Hello")
                .get("https://api.telegram.org/bot826838727:AAEhTJT4xQsTRxuF5wxtgNELjSJgjwkvYro/sendMessage");
        System.out.println(response.prettyPrint());
    }
}
