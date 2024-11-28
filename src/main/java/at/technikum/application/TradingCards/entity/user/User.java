package at.technikum.application.TradingCards.entity.user;


import at.technikum.application.TradingCards.entity.card.Card;

import java.util.ArrayList;
import java.util.List;

public class User {
    //username must be unique
    private String username;
    private String password;
    private String token;
    private int coins;
    private int elo;
    private List<Card> stack;
    private List<Card> deck;

    public User(){
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = generateToken();
        this.coins = 20;
        this.elo = 100;
        this.stack = new ArrayList<>();
        this.deck = new ArrayList<>();
    }

    private String generateToken() {
        return this.username + "-mtcgToken";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public int getCoins() {
        return coins;
    }

    public int getElo() {
        return elo;
    }

    public List<Card> getStack() {
        return stack;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public void setStack(List<Card> stack) {
        this.stack = stack;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }
}
