package at.technikum.application.TradingCards.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {

    private String username;
    private String password;
    private int elo;
    private int coins;
    private String name; // New field
    private String bio;  // New field
    private String image; // New field

    public UserDTO() {
        // Standardkonstruktor
    }
    public UserDTO(String name, String bio, String image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }
    public UserDTO(String username, String password, int elo, int coins) {
        this.username = username;
        this.password = password;
        this.elo = elo;
        this.coins = coins;
    }

    public UserDTO(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("name") String name,
            @JsonProperty("bio") String bio,
            @JsonProperty("image") String image) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.elo = 0; // Default value
        this.coins = 0; // Default value
    }

    public UserDTO(String username, String password, int elo, int coins, String name, String bio, String image) {
        this.username = username;
        this.password = password;
        this.elo = elo;
        this.coins = coins;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    // Getters and Setters
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

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
