package restaurant.KFC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import restaurant.KFC.model.ProductInOrder;
import restaurant.KFC.repository.ProductInOrderRepository;

import java.util.List;


@Controller
public class HomeControler {

    @RequestMapping(value = "/")
    public String manu(Model model)
    {
        return "index";
    }

    @RequestMapping(value = "/login")
    public String login()
    {
        return "login";
    }

    @RequestMapping(value = "/403")
    public String notPermission(Model model)
    {
        model.addAttribute("header", "Ups...");
        model.addAttribute("message", "Nie masz tutaj dostępu");
        return "viewmessage";
    }
}
