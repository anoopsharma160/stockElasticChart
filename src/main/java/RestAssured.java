import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RestAssured {
    public static void main(String[] args) {
        Response response = io.restassured.RestAssured.given()
                .header("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
//                .get("https://www.nseindia.com/api/option-chain-indices?symbol=BANKNIFTY")
                .get("https://jsonplaceholder.typicode.com/todos/1");

        System.out.println(response.getStatusCode());
    }
}
