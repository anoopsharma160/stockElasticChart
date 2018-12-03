import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;


public class BNOIDataTest {
    @Test(invocationCount = 1000000)
    void callNseData() throws InterruptedException {
        Thread.sleep(30000);
        Response response= RestAssured.given().port(9000).get("/dashboard/nsebnoi");
        System.out.println(response.prettyPrint());
        Assert.assertEquals(response.getStatusCode(),200);
    }
}
