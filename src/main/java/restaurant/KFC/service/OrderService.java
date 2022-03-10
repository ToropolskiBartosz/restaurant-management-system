package restaurant.KFC.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.KFC.model.*;
import restaurant.KFC.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final KFC_SetRepository kfc_setRepository;
    private final OrderKFCRepository orderKFCRepository;
    private final CustomerRepository customerRepository;
    private final ProductInOrderRepository productInOrderRepository;

    public OrderService(ProductRepository productRepository,
                        KFC_SetRepository kfc_setRepository,
                        OrderKFCRepository orderKFCRepository,
                        CustomerRepository customerRepository,
                        ProductInOrderRepository productInOrderRepository) {
        this.productRepository = productRepository;
        this.kfc_setRepository = kfc_setRepository;
        this.orderKFCRepository = orderKFCRepository;
        this.customerRepository = customerRepository;
        this.productInOrderRepository = productInOrderRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public int getAmount() {
        OrderKFC orderKFC = getCurrentOrder();
        List< ProductInOrder > proInOrd = productInOrderRepository.findByOrderId(orderKFC.getId());
        int amount = 0;
        amount = proInOrd.size();
        return amount;
    }

    public List<DetailsSet> getKfcSetList() {
        return kfc_setRepository.findBySQLQuery().stream()
                .map(details -> {
                    String[] setNamePrice = details.split(",");
                    return new DetailsSet(setNamePrice[0], setNamePrice[1]);
                }).collect(Collectors.toList());
    }

    @Transactional
    public void createOrder() {
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
            //Tworzenie nowego zamówienia
            var orderKFC = new OrderKFC(dtf.format(now), false);
            orderKFC.setCustomer(customerLogin.get(0));
            orderKFCRepository.save(orderKFC);
            //Wprowadzenie zamówienia do klienta
            customerLogin.get(0).getOrder().add(orderKFC);
            customerRepository.save(customerLogin.get(0));
        }
    }

    public OrderKFC getCurrentOrder(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) user).getUsername();
        List<Customer> customerLogin = customerRepository.findByeMail(username);

        //Stworzenie Zamówienia i przypięcie do niego klienta -------------MA BYC CALY CZAS ---------
        return orderKFCRepository.findByFinishedAndCustomerId
                (false, customerLogin.get(0).getId()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The order wasn't created by system"));
    }


    @Transactional
    public void addProductToOrder(String id) {
        OrderKFC orderKFC = getCurrentOrder();
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

    }

    //Metoda służąca do pobrania listy produktów w danym zestawie
    @Transactional
    public void addSetToOrder(KFCSetListData postData) {
        List<Long> ksfSetMain_id = getCollect(postData.getMainProduct());
        List<Long> ksfSetSecond_id = getCollect(postData.getSecondProduct());
        List<Long> ksfSetDrink_id = getCollect(postData.getDrink());
        OrderKFC orderKFC = getCurrentOrder();
        //WYSZUKANIE NASZEGO ZESTAWU
        ksfSetMain_id.retainAll(ksfSetSecond_id);
        ksfSetMain_id.retainAll(ksfSetDrink_id);

        //POBRANIE ZESTAWU
        KFCSet addKfcSet = kfc_setRepository.findById(ksfSetMain_id.get(0))
                .orElseThrow(() -> new IllegalArgumentException("Set with give ID not found"));
        System.out.println(addKfcSet.getName()+" "+ksfSetMain_id);

        //Tworzenie wiersza z produktem
        ProductInOrder productInOrder = new ProductInOrder();
        //Dodanie produktu do koszyka
        productInOrder.setOrder(orderKFC);
        productInOrder.setKfcSet(addKfcSet);

        orderKFC.getProductInOrder().add(productInOrder);
        addKfcSet.getProductInOrder().add(productInOrder);

        productInOrderRepository.save(productInOrder);
        kfc_setRepository.save(addKfcSet);
        orderKFCRepository.save(orderKFC);
    }

    public List<ProductInOrder> getProductsInOrder() {
        OrderKFC orderKFC = getCurrentOrder();
        return productInOrderRepository.
                findByOrderIdAndProductNotNull(orderKFC.getId());
    }

    public List<ProductInOrder> getKfcSetInOrder() {
        OrderKFC orderKFC = getCurrentOrder();
        return productInOrderRepository.
                findByOrderIdAndKfcSetNotNull(orderKFC.getId());
    }

    public void delete(String id) {
        Long longId = Long.parseLong(id);
        productInOrderRepository.deleteById(longId);
    }

    private List<Long> getCollect(String postData) {
        return kfc_setRepository.findByProductName(postData).stream()
                .map(KFCSet::getId)
                .collect(Collectors.toList());
    }

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

    public double getPrice(List<ProductInOrder> productsInOrder, List<ProductInOrder> kfcSetsInOrder) {
        double priceProduct = productsInOrder.stream()
                .map(ProductInOrder::getProduct)
                .map(Product::getPrice)
                .reduce(0.0, Double::sum);
        double priceSet = kfcSetsInOrder.stream()
                .map(ProductInOrder::getKfcSet)
                .map(KFCSet::getPrice)
                .reduce(0.0, Double::sum);
        return (priceProduct+priceSet);
    }

    public CustomerData getCustomerData() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) user).getUsername();
        List<Customer> customerLogin = customerRepository.findByeMail(username);

        return new CustomerData(
                customerLogin.get(0).getId(),
                customerLogin.get(0).getFirstName(),
                customerLogin.get(0).getLastName(),
                customerLogin.get(0).geteMail(),
                customerLogin.get(0).getPassword(),
                Integer.toString(customerLogin.get(0).getAge()),
                customerLogin.get(0).getPhoneNumber(),
                customerLogin.get(0).getCity(),
                customerLogin.get(0).getAdres());
    }

    public long approve(CustomerData customerData) {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) user).getUsername();
        List<Customer> customer = customerRepository.findByeMail(username);
        List<OrderKFC> notFinishedOrder = orderKFCRepository.findByFinishedAndCustomerId
                (false,customer.get(0).getId());
        OrderKFC orderKFC = notFinishedOrder.get(0);

        orderKFC.setPhoneNumber(customerData.getPhoneNumber());
        orderKFC.setCity(customerData.getCity());
        orderKFC.setAdres(customerData.getAdres());
        orderKFC.setFinished(true);
        return orderKFCRepository.save(orderKFC).getId();

    }
}
