package at.technikum.application.TradingCards.DTO;

public class UserDTO {

    private String username;
    private String password;
    private int elo;
    private int coins;

    public UserDTO(String username, String password, int elo, int coins) {
        this.username = username;
        this.password = password;
        this.elo = elo;
        this.coins = coins;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public int getElo() {
        return elo;
    }

    public int getCoins() {
        return coins;
    }

    public String getPassword() {
        return password;
    }
}
