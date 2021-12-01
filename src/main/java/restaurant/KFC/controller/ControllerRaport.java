package restaurant.KFC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import restaurant.KFC.model.KFCSet;
import restaurant.KFC.model.OrderKFC;
import restaurant.KFC.model.Product;
import restaurant.KFC.model.ProductInOrder;
import restaurant.KFC.repository.KFC_SetRepository;
import restaurant.KFC.repository.OrderKFCRepository;
import restaurant.KFC.repository.ProductInOrderRepository;
import restaurant.KFC.repository.ProductRepository;

import java.util.List;

@Controller
public class ControllerRaport {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    KFC_SetRepository kfc_setRepository;
    @Autowired
    OrderKFCRepository orderKFCRepository;
    @Autowired
    ProductInOrderRepository productInOrderRepository;

    @RequestMapping(value = "/printAllAssortment",method = RequestMethod.GET)
    public String printAll(Model model)
    {
        try {
            List<Product> productList = productRepository.findAll();
            List<KFCSet> setList = kfc_setRepository.findAll();

            model.addAttribute("header", "Lista wszystkich produktow"); //Dodanie obiektu do pamieci lokalnej modelu
            model.addAttribute("productList", productList); //Dodanie obiektu do pamieci lokalnej modelu
            model.addAttribute("header2", "Lista Zestawów");
            model.addAttribute("kfcSetList", setList);

            return "allAssortment";
        }catch (Exception e){
            model.addAttribute("header", "Ups...");
            model.addAttribute("message", "Coś poszło nie tak");
            return "viewmessage";
        }
    }
    @RequestMapping(value = "/printDetailsOrder",method = RequestMethod.GET)
    public String printDetailsOrder(Model model)
    {
        try {
            List<ProductInOrder> productsInOrder = productInOrderRepository.
                    findByProductNotNull();
            List<ProductInOrder> kfcSetsInOrder = productInOrderRepository.
                    findByKfcSetNotNull();
            if (productsInOrder.size() <= 0 && kfcSetsInOrder.size() <= 0) {
                model.addAttribute("header", "Pusto");
                model.addAttribute("message", "Nie zrobiono żadnych zamówień");
                return "viewmessage";
            } else {

                double priceFinal = 0;
                for(ProductInOrder pInO : productsInOrder) priceFinal += pInO.getProduct().getPrice();
                double productPrice = priceFinal;
                for(ProductInOrder kSInO : kfcSetsInOrder) priceFinal += kSInO.getKfcSet().getPrice();
                double kfcSetPrice = priceFinal-productPrice;

                model.addAttribute("priceFinal", Double.toString(priceFinal));
                model.addAttribute("productPrice", Double.toString(productPrice));
                model.addAttribute("kfcSetPrice", Double.toString(kfcSetPrice));
                model.addAttribute("amounProduct", Integer.toString(productsInOrder.size()));
                model.addAttribute("amountSet", Integer.toString(kfcSetsInOrder.size()));
                model.addAttribute("header", "Analiza"); //Dodanie obiektu do pamieci lokalnej modelu
                model.addAttribute("productsInOrder", productsInOrder);
                model.addAttribute("kfcSetsInOrder", kfcSetsInOrder);

                return "detailsOrder";
            }
        }catch (Exception e){
            model.addAttribute("header", "Ups...");
            model.addAttribute("message", "Coś poszło nie tak");
            return "viewmessage";
        }
    }
    @RequestMapping(value = "/printProfit",method = RequestMethod.GET)
    public String printProfit(Model model)
    {
        try {
            List<OrderKFC> listOrder = orderKFCRepository.findAll();
            List<OrderKFC> listOrder_amountOrderNotFinish = orderKFCRepository.findByFinished(false);
            int amountOrderNotFinish = listOrder_amountOrderNotFinish.size();

            if (listOrder.isEmpty()) {
                model.addAttribute("header", "Pusto");
                model.addAttribute("message", "Nie zrobiono żadnych zamówień");
                return "viewmessage";
            } else {
                model.addAttribute("amountOrderNotFinish", Integer.toString(amountOrderNotFinish));
                model.addAttribute("header", "Lista wszystkich produktow"); //Dodanie obiektu do pamieci lokalnej modelu
                model.addAttribute("listOrder", listOrder);
                return "list_Order";
            }
        }catch (Exception e){
            model.addAttribute("header", "Ups...");
            model.addAttribute("message", "Coś poszło nie tak");
            return "viewmessage";
        }
    }

}
