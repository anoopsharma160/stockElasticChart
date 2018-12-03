//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//public class IncOiIncPriTest {
//    @Test(invocationCount = 1000000)
//    void callIncPriIncOi() throws InterruptedException {
//        Thread.sleep(100000);
//        Response response = RestAssured.given().port(9000).get("/dashboard/IncPriIncOi/0");
//        Assert.assertEquals(response.getStatusCode(), 200);
//
//    }
//}
