package restaurant.KFC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import restaurant.KFC.model.Product;
import restaurant.KFC.repository.KFC_SetRepository;
import restaurant.KFC.repository.ProductRepository;

import java.util.List;

@SpringBootApplication
public class KfcApplication  {

	public static void main(String[] args) {
		SpringApplication.run(KfcApplication.class, args);
	}

}
