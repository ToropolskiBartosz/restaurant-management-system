package restaurant.KFC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import restaurant.KFC.model.*;
import restaurant.KFC.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.isNull;

@Controller
public class ControllerOrder {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    KFC_SetRepository kfc_setRepository;
    @Autowired
    OrderKFCRepository orderKFCRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProductInOrderRepository productInOrderRepository;

    OrderKFC orderKFC;

    @RequestMapping("/menu")
    public String menu(Model model)
    {
        try {
            List<Product> productList = productRepository.findAll();
            //Group by
            ArrayList<DetailsSet> kfcSetList = new ArrayList<>();
            for (String details : kfc_setRepository.findBySQLQuery()) {
                String[] setNamePrice = details.split(",");
                DetailsSet ds = new DetailsSet(setNamePrice[0], setNamePrice[1]);
                kfcSetList.add(ds);
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetails) user).getUsername();
            List<Customer> customerLogin = customerRepository.findByeMail(username);

            //Stworzenie Zamówienia i przypięcie do niego klienta -------------MA BYC CALY CZAS ---------
            List<OrderKFC> notFinishedOrder = orderKFCRepository.findByFinishedAndCustomerId
                    (false, customerLogin.get(0).getId());
            if (notFinishedOrder.size() == 0) {
                //Przypięcie klienta do zamówienia
                customerRepository.save(customerLogin.get(0));
                //-----------------------------
                //Tworzenie nowego zamówienia
                orderKFC = new OrderKFC(dtf.format(now), false);
                orderKFC.setCustomer(customerLogin.get(0));
                orderKFCRepository.save(orderKFC);
                //-------------------------------
                //Wprowadzenie zamówienia do klienta
                customerLogin.get(0).getOrder().add(orderKFC);
                customerRepository.save(customerLogin.get(0));
                //--------------------------
            } else {
                orderKFC = notFinishedOrder.get(0);
            }
            List<ProductInOrder> proInOrd = productInOrderRepository.findByOrderId(orderKFC.getId());
            int amount = 0;
            amount = proInOrd.size();
            model.addAttribute("amount", Integer.toString(amount));
            model.addAttribute("header", "Lista produktow");
            model.addAttribute("productList", productList);
            model.addAttribute("header2", "Lista Zestawów");
            model.addAttribute("kfcSetList", kfcSetList);

            return "menu_list";
        }catch (Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }



