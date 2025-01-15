package at.technikum.application.TradingCards.DTO;

public class UserDTO {

    private String username;
    private String password;
    private int elo;
    private int coins;

    public UserDTO() {
        // Standardkonstruktor
    }

    public UserDTO(String username, String password, int elo, int coins) {
        this.username = username;
        this.password = password;
        this.elo = elo;
        this.coins = coins;
    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
        this.elo = 0; // Standardwert für Elo
        this.coins = 0; // Standardwert für Coins
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
