package restaurant.KFC.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.KFC.model.Categories;
import restaurant.KFC.model.KFCSet;
import restaurant.KFC.model.KFCSetListData;
import restaurant.KFC.model.Product;
import restaurant.KFC.repository.KFC_SetRepository;
import restaurant.KFC.repository.ProductInOrderRepository;
import restaurant.KFC.repository.ProductRepository;

import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@Service
public class KfcSetService {
    private final ProductRepository productRepository;
    private final KFC_SetRepository kfc_setRepository;


    public KfcSetService(ProductRepository productRepository,
                         KFC_SetRepository kfc_setRepository) {
        this.productRepository = productRepository;
        this.kfc_setRepository = kfc_setRepository;
    }

    public List<KFCSet> getAll() {
        return kfc_setRepository.findAll();
    }

    public KFCSetListData getSetKfc(long id) {
        return kfc_setRepository.findById(id)
                .map(kfcSet -> {
                    var result = new KFCSetListData();
                    result.setId(kfcSet.getId());
                    result.setName(kfcSet.getName());
                    result.setPrice(Double.toString(kfcSet.getPrice()));
                    return result;
                }).orElseThrow(() -> new IllegalArgumentException("setKFC with give id not found"));
    }

    private List<String> getProductByCategories(String categoriesOfFoot) {
        List<Product> products = productRepository.findByCategory(categoriesOfFoot);
        return products.stream()
                .map(product -> product.getName()+" "+product.getSize())
                .collect(Collectors.toList());
    }

    public List<String> getProductByNameAndSize(){
        return getProductByCategories(Categories.MAIN.getDescription());
    }

    public List<String> getAppetizerProduct(){
        return getProductByCategories(Categories.APPETIZER.getDescription());
    }

    public List<String> getDrinkProduct(){
        return getProductByCategories(Categories.DRINK.getDescription());
    }


    @Transactional
    public void save(KFCSetListData kfcSetListData) {
        String name = kfcSetListData.getName();
        double price = Double.parseDouble(kfcSetListData.getPrice());
        KFCSet kfc = new KFCSet(name, price);
        kfc_setRepository.save(kfc);

        saveToDB(kfcSetListData, kfc);
    }

    @Transactional
    public void updateKfcSet(KFCSetListData kfcSetListData) {
        KFCSet kfc = kfc_setRepository.findById(kfcSetListData.getId())
                .map(kfcset -> {
                    removeProductFromKfcSet(kfcset);
                    return kfcset;
                }).orElseThrow(() -> new IllegalArgumentException("set with give id not found"));
        List<Product> remove_p2 = productRepository.findByKfcSetId(kfc.getId());

        String name = kfcSetListData.getName();
        double price = Double.parseDouble(kfcSetListData.getPrice());
        kfc.setName(name);
        kfc.setPrice(price);
        //kfc_setRepository.save(kfc);
        kfc.getProduct().clear();
        saveToDB(kfcSetListData, kfc);
    }

    private void saveToDB(KFCSetListData kfcSetListData, KFCSet kfc) {
        //Pobranie jakie produkty zostały wybrane i wyszukanie ich w bazie danych
        String[] describeMainProduct = getDetails(kfcSetListData.getMainProduct());
        List<Product> mainProduct = getProductByNameAndSize(describeMainProduct);
        String[] describeSecondProduct = getDetails(kfcSetListData.getSecondProduct());
        List<Product> secondProduct = getProductByNameAndSize(describeSecondProduct);
        String[] describeDrinkProduct = getDetails(kfcSetListData.getDrink());
        List<Product> drinkProduct = getProductByNameAndSize(describeDrinkProduct);

        //Dodanie relacji z produktem głównym
        kfc.addProduct(mainProduct.get(0));
        kfc.addProduct(secondProduct.get(0));
        kfc.addProduct(drinkProduct.get(0));

        kfc_setRepository.save(kfc);
        productRepository.save(mainProduct.get(0));
        productRepository.save(secondProduct.get(0));
        productRepository.save(drinkProduct.get(0));
    }

    private List<Product> getProductByNameAndSize(String[] describeMainProduct) {
        return productRepository.findByNameAndSize(describeMainProduct[0], describeMainProduct[1]);
    }

    private void removeProductFromKfcSet(KFCSet kfc) {
        productRepository.findByKfcSetId(kfc.getId())
                .forEach(product -> {
                    product.removeKFCSetFromProduct(kfc);
                    productRepository.save(product);
                });
    }


    private String[] getDetails(String info){
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