    @RequestMapping(value = "/addProduct",method = RequestMethod.GET)
    public String addProductToOrder(@RequestParam(name = "productId")String id, Model model)
    {
        try{
        List<Product> productList =  productRepository.findAll();
        ArrayList<DetailsSet> kfcSetList = new ArrayList<>();
        for(String details:kfc_setRepository.findBySQLQuery()){
            String[] setNamePrice = details.split(",");
            DetailsSet ds = new DetailsSet(setNamePrice[0],setNamePrice[1]);
            kfcSetList.add(ds);
        }

        //Szukanie dodawanego produktu
        Long productId = Long.parseLong(id);
        Optional<Product> addProduct = productRepository.findById(productId);
        //Tworzenie wiersza z produktem
        ProductInOrder productInOrder = new ProductInOrder();
        //Dodanie produktu do koszyka
        productInOrder.setOrder(orderKFC);
        productInOrder.setProduct(addProduct.get());

        orderKFC.getProductInOrder().add(productInOrder);
        addProduct.get().getProductInOrder().add(productInOrder);

        productInOrderRepository.save(productInOrder);
        productRepository.save(addProduct.get());
        orderKFCRepository.save(orderKFC);
        //------------------------------------------

            List<ProductInOrder> proInOrd = productInOrderRepository.findByOrderId(orderKFC.getId());
            int amount = 0;
            amount = proInOrd.size();
            model.addAttribute("amount", Integer.toString(amount));
        model.addAttribute("header","Lista produktow");
        model.addAttribute("productList",productList);
        model.addAttribute("header2","Lista Zestawów");
        model.addAttribute("kfcSetList",kfcSetList);

        return "menu_list";
        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }

    @RequestMapping(value = "/addKfcSet")
    public String addKfcSet(@RequestParam(name = "nameSet")String name, Model model){

            KFCSetListData postData = new KFCSetListData();

            String kfcSetList = name;

            //Kontener zawierający informacje o produktach w danych zestawach
            HashMap<String, ArrayList<String>> productInSet = new HashMap<String, ArrayList<String>>();
            //GETLISTPRODUCT TO WŁASAN METODA! JEST NA DOLE (usuwa dupliktaty)
            //Lista produktów które znajdują się w tym zestawie
            productInSet = getListProduct(kfcSetList, "Główne");

            HashMap<String, ArrayList<String>> secondproductInSet = new HashMap<String, ArrayList<String>>();
            secondproductInSet = getListProduct(kfcSetList, "Przystawka");

            HashMap<String, ArrayList<String>> drinkInSet = new HashMap<String, ArrayList<String>>();
            drinkInSet = getListProduct(kfcSetList, "Napój");

            List<ProductInOrder> proInOrd = productInOrderRepository.findByOrderId(orderKFC.getId());
            int amount = 0;
            amount = proInOrd.size();
            model.addAttribute("amount", Integer.toString(amount));
            model.addAttribute("kfcSetList", kfcSetList);
            model.addAttribute("productInSet", productInSet);
            model.addAttribute("secondproductInSet", secondproductInSet);
            model.addAttribute("drinkInSet", drinkInSet);
            model.addAttribute("postData", postData);

            return "selectSet";

    }
    @RequestMapping(value = "/addKfcSet", method = RequestMethod.POST)
    public String addKfcSet(KFCSetListData postData,Model model)
    {
        try{
        List<Product> productList =  productRepository.findAll();
        ArrayList<DetailsSet> kfcSetList = new ArrayList<>();
        for(String details:kfc_setRepository.findBySQLQuery()){
            String[] setNamePrice = details.split(",");
            DetailsSet ds = new DetailsSet(setNamePrice[0],setNamePrice[1]);
            kfcSetList.add(ds);
        }

        //-------------------------WYSZUKANIE ODPOWIEDNIEGO ZESTAWU ----------------------------
        //WYSZUKIWANIE PRODUKTÓW W DANYCH ZESTAWACH
        List<KFCSet> kfcSetList_Main = kfc_setRepository.findByProductName(postData.getMainProduct());
        ArrayList<Long> ksfSetMain_id = new ArrayList<Long>();
        for(KFCSet kfc:kfcSetList_Main){
            ksfSetMain_id.add(kfc.getId());
        }

        List<KFCSet> kfcSetList_Second = kfc_setRepository.findByProductName(postData.getSecondProduct());
        ArrayList<Long> ksfSetSecond_id = new ArrayList<Long>();
        for(KFCSet kfc:kfcSetList_Second){
            ksfSetSecond_id.add(kfc.getId());
        }

        List<KFCSet> kfcSetList_Drink = kfc_setRepository.findByProductName(postData.getDrink());
        ArrayList<Long> ksfSetDrink_id = new ArrayList<Long>();
        for(KFCSet kfc:kfcSetList_Drink){
            ksfSetDrink_id.add(kfc.getId());
        }
        //WYSZUKANIE NASZEGO ZESTAWU
        ksfSetMain_id.retainAll(ksfSetSecond_id);
        ksfSetMain_id.retainAll(ksfSetDrink_id);

        //POBRANIE ZESTAWU
        Optional<KFCSet> addKfcSet = kfc_setRepository.findById(ksfSetMain_id.get(0));
        System.out.println(addKfcSet.get().getName()+" "+ksfSetMain_id);

        //Tworzenie wiersza z produktem
        ProductInOrder productInOrder = new ProductInOrder();
        //Dodanie produktu do koszyka
        productInOrder.setOrder(orderKFC);
        productInOrder.setKfcSet(addKfcSet.get());

        orderKFC.getProductInOrder().add(productInOrder);
        addKfcSet.get().getProductInOrder().add(productInOrder);

        productInOrderRepository.save(productInOrder);
        kfc_setRepository.save(addKfcSet .get());
        orderKFCRepository.save(orderKFC);

            List<ProductInOrder> proInOrd = productInOrderRepository.findByOrderId(orderKFC.getId());
            int amount = 0;
            amount = proInOrd.size();
            model.addAttribute("amount", Integer.toString(amount));
        model.addAttribute("header","Lista produktow");
        model.addAttribute("productList",productList);
        model.addAttribute("header2","Lista Zestawów");
        model.addAttribute("kfcSetList",kfcSetList);

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
            //Sprawdzanie użytkowanika
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            //Jeśli użytkownik przyciśnie koszyk tworzy się nowe zamówienie
            Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetails) user).getUsername();
            List<Customer> customerLogin = customerRepository.findByeMail(username);
            List<OrderKFC> notFinishedOrder = orderKFCRepository.findByFinishedAndCustomerId
                    (false,customerLogin.get(0).getId());

            if(notFinishedOrder.size() == 0){
                //Tworzenie klienta do testów
                customerRepository.save(customerLogin.get(0));
                //-----------------------------
                //Tworzenie nowego zamówienia
                orderKFC = new OrderKFC(dtf.format(now),false);
                orderKFC.setCustomer(customerLogin.get(0));
                orderKFCRepository.save(orderKFC);
                //-------------------------------
                //Wprowadzenie zamówienia do klienta
                customerLogin.get(0).getOrder().add(orderKFC);
                customerRepository.save(customerLogin.get(0));
                //--------------------------
            }else{
                orderKFC = notFinishedOrder.get(0);
            }
            //--------------------------------------------------------
            if(isNull(orderKFC)){
                model.addAttribute("header", "Koszyk jest pusty");
                return "viewmessage";
            }else{
            List<ProductInOrder> productsInOrder = productInOrderRepository.
                                                    findByOrderIdAndProductNotNull(orderKFC.getId());
            List<ProductInOrder> kfcSetsInOrder = productInOrderRepository.
                                                    findByOrderIdAndKfcSetNotNull(orderKFC.getId());

            if(productsInOrder.size()==0 & kfcSetsInOrder.size()==0){
                model.addAttribute("header", "Koszyk jest pusty");
                return "viewmessage";
            }else{
                double priceFinal = 0;
                for(ProductInOrder pInO : productsInOrder) priceFinal += pInO.getProduct().getPrice();
                for(ProductInOrder kSInO : kfcSetsInOrder) priceFinal += kSInO.getKfcSet().getPrice();

                //Ilość produktów w koszyku
                List<ProductInOrder> proInOrd = productInOrderRepository.findByOrderId(orderKFC.getId());
                int amount = 0;
                amount = proInOrd.size();
                //--------------------
                model.addAttribute("amount", Integer.toString(amount));
                model.addAttribute("header","Lista wszystkich produktow"); //Dodanie obiektu do pamieci lokalnej modelu
                model.addAttribute("priceFinal",Double.toString(priceFinal)); //Dodanie obiektu do pamieci lokalnej modelu
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
        Long longId = Long.parseLong(id);

        productInOrderRepository.deleteById(longId);

        List<ProductInOrder> productsInOrder = productInOrderRepository.findByOrderIdAndProductNotNull(orderKFC.getId());
        List<ProductInOrder> kfcSetsInOrder = productInOrderRepository.findByOrderIdAndKfcSetNotNull(orderKFC.getId());

        if(productsInOrder.isEmpty() & kfcSetsInOrder.isEmpty()){
            model.addAttribute("header", "Koszyk jest pusty");
            return "viewmessage";
        }else{
            double priceFinal = 0;
            for(ProductInOrder pInO : productsInOrder) priceFinal += pInO.getProduct().getPrice();
            for(ProductInOrder kSInO : kfcSetsInOrder) priceFinal += kSInO.getKfcSet().getPrice();

            List<ProductInOrder> proInOrd = productInOrderRepository.findByOrderId(orderKFC.getId());
            int amount = 0;
            amount = proInOrd.size();
            model.addAttribute("amount", Integer.toString(amount));

            model.addAttribute("header","Lista wszystkich produktow"); //Dodanie obiektu do pamieci lokalnej modelu
            model.addAttribute("priceFinal",Double.toString(priceFinal));
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
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) user).getUsername();
        List<Customer> customerLogin = customerRepository.findByeMail(username);

        CustomerData customerData = new CustomerData(customerLogin.get(0).getFirstName(), customerLogin.get(0).getLastName(),
                                                    customerLogin.get(0).geteMail(),customerLogin.get(0).getPassword(),
                                                    Integer.toString(customerLogin.get(0).getAge()), customerLogin.get(0).getPhoneNumber(),
                                                    customerLogin.get(0).getCity(),customerLogin.get(0).getAdres());
        model.addAttribute("customerData", customerData);

        //model.addAttribute("header", "Zrealizowano operacje");
        //model.addAttribute("message","Zamówienie " + orderKFC.getId() + " zostało zrealizowane");
        return "addressOrder";

        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... Coś poszło nie tak");
            return "viewmessage";
        }
    }
    @RequestMapping(value = "/accept", method = RequestMethod.POST)
    public String editPerson(Model model, CustomerData customerData)
    {
        try {
            //aktualizacji obiektu orderKFC
            Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetails) user).getUsername();
            List<Customer> customer = customerRepository.findByeMail(username);
            List<OrderKFC> notFinishedOrder = orderKFCRepository.findByFinishedAndCustomerId
                    (false,customer.get(0).getId());
            orderKFC = notFinishedOrder.get(0);
            //------------------------------

