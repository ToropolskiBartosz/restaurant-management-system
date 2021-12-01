package restaurant.KFC.DataLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import restaurant.KFC.model.Customer;
import restaurant.KFC.model.KFCSet;
import restaurant.KFC.model.Product;
import restaurant.KFC.repository.CustomerRepository;
import restaurant.KFC.repository.KFC_SetRepository;
import restaurant.KFC.repository.ProductRepository;

import java.util.List;

@Component
public class ProductDataLoader implements ApplicationRunner {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    KFC_SetRepository kfc_setRepository;

    public void run(ApplicationArguments args) {

        productRepository.save(new Product("Qurito","Główne",23,"L",800));
        productRepository.save(new Product("Kubełek","Główne",53,"XXL",1500));
        productRepository.save(new Product("Nugetsy","Przystawka",15,"S",300));
        productRepository.save(new Product("Cola","Napój",5,"S",300));
        productRepository.save(new Product("Sok","Napój",5,"S",300));


        List<Product> productList =  productRepository.findAll();

        KFCSet kfc1 = new KFCSet("Mexico",30);
        KFCSet kfc2 = new KFCSet("MarcoPolo",50);
        KFCSet kfc3 = new KFCSet("Kubełek",50);
        KFCSet kfc4 = new KFCSet("Mexico",30);
        KFCSet kfc5 = new KFCSet("Mexico",30);
        kfc_setRepository.save(kfc1);
        kfc_setRepository.save(kfc2);
        kfc_setRepository.save(kfc3);
        kfc_setRepository.save(kfc4);
        kfc_setRepository.save(kfc5);

        kfc1.getProduct().add(productList.get(0));
        productList.get(0).getKfcSet().add(kfc1);
        kfc1.getProduct().add(productList.get(2));
        productList.get(2).getKfcSet().add(kfc1);
        kfc1.getProduct().add(productList.get(3));
        productList.get(3).getKfcSet().add(kfc1);

        kfc2.getProduct().add(productList.get(1));
        productList.get(1).getKfcSet().add(kfc2);
        kfc2.getProduct().add(productList.get(2));
        productList.get(2).getKfcSet().add(kfc2);
        kfc2.getProduct().add(productList.get(3));
        productList.get(3).getKfcSet().add(kfc2);

        kfc3.getProduct().add(productList.get(1));
        productList.get(1).getKfcSet().add(kfc3);
        kfc3.getProduct().add(productList.get(2));
        productList.get(2).getKfcSet().add(kfc3);
        kfc3.getProduct().add(productList.get(4));
        productList.get(4).getKfcSet().add(kfc3);

        kfc4.getProduct().add(productList.get(1));
        productList.get(1).getKfcSet().add(kfc4);
        kfc4.getProduct().add(productList.get(2));
        productList.get(2).getKfcSet().add(kfc4);
        kfc4.getProduct().add(productList.get(4));
        productList.get(4).getKfcSet().add(kfc4);

        kfc5.getProduct().add(productList.get(0));
        productList.get(1).getKfcSet().add(kfc5);
        kfc5.getProduct().add(productList.get(2));
        productList.get(2).getKfcSet().add(kfc5);
        kfc5.getProduct().add(productList.get(4));
        productList.get(4).getKfcSet().add(kfc5);

        for(int i=0; i<productList.size(); i++) productRepository.save(productList.get(i));

        kfc_setRepository.save(kfc1);
        kfc_setRepository.save(kfc2);
        kfc_setRepository.save(kfc3);
        kfc_setRepository.save(kfc4);
        kfc_setRepository.save(kfc5);


    }
}
