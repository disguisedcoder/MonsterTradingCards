package at.technikum.application.TradingCards.DTO;

public class CardDTO {
    private final String id;
    private final String name;
    private final double damage;
    private final String username;

    public CardDTO(String id, String name, double damage, String username) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.username = username;
    }


    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public String getUsername() {
        return username;
    }
}
