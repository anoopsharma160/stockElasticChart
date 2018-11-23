package StockMongo.StockMongo;

import Controller.IncreaseOIController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = IncreaseOIController.class)
public class StockMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockMongoApplication.class, args);
	}
}
