package at.technikum.application.TradingCards.service;

import at.technikum.application.TradingCards.entity.card.Card;
import at.technikum.application.TradingCards.entity.user.User;
import at.technikum.application.TradingCards.repository.CardRepository;
import at.technikum.application.TradingCards.repository.UserRepository;
import at.technikum.application.TradingCards.repository.StatsRepository;
import at.technikum.application.TradingCards.repository.DeckRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ConcurrentHashMap;

public class BattleService {

    private final BlockingQueue<User> requestQueue = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<String, String> battleResults = new ConcurrentHashMap<>();
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final StatsRepository statsRepository;
    private final DeckRepository deckRepository;

    public BattleService(CardRepository cardRepository, StatsRepository statsRepository, UserRepository userRepository,DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.statsRepository = statsRepository;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
    }

    public void addPlayerToQueue(String token) {
        User user = userRepository.findByToken(token);

        if (user == null) {
            throw new IllegalArgumentException("Invalid user token.");
        }

        if (user.getDeck().isEmpty()) {
            throw new IllegalArgumentException("User has no deck.");
        }

        try {
            synchronized (requestQueue) {
                User waitingUser = requestQueue.poll();
                if (waitingUser == null) {
                    requestQueue.put(user);
                } else {
                    String battleLog = handleBattle(waitingUser, user);
                    battleResults.put(waitingUser.getUsername(), battleLog);
                    battleResults.put(user.getUsername(), battleLog);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Battle queue operation was interrupted", e);
        }
    }

    public String getBattleResult(String token) {
        User user = userRepository.findByToken(token);

        if (user == null) {
            throw new IllegalArgumentException("Invalid user token.");
        }

        return battleResults.remove(user.getUsername());
    }

    private String handleBattle(User user1, User user2) {
        ArrayList<Card> deck1 = loadDeck(user1);
        ArrayList<Card> deck2 = loadDeck(user2);

        StringBuilder battleLog = new StringBuilder();
        battleLog.append("Battle started between ").append(user1.getUsername())
                .append(" and ").append(user2.getUsername()).append("\n");

        int round = 1;
        while (round <= 100 && !deck1.isEmpty() && !deck2.isEmpty()) {
            battleLog.append("Round ").append(round).append(":\n");

            Card card1 = deck1.get(ThreadLocalRandom.current().nextInt(deck1.size()));
            Card card2 = deck2.get(ThreadLocalRandom.current().nextInt(deck2.size()));

            if (!card1.canAttack(card2)) {
                battleLog.append(card1.getName()).append(" cannot attack ").append(card2.getName()).append("\n");
                deck1.remove(card1);
                continue;
            }
            if (!card2.canAttack(card1)) {
                battleLog.append(card2.getName()).append(" cannot attack ").append(card1.getName()).append("\n");
                deck2.remove(card2);
                continue;
            }

            double damage1 = card1.calculateDamageAgainst(card2);
            double damage2 = card2.calculateDamageAgainst(card1);

            battleLog.append(user1.getUsername()).append(" played ").append(card1.getName())
                    .append(" (Damage: ").append(damage1).append(")\n");
            battleLog.append(user2.getUsername()).append(" played ").append(card2.getName())
                    .append(" (Damage: ").append(damage2).append(")\n");

            if (damage1 > damage2) {
                deck2.remove(card2);
                deck1.add(card2);
                battleLog.append(user1.getUsername()).append(" wins this round!\n");
            } else if (damage2 > damage1) {
                deck1.remove(card1);
                deck2.add(card1);
                battleLog.append(user2.getUsername()).append(" wins this round!\n");
            } else {
                battleLog.append("It's a draw! No cards are exchanged.\n");
            }
            round++;
        }

        User winner = null;
        if (!deck1.isEmpty() && deck2.isEmpty()) {
            winner = user1;
            battleLog.append(user1.getUsername()).append(" wins the battle!\n");
        } else if (deck1.isEmpty() && !deck2.isEmpty()) {
            winner = user2;
            battleLog.append(user2.getUsername()).append(" wins the battle!\n");
        } else {
            battleLog.append("The battle ended in a draw!\n");
        }

        if (winner != null) {
            adjustElo(user1, user2, winner);
            statsRepository.addWin(winner.getUsername());
            statsRepository.addLoss(winner.equals(user1) ? user2.getUsername() : user1.getUsername());
        }

        return battleLog.toString();
    }

    private ArrayList<Card> loadDeck(User user) {
        ArrayList<Card> deck = new ArrayList<>();

        // Abrufen der Karten-IDs aus dem Deck-Repository
        List<String> userDeck = deckRepository.getDeck(user.getUsername());

        // Überprüfen, ob das Deck leer ist oder null zurückgegeben wurde
        if (userDeck == null || userDeck.isEmpty()) {
            throw new IllegalArgumentException("The user's deck is empty or not initialized.");
        }

        // Laden der Karten basierend auf den IDs
        for (String cardId : userDeck) {
            Card card = cardRepository.findById(cardId);

            if (card == null) {
                throw new IllegalArgumentException("Card with ID " + cardId + " not found.");
            }

            deck.add(card);
        }

        return deck;
    }



    private void adjustElo(User user1, User user2, User winner) {
        int eloChange = 5;
        if (winner.equals(user1)) {
            user1.setElo(user1.getElo() + eloChange);
            user2.setElo(user2.getElo() - eloChange);
        } else {
            user2.setElo(user2.getElo() + eloChange);
            user1.setElo(user1.getElo() - eloChange);
        }

        statsRepository.updateElo(user1.getUsername(), user1.getElo());
        statsRepository.updateElo(user2.getUsername(), user2.getElo());
    }
}
