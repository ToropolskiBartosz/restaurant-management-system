package restaurant.KFC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.KFC.model.*;
import restaurant.KFC.repository.*;
import restaurant.KFC.service.OrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.isNull;

@Controller
public class ControllerOrder {
    private OrderKFC orderKFC;
    private final OrderService service;

    public ControllerOrder(OrderService service) {
        this.service = service;
    }

    @RequestMapping("/menu")
    public String menu(Model model)
    {
        try {
            orderKFC = service.createOrder();
            model.addAttribute("amount", service.getAmount(orderKFC));
            model.addAttribute("header", "Lista produktow");
            model.addAttribute("productList", service.getAll());
            model.addAttribute("header2", "Lista Zestawów");
            model.addAttribute("kfcSetList", service.getKfcSetList());
            return "menu_list";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak"+e+"MENU");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/addProduct",method = RequestMethod.GET)
    public String addProductToOrder(@RequestParam(name = "productId")String id, Model model)
    {
        try{
            service.addProductToOrder(orderKFC,id);
            model.addAttribute("amount", service.getAmount(orderKFC));
            model.addAttribute("header","Lista produktow");
            model.addAttribute("productList",service.getAll());
            model.addAttribute("header2","Lista Zestawów");
            model.addAttribute("kfcSetList",service.getKfcSetList());

            return "menu_list";
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/addKfcSet")
    public String addKfcSet(@RequestParam(name = "nameSet")String name, Model model){
            model.addAttribute("amount", service.getAmount(orderKFC));
            model.addAttribute("kfcSetList", name);
            model.addAttribute("productInSet", service
                    .getListProduct(name, Categories.MAIN.getDescription()));
            model.addAttribute("secondproductInSet", service
                    .getListProduct(name, Categories.APPETIZER.getDescription()));
            model.addAttribute("drinkInSet", service
                    .getListProduct(name, Categories.DRINK.getDescription()));
            model.addAttribute("postData", new KFCSetListData());

            return "selectSet";

    }
    @RequestMapping(value = "/addKfcSet", method = RequestMethod.POST)
    public String addKfcSet(@ModelAttribute("postData") KFCSetListData postData,
                            Model model) {
        try{
            service.addSetToOrder(orderKFC,postData);

            model.addAttribute("amount", service.getAmount(orderKFC));
            model.addAttribute("header","Lista produktow");
            model.addAttribute("productList",service.getAll());
            model.addAttribute("header2","Lista Zestawów");
            model.addAttribute("kfcSetList",service.getKfcSetList());
            return "menu_list";

        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }


    @RequestMapping(value = "/basket",method = RequestMethod.GET)
    public String basket( Model model)
    {
        try{
            orderKFC = service.createOrder();
            //--------------------------------------------------------
            if(isNull(orderKFC)){
                model.addAttribute("header", "Koszyk jest pusty");
                return "viewmessage";
            }else{
                List<ProductInOrder> productsInOrder = service.getProductsInOrder(orderKFC);
                List<ProductInOrder> kfcSetsInOrder = service.getKfcSetInOrder(orderKFC);
                if(productsInOrder.size()==0 & kfcSetsInOrder.size()==0){
                    model.addAttribute("header", "Koszyk jest pusty");
                    return "viewmessage";
                }else{

                    model.addAttribute("amount", service.getAmount(orderKFC));
                    model.addAttribute("header","Lista wszystkich produktow");
                    model.addAttribute("priceFinal",service.getPrice(productsInOrder,kfcSetsInOrder));
                    model.addAttribute("productsInOrder",productsInOrder );
                    model.addAttribute("kfcSetsInOrder",kfcSetsInOrder );

                    return "productInOrder_list";
                    }
            }
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/deleteProduct", method = RequestMethod.GET)
    public String deleteUser(@RequestParam(name = "productId")String id, Model model) {

        try{
            service.delete(id);
            List<ProductInOrder> productsInOrder = service.getProductsInOrder(orderKFC);
            List<ProductInOrder> kfcSetsInOrder = service.getKfcSetInOrder(orderKFC);
            if(productsInOrder.isEmpty() & kfcSetsInOrder.isEmpty()){
                model.addAttribute("header", "Koszyk jest pusty");
                return "viewmessage";
            }else{
                model.addAttribute("amount", service.getAmount(orderKFC));
                model.addAttribute("header","Lista wszystkich produktow"); //Dodanie obiektu do pamieci lokalnej modelu
                model.addAttribute("priceFinal",service.getPrice(productsInOrder,kfcSetsInOrder));
                model.addAttribute("productsInOrder",productsInOrder );
                model.addAttribute("kfcSetsInOrder",kfcSetsInOrder );
                return "productInOrder_list";
        }
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/accept",method = RequestMethod.GET)
    public String accept( Model model)
    {
        try{
            model.addAttribute("customerData", service.getCustomerData());
            return "addressOrder";
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }
    @RequestMapping(value = "/accept", method = RequestMethod.POST)
    public String editPerson(@ModelAttribute("customerData") CustomerData customerData,
                             Model model)
    {
        try {
            long orderId = service.approve(customerData);
            model.addAttribute("header", "Zrealizowano operacje");
            model.addAttribute("message","Zamówienie " + orderId + " zostało zrealizowane");
            return "viewmessage";

        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... coś poszło nie tak");
            return "viewmessage";
        }
    }

}
