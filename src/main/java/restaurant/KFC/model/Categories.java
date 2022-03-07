package restaurant.KFC.model;

public enum Categories {
    MAIN("Główne"),
    APPETIZER("Przystawka"),
    DRINK("Napój");

    private String description;

    Categories(String description) {
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

}
