package restaurant.KFC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.KFC.model.*;
import restaurant.KFC.repository.KFC_SetRepository;
import restaurant.KFC.repository.ProductInOrderRepository;
import restaurant.KFC.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Controller
public class ControllerKFCSet {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    KFC_SetRepository kfc_setRepository;
    @Autowired
    ProductInOrderRepository productInOrderRepository;

    @RequestMapping(value = "/print_all_Set",method = RequestMethod.GET)
    public String printAll(Model model)
    {
        try {
            List<KFCSet> setList = kfc_setRepository.findAll();
            List<Product> productList = productRepository.findAll();
            List<ProductInOrder> proInOrd = productInOrderRepository.findAll();

            model.addAttribute("header", "Lista wszystkich produktow"); //Dodanie obiektu do pamieci lokalnej modelu
            model.addAttribute("productList", setList);

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
        // Pobieranie produktów z kategorii główne dania
        List<Product> productsMain = productRepository.findByCategory("Główne");
        ArrayList<String> productsMainName = new ArrayList<String>();
        for(Product p : productsMain) productsMainName.add(p.getName()+" "+p.getSize());
        // Pobieranie produktów z kategorii przystawka
        List<Product> productsSecond = productRepository.findByCategory("Przystawka");
        ArrayList<String> productsSecondName = new ArrayList<String>();
        for(Product p : productsSecond) productsSecondName.add(p.getName()+" "+p.getSize());
        // Pobieranie produktów z kategorii przystawka
        List<Product> productsDrink = productRepository.findByCategory("Napój");
        ArrayList<String> productsDrinkName = new ArrayList<String>();
        for(Product p : productsDrink) productsDrinkName.add(p.getName()+" "+p.getSize());

        model.addAttribute("kfcSetListData", kfcSetListData);
        model.addAttribute("productsMainName", productsMainName);
        model.addAttribute("productsSecondName", productsSecondName);
        model.addAttribute("productsDrinkName", productsDrinkName);
        return "add_Set_form";
    }

    @RequestMapping(value = "/add_Set", method = RequestMethod.POST)
    public String addProduct(Model model,KFCSetListData kfcSetListData)
    {
        try {
            String name = kfcSetListData.getName();
            double price = Double.parseDouble(kfcSetListData.getPrice());
            KFCSet kfc = new KFCSet(name, price);
            kfc_setRepository.save(kfc);

            //Pobranie jakie produkty zostały wybrane i wyszukanie ich w bazie danych
            String[] describeMainProduct = getDetails(kfcSetListData.getMainProduct());
            List<Product> mainProduct = productRepository.findByNameAndSize(describeMainProduct[0], describeMainProduct[1]);
            String[] describeSecondProduct = getDetails(kfcSetListData.getSecondProduct());
            List<Product> secondProduct = productRepository.findByNameAndSize(describeSecondProduct[0], describeSecondProduct[1]);
            String[] describeDrinkProduct = getDetails(kfcSetListData.getDrink());
            List<Product> drinkProduct = productRepository.findByNameAndSize(describeDrinkProduct[0], describeDrinkProduct[1]);

            //Dodanie relacji z produktem głównym
            kfc.getProduct().add(mainProduct.get(0));
            mainProduct.get(0).getKfcSet().add(kfc);
            //Dodanie relacji z przystawką
            kfc.getProduct().add(secondProduct.get(0));
            secondProduct.get(0).getKfcSet().add(kfc);
            //Dodanie relacji z napojem
            kfc.getProduct().add(drinkProduct.get(0));
            drinkProduct.get(0).getKfcSet().add(kfc);

            kfc_setRepository.save(kfc);
            productRepository.save(mainProduct.get(0));
            productRepository.save(secondProduct.get(0));
            productRepository.save(drinkProduct.get(0));

            model.addAttribute("header", "Pomyślnie dodałeś nowy zestaw");

            return "viewmessage";
        }catch(Exception  e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }
    @RequestMapping(value = "/editKFCSet", method = RequestMethod.GET)
    public String editKFCSet(@RequestParam(name = "productId")String id,
                             @RequestParam(name = "productName")String name,
                             @RequestParam(name = "productPrice")String price,
                             Model model) {

        KFCSetListData kfcSetListData = new KFCSetListData(name,price);
        // Pobieranie produktów z kategorii główne dania
        List<Product> productsMain = productRepository.findByCategory("Główne");
        ArrayList<String> productsMainName = new ArrayList<String>();
        for(Product p : productsMain) productsMainName.add(p.getName()+" "+p.getSize());
        // Pobieranie produktów z kategorii przystawka
        List<Product> productsSecond = productRepository.findByCategory("Przystawka");
        ArrayList<String> productsSecondName = new ArrayList<String>();
        for(Product p : productsSecond) productsSecondName.add(p.getName()+" "+p.getSize());
        // Pobieranie produktów z kategorii przystawka
        List<Product> productsDrink = productRepository.findByCategory("Napój");
        ArrayList<String> productsDrinkName = new ArrayList<String>();
        for(Product p : productsDrink) productsDrinkName.add(p.getName()+" "+p.getSize());

        model.addAttribute("kfcSetListData", kfcSetListData);
        model.addAttribute("productsMainName", productsMainName);
        model.addAttribute("productsSecondName", productsSecondName);
        model.addAttribute("productsDrinkName", productsDrinkName);

        return "add_Set_form";
    }

    @RequestMapping(value = "/editKFCSet", method = RequestMethod.POST)
    public String editKFCSet(@RequestParam(name = "productId")String id, Model model, KFCSetListData kfcSetListData)
    {
        try {
            Long setId = Long.parseLong(id);
            Optional<KFCSet> kfc = kfc_setRepository.findById(setId);

            if (kfc.isPresent()) {
                List<Product> remove_p = productRepository.findByKfcSetId(kfc.get().getId());
                for (Product p : remove_p) {
                    p.removeKFCSetFromProduct(kfc.get());
                    productRepository.save(p);
                }
                //odświerzenie listy
                List<Product> remove_p2 = productRepository.findByKfcSetId(kfc.get().getId());

                String name = kfcSetListData.getName();
                double price = Double.parseDouble(kfcSetListData.getPrice());
                kfc.get().setName(name);
                kfc.get().setPrice(price);
                kfc_setRepository.save(kfc.get());

                //Pobranie jakie produkty zostały wybrane i wyszukanie ich w bazie danych
                String[] describeMainProduct = getDetails(kfcSetListData.getMainProduct());
                List<Product> mainProduct = productRepository.findByNameAndSize(describeMainProduct[0], describeMainProduct[1]);
                String[] describeSecondProduct = getDetails(kfcSetListData.getSecondProduct());
                List<Product> secondProduct = productRepository.findByNameAndSize(describeSecondProduct[0], describeSecondProduct[1]);
                String[] describeDrinkProduct = getDetails(kfcSetListData.getDrink());
                List<Product> drinkProduct = productRepository.findByNameAndSize(describeDrinkProduct[0], describeDrinkProduct[1]);

                //Dodanie relacji z produktem głównym

                kfc.get().getProduct().clear();
                kfc.get().getProduct().add(mainProduct.get(0));
                mainProduct.get(0).getKfcSet().add(kfc.get());
                //Dodanie relacji z przystawką
                kfc.get().getProduct().add(secondProduct.get(0));
                secondProduct.get(0).getKfcSet().add(kfc.get());
                //Dodanie relacji z napojem
                kfc.get().getProduct().add(drinkProduct.get(0));
                drinkProduct.get(0).getKfcSet().add(kfc.get());

                kfc_setRepository.save(kfc.get());
                productRepository.save(mainProduct.get(0));
                productRepository.save(secondProduct.get(0));
                productRepository.save(drinkProduct.get(0));

                model.addAttribute("header", "Wynik");

                return "viewmessage";
            } else {
                model.addAttribute("header", "Wynik");
                model.addAttribute("message", "Nie znaleziono takiego produktu");
                return "viewmessage";
            }
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }

    public String[] getDetails(String info){
        String[] product = new String[2];
        StringTokenizer st = new StringTokenizer(info, " ");
        int i = 0;
        while (st.hasMoreTokens()) {
            product[i] = st.nextToken();
            i++;
        }
        return product;
    }
}
