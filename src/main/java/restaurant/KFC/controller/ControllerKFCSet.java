package restaurant.KFC.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import restaurant.KFC.model.*;
import restaurant.KFC.repository.KFC_SetRepository;
import restaurant.KFC.repository.ProductInOrderRepository;
import restaurant.KFC.repository.ProductRepository;
import restaurant.KFC.service.KfcSetService;

@Controller
public class ControllerKFCSet {
    KfcSetService service;

    public ControllerKFCSet(KfcSetService service) {
        this.service = service;
    }

    @RequestMapping(value = "/print_all_Set",method = RequestMethod.GET)
    public String printAll(Model model)
    {
        try {
            model.addAttribute("header", "Lista wszystkich produktow");
            model.addAttribute("productList", service.getAll());
            return "kfcSet_list";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak przy rejestracji");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/add_Set",method = RequestMethod.GET)
    public String addProduct(Model model)
    {
        KFCSetListData kfcSetListData = new KFCSetListData();
        model.addAttribute("kfcSetListData", kfcSetListData);
        model.addAttribute("productsMainName", service.getProductByNameAndSize());
        model.addAttribute("productsSecondName", service.getAppetizerProduct());
        model.addAttribute("productsDrinkName", service.getDrinkProduct());
        return "add_Set_form";
    }

    @RequestMapping(value = "/add_Set", method = RequestMethod.POST)
    public String addProduct(Model model,KFCSetListData kfcSetListData)
    {
        try {
            service.save(kfcSetListData);
            model.addAttribute("header", "Pomyślnie dodałeś nowy zestaw");
            return "viewmessage";
        }catch(Exception  e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }
    @RequestMapping(value = "/editKFCSet/{id}", method = RequestMethod.GET)
    public String editKFCSet(@PathVariable long id,
                             Model model) {

        KFCSetListData kfcSetListData = service.getSetKfc(id);

        model.addAttribute("kfcSetListData", kfcSetListData);
        model.addAttribute("productsMainName", service.getProductByNameAndSize());
        model.addAttribute("productsSecondName", service.getAppetizerProduct());
        model.addAttribute("productsDrinkName", service.getDrinkProduct());

        return "kfcSet_update";
    }

    @RequestMapping(value = "/editKFCSet", method = RequestMethod.POST)
    public String editKFCSet(@ModelAttribute("kfcSetListData") KFCSetListData kfcSetListData,
                             Model model)
    {
        try {
            service.updateKfcSet(kfcSetListData);
            model.addAttribute("header", "Wynik");
            return "viewmessage";
        }catch(IllegalArgumentException e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }
}
