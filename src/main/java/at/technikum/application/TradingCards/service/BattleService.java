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

    public BattleService(CardRepository cardRepository,
                         StatsRepository statsRepository,
                         UserRepository userRepository,
                         DeckRepository deckRepository) {
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
                if (requestQueue.isEmpty()) {
                    requestQueue.put(user);
                    while (battleResults.get(user.getUsername()) == null) {
                        requestQueue.wait();
                    }
                } else {
                    User waitingUser = requestQueue.take();
                    String battleLog = handleBattle(waitingUser, user);
                    battleResults.put(waitingUser.getUsername(), battleLog);
                    battleResults.put(user.getUsername(), battleLog);
                    requestQueue.notifyAll();
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
        battleLog.append("Battle started between ")
                .append(user1.getUsername())
                .append(" and ")
                .append(user2.getUsername())
                .append("\n");
        int round = 1;
        while (round <= 100 && !deck1.isEmpty() && !deck2.isEmpty()) {
            battleLog.append("Round ").append(round).append(":\n");
            Card card1 = deck1.get(ThreadLocalRandom.current().nextInt(deck1.size()));
            Card card2 = deck2.get(ThreadLocalRandom.current().nextInt(deck2.size()));
            if (!card1.canAttack(card2)) {
                battleLog.append(card1.getName()).append(" cannot attack ")
                        .append(card2.getName()).append("\n");
                deck1.remove(card1);
                round++;
                continue;
            }
            if (!card2.canAttack(card1)) {
                battleLog.append(card2.getName()).append(" cannot attack ")
                        .append(card1.getName()).append("\n");
                deck2.remove(card2);
                round++;
                continue;
            }
            double damage1 = card1.calculateDamageAgainst(card2);
            double damage2 = card2.calculateDamageAgainst(card1);
            battleLog.append(user1.getUsername()).append(" played ")
                    .append(card1.getName())
                    .append(" (Damage: ")
                    .append(damage1)
                    .append(")\n");
            battleLog.append(user2.getUsername()).append(" played ")
                    .append(card2.getName())
                    .append(" (Damage: ")
                    .append(damage2)
                    .append(")\n");
            if (damage1 > damage2) {
                deck2.remove(card2);
                deck1.add(card2);
                battleLog.append(user1.getUsername()).append(" wins this round!\n");
            } else if (damage2 > damage1) {
                deck1.remove(card1);
                deck2.add(card1);
                battleLog.append(user2.getUsername()).append(" wins this round!\n");
            } else {
                battleLog.append("Round ends in a draw.\n");
                // Bei Gleichstand werden keine Karten entfernt.
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
            // Nach 100 Runden liegt noch immer ein Draw vor.
            battleLog.append("The battle ended in a draw after 100 rounds.\n");
            boolean conductTieBreaker = ThreadLocalRandom.current().nextBoolean();
            if (conductTieBreaker) {
                int tieBreaker1, tieBreaker2;
                do {
                    tieBreaker1 = ThreadLocalRandom.current().nextInt(1, 101);
                    tieBreaker2 = ThreadLocalRandom.current().nextInt(1, 101);
                    battleLog.append("Tie-breaker: ")
                            .append(user1.getUsername()).append(" gets ").append(tieBreaker1)
                            .append(", ")
                            .append(user2.getUsername()).append(" gets ").append(tieBreaker2)
                            .append("\n");
                } while (tieBreaker1 == tieBreaker2);
                if (tieBreaker1 > tieBreaker2) {
                    winner = user1;
                    battleLog.append(user1.getUsername()).append(" wins the tie-breaker!\n");
                } else {
                    winner = user2;
                    battleLog.append(user2.getUsername()).append(" wins the tie-breaker!\n");
                }
            } else {
                battleLog.append("No tie-breaker was conducted. The battle remains a draw.\n");
            }
        }
        // Aufruf von adjustElo: Falls ein Sieger ermittelt wurde, werden wins/losses gesetzt,
        // ansonsten wird für beide ein Draw verzeichnet.
        if (winner != null) {
            adjustElo(user1, user2, winner);
            statsRepository.addWin(winner.getUsername());
            statsRepository.addLoss(winner.equals(user1) ? user2.getUsername() : user1.getUsername());
        } else {
            adjustElo(user1, user2, null);
        }
        return battleLog.toString();
    }



    private ArrayList<Card> loadDeck(User user) {
        ArrayList<Card> deck = new ArrayList<>();
        List<String> userDeck = deckRepository.getDeck(user.getUsername());
        if (userDeck == null || userDeck.isEmpty()) {
            throw new IllegalArgumentException("The user's deck is empty or not initialized.");
        }
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
        int eloChangeLoose = 5;
        int eloChangeWin = 3;    if (winner == null) {
            statsRepository.addDraw(user1.getUsername());
            statsRepository.addDraw(user2.getUsername());
        } else if (winner.equals(user1)) {
            user1.setElo(user1.getElo() + eloChangeWin);
            user2.setElo(user2.getElo() - eloChangeLoose);
        } else {
            user2.setElo(user2.getElo() + eloChangeWin);
            user1.setElo(user1.getElo() - eloChangeLoose);
        }
        statsRepository.updateElo(user1.getUsername(), user1.getElo());
        statsRepository.updateElo(user2.getUsername(), user2.getElo());
        statsRepository.incrementGamesPlayed(user1.getUsername());
        statsRepository.incrementGamesPlayed(user2.getUsername());

        userRepository.updateElo(user1.getUsername(), user1.getElo());
        userRepository.updateElo(user2.getUsername(), user2.getElo());


    }
}
