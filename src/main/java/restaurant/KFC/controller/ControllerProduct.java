package restaurant.KFC.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.KFC.model.*;
import restaurant.KFC.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ControllerProduct {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    KFC_SetRepository kfc_setRepository;
    @Autowired
    ProductInOrderRepository productInOrderRepository;

    @RequestMapping(value = "/print_all_product",method = RequestMethod.GET)
    public String printAll(Model model)
    {
        try {
            List<Product> productList = productRepository.findAll();
            List<ProductInOrder> proInOrd = productInOrderRepository.findAll();

            model.addAttribute("header", "Lista wszystkich produktow"); //Dodanie obiektu do pamieci lokalnej modelu
            model.addAttribute("productList", productList); //Dodanie obiektu do pamieci lokalnej modelu

            return "product_list";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/add_product",method = RequestMethod.GET)
    public String addProduct(Model model)
    {
        ProductData productData = new ProductData();
        model.addAttribute("productData", productData);

        String [] categories = new String[3];
        categories[0] = "Główne";
        categories[1] = "Przystawka";
        categories[2] = "Napój";

        String [] sizes = new String[5];
        sizes[0] = "XXL";
        sizes[1] = "XL";
        sizes[2] = "L";
        sizes[3] = "M";
        sizes[4] = "S";

        model.addAttribute("categories", categories);
        model.addAttribute("sizes", sizes);
        return "add_product_form";
    }

    @RequestMapping(value = "/add_product", method = RequestMethod.POST)
    public String addProduct(Model model,ProductData productData)
    {
        try {
            String name = productData.getName();
            String category = productData.getCategory();
            double price = Double.parseDouble(productData.getPrice());
            String size = productData.getSize();
            int kcal = Integer.parseInt(productData.getKcal());
            productRepository.save(new Product(name, category, price, size, kcal));

            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Wstawiono do bazy product: " + name);
            return "viewmessage";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak przy dodawaniu produktu");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/editProduct", method = RequestMethod.GET)
    public String editProduct(@RequestParam(name = "productId")String id,
                             @RequestParam(name = "productName")String productName,
                             @RequestParam(name = "productCategory")String productCategory,
                             @RequestParam(name = "productPrice")String productPrice,
                             @RequestParam(name = "productSize")String productSize,
                             @RequestParam(name = "productKcal")String productKcal,
                             Model model) {

        ProductData productData = new ProductData(productName,productCategory,productPrice,productSize,productKcal);
        model.addAttribute("productData", productData);

        String [] categories = new String[3];
        categories[0] = "Główne";
        categories[1] = "Przystawka";
        categories[2] = "Napój";

        String [] sizes = new String[5];
        sizes[0] = "XXL";
        sizes[1] = "XL";
        sizes[2] = "L";
        sizes[3] = "M";
        sizes[4] = "S";
        model.addAttribute("categories", categories);
        model.addAttribute("sizes", sizes);
        return "add_product_form";
    }

    @RequestMapping(value = "/editProduct", method = RequestMethod.POST)
    public String editProduct(@RequestParam(name = "productId")String id, Model model, ProductData productData)
    {
        try {
            Long productId = Long.parseLong(id);
            Optional<Product> product = productRepository.findById(productId);

            String name = productData.getName();
            String category = productData.getCategory();
            double price = Double.parseDouble(productData.getPrice());
            String size = productData.getSize();
            int kcal = Integer.parseInt(productData.getKcal());

            if(product.isPresent()) {
                product.get().setName(name);
                product.get().setCategory(category);
                product.get().setPrice(price);
                product.get().setSize(size);
                product.get().setKcal(kcal);
                productRepository.save(product.get());
                model.addAttribute("header", "Wynik");
                model.addAttribute("message", "Edycja klienta o id:" + productId);
                return "viewmessage";
            } else {
                model.addAttribute("header", "Wynik");
                model.addAttribute("message", "Nie odnaleziono klienta o taki id:");
                return "viewmessage";
            }
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }


}