            String phoneNumber = customerData.getPhoneNumber();
            String city = customerData.getCity();
            String adres = customerData.getAdres();

            orderKFC.setPhoneNumber(phoneNumber);
            orderKFC.setCity(city);
            orderKFC.setAdres(adres);
            orderKFC.setFinished(true);
            orderKFCRepository.save(orderKFC);

            model.addAttribute("header", "Zrealizowano operacje");
            model.addAttribute("message","Zamówienie " + orderKFC.getId() + " zostało zrealizowane");
            return "viewmessage";

        }catch(Exception e){
            model.addAttribute("header", "Wynik");
            model.addAttribute("message", "Ups... coś poszło nie tak");
            return "viewmessage";
        }
    }


    //Metoda służąca do pobrania listy produktów w danym zestawie
    public HashMap<String, ArrayList<String>> getListProduct(String nameSets, String category){

        HashMap<String, ArrayList<String>> productInSet = new HashMap<String, ArrayList<String>>();
        ArrayList<String> mainProduct = new ArrayList<>();
            for(Product p : productRepository.findByCategoryAndKfcSetName(category,nameSets)){
                mainProduct.add(p.getName());
            }
            //USUWANIE DUPLIKATÓW
            Set<String> set = new HashSet<>(mainProduct);
            mainProduct.clear();
            mainProduct.addAll(set);
            //--------------------
            productInSet.put(nameSets,(ArrayList<String>) mainProduct.clone());
            mainProduct.clear();

        return productInSet;
    }

}
