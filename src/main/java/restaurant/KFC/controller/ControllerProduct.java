package restaurant.KFC.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.KFC.model.*;
import restaurant.KFC.repository.*;
import restaurant.KFC.service.ProductService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ControllerProduct {
    ProductService service;

    public ControllerProduct(ProductService service) {
        this.service = service;
    }

    @RequestMapping(value = "/print_all_product",method = RequestMethod.GET)
    public String printAll(Model model)
    {
        try {
            model.addAttribute("header", "Lista wszystkich produktow");
            model.addAttribute("productList", service.getAll());
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
        model.addAttribute("categories", service.getCategories());
        model.addAttribute("sizes", service.getSize());
        return "add_product_form";
    }

    @RequestMapping(value = "/add_product", method = RequestMethod.POST)
    public String addProduct(@ModelAttribute("productData") ProductData productData,
                             Model model)
    {
        try {
            service.save(productData);
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Wstawiono do bazy product: " + productData.getName());
            return "viewmessage";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak przy dodawaniu produktu");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/editProduct/{id}", method = RequestMethod.GET)
    public String editProduct(@PathVariable long id,
                             Model model) {
        ProductData productData = service.getProduct(id);

        model.addAttribute("productData", productData);
        model.addAttribute("categories", service.getCategories());
        model.addAttribute("sizes", service.getSize());
        return "product_update";
    }

    @RequestMapping(value = "/editProduct", method = RequestMethod.POST)
    public String editProduct(@ModelAttribute("productData") ProductData productData, Model model)
    {
        try {
            service.updateProduct(productData);
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Edycja klienta o id:" + productData.getId());
            return "viewmessage";
        }catch (IllegalArgumentException e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Nie odnaleziono klienta o takim id:" + productData.getId());
            return "viewmessage";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }


}
