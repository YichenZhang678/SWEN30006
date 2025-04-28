// LuckyThirteen.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class LuckyThirdteen extends CardGame {

    public enum Suit {
        SPADES ("S", 4), HEARTS ("H", 3),
        DIAMONDS ("D", 2), CLUBS ("C", 1);
        private String suitShortHand = "";
        private int multiplicationFactor = 1;
        public static final int PUBLIC_CARD_MULTIPLICATION_FACTOR = 2;
        Suit(String shortHand, int multiplicationFactor) {
            this.suitShortHand = shortHand;
            this.multiplicationFactor = multiplicationFactor;
        }

        public String getSuitShortHand() {
            return suitShortHand;
        }

        public int getMultiplicationFactor() {
            return multiplicationFactor;
        }
    }

    public enum Rank {
        // Reverse order of rank importance (see rankGreater() below)
        ACE (1, 1, 0, 1),
        KING (13, 13, 10, 11, 12, 13),
        QUEEN (12, 12, 10, 11, 12, 13),
        JACK (11, 11, 10, 11, 12, 13),
        TEN (10, 10, 10), NINE (9, 9, 9),
        EIGHT (8, 8, 8), SEVEN (7, 7, 7),
        SIX (6, 6, 6), FIVE (5, 5, 5),
        FOUR (4, 4, 4), THREE (3, 3, 3),
        TWO (2, 2, 2);

        private int rankCardValue = 1;
        private int scoreValue = 0;
        private int []possibleSumValues = null;
        Rank(int rankCardValue, int scoreValue, int... possibleSumValues) {
            this.rankCardValue = rankCardValue;
            this.scoreValue = scoreValue;
            this.possibleSumValues = possibleSumValues;
        }

        public int getRankCardValue() {
            return rankCardValue;
        }

        public int getScoreCardValue() { return scoreValue; }

        public int[] getPossibleSumValues() {
            return possibleSumValues;
        }

        public String getRankCardLog() {
            return String.format("%d", rankCardValue);
        }
    }

    final String trumpImage[] = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};

    static public final int seed = 30008;
    static final Random random = new Random(seed);
    private Properties properties;
    private StringBuilder logResult = new StringBuilder();
    private List<List<String>> playerAutoMovements = new ArrayList<>();

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    private final String version = "1.0";
    public final int nbPlayers = 4;
    public final int nbStartCards = 2;
    public final int nbFaceUpCards = 2;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private static final int THIRTEEN_GOAL = 13;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };
    private Actor[] scoreActors = {null, null, null, null};
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private int thinkingTime = 2000;
    private int delayTime = 600;
    private Hand[] hands;
    private Player[] players;
    private PlayerAdaptor[] playerAdaptors;
    public void setStatus(String string) {
        setStatusText(string);
    }

    private int[] scores = new int[nbPlayers];

    private int[] autoIndexHands = new int [nbPlayers];
    private boolean isAuto = false;
    private Hand playingArea;
    private Hand pack;

    Font bigFont = new Font("Arial", Font.BOLD, 36);

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            // scores[i] = 0;
            //String text = "[" + String.valueOf(scores[i]) + "]";
            String text = "[" + String.valueOf(playerAdaptors[i].getPlayer().getScore()) + "]";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    private void calculateScoreEndOfRound() {
        List<Boolean> isThirteenChecks = Arrays.asList(false, false, false, false);
        for (int i = 0; i < playerAdaptors.length; i++) {
            playerAdaptors[i].calculateScore(playingArea.getCardList());
            isThirteenChecks.set(i, playerAdaptors[i].getPlayer().getWinState());
        }
        List<Integer> indexesWithThirteen = new ArrayList<>();
        for (int i = 0; i < isThirteenChecks.size(); i++) {
            if (isThirteenChecks.get(i)) {
                indexesWithThirteen.add(i);
            }
        }
        long countTrue = indexesWithThirteen.size();
        Arrays.fill(scores, 0);
        if (countTrue == 1) {
            int winnerIndex = indexesWithThirteen.get(0);
            scores[winnerIndex] = 100;
        } else if (countTrue > 1) {
            for (Integer thirteenIndex : indexesWithThirteen) {
                scores[thirteenIndex] = playerAdaptors[thirteenIndex].getPlayer().getScore();
            }

        } else {
            for (int i = 0; i < scores.length; i++) {
                scores[i] = playerAdaptors[i].getPlayer().getScore();
            }
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        int displayScore = Math.max(scores[player], 0);
        String text = "P" + player + "[" + String.valueOf(displayScore) + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private void initScores() {
        Arrays.fill(scores, 0);
    }

    private Card selected;

    private void initGame() {
        //hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            //hands[i] = new Hand(deck);
            playerAdaptors[i].getPlayer().setHand(deck);
        }
        playingArea = new Hand(deck);
        dealingOut(hands, nbPlayers, nbStartCards, nbFaceUpCards);
        playingArea.setView(this, new RowLayout(trickLocation, (playingArea.getNumberOfCards() + 2) * trickWidth));
        playingArea.draw();

        for (int i = 0; i < nbPlayers; i++) {
            //hands[i].sort(Hand.SortType.SUITPRIORITY, false);
            playerAdaptors[i].getPlayer().sortCard();
        }

        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);

            playerAdaptors[i].getPlayer().getHand().setView(this, layouts[i]);
            playerAdaptors[i].getPlayer().getHand().setTargetArea(new TargetArea(trickLocation));
            playerAdaptors[i].getPlayer().getHand().draw();

        }
    }


    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    public Card getRandomCard(Hand hand) {
        dealACardToHand(hand);

        delay(thinkingTime);

        int x = random.nextInt(hand.getCardList().size());
        return hand.getCardList().get(x);
    }

    private Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        Integer rankValue = Integer.parseInt(rankString);

        for (Rank rank : Rank.values()) {
            if (rank.getRankCardValue() == rankValue) {
                return rank;
            }
        }

        return Rank.ACE;
    }

    private Suit getSuitFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        String suitString = cardName.substring(cardName.length() - 1, cardName.length());
        Integer rankValue = Integer.parseInt(rankString);

        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }


    private Card getCardFromList(List<Card> cards, String cardName) {
        Rank cardRank = getRankFromString(cardName);
        Suit cardSuit = getSuitFromString(cardName);
        for (Card card: cards) {
            if (card.getSuit() == cardSuit
                    && card.getRank() == cardRank) {
                return card;
            }
        }

        return null;
    }

    private Card dealtCard(int playerNum){
        //System.out.println("11111111");

        if(playerAdaptors[playerNum].getPlayer().getIsFinishedAuto()){
            if(playerAdaptors[playerNum].getPlayer() instanceof HumanPlayer){
                setStatus("Player 0 is playing. Please double click on a card to discard");
                return randomCard(pack.getCardList());
            }
            //delay(thinkingTime);
            setStatusText("Player " + playerNum + " thinking...");
            return randomCard(pack.getCardList());
        }
        String playExpectCard = playerAdaptors[playerNum].getPlayer().getNextMovement();
        //System.out.println("ssssssss" +  playExpectCard);
        Card card = getCardFromList(pack.getCardList(), playExpectCard);
        if(card == null){
            //if (pack.isEmpty()) return null;
            //delay(thinkingTime);
            card = randomCard(pack.getCardList());
        }
        return card;
    }

    private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer, int nbSharedCards) {
        pack = deck.toHand(false);

        String initialShareKey = "shared.initialcards";
        String initialShareValue = properties.getProperty(initialShareKey);
        if (initialShareValue != null) {
            String[] initialCards = initialShareValue.split(",");
            for (String initialCard : initialCards) {
                if (initialCard.length() <= 1) {
                    continue;
                }
                Card card = getCardFromList(pack.getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(true);
                    playingArea.insert(card, true);
                }
            }
        }
        int cardsToShare = nbSharedCards - playingArea.getNumberOfCards();

        for (int j = 0; j < cardsToShare; j++) {
            if (pack.isEmpty()) return;
            Card dealt = randomCard(pack.getCardList());
            dealt.removeFromHand(true);
            playingArea.insert(dealt, true);
        }

        for (int i = 0; i < nbPlayers; i++) {
            String initialCardsKey = "players." + i + ".initialcards";
            String initialCardsValue = properties.getProperty(initialCardsKey);
            if (initialCardsValue == null) {
                continue;
            }
            String[] initialCards = initialCardsValue.split(",");
            for (String initialCard: initialCards) {
                if (initialCard.length() <= 1) {
                    continue;
                }
                Card card = getCardFromList(pack.getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    //hands[i].insert(card, false);
                    playerAdaptors[i].getPlayer().insertCard(card);
                }
            }
        }

        for (int i = 0; i < nbPlayers; i++) {
            int cardsToDealt = nbCardsPerPlayer - playerAdaptors[i].getPlayer().getHand().getNumberOfCards();
            for (int j = 0; j < cardsToDealt; j++) {
                if (pack.isEmpty()) return;
                Card dealt = randomCard(pack.getCardList());
                dealt.removeFromHand(false);
                //hands[i].insert(dealt, false);
                //players[i].insertCard(dealt);
                playerAdaptors[i].getPlayer().insertCard(dealt);
            }
        }
    }

    private void dealACardToHand(Hand hand) {
        if (pack.isEmpty()) return;
        Card dealt = randomCard(pack.getCardList());
        dealt.removeFromHand(false);
        hand.insert(dealt, true);
    }

    private void addCardPlayedToLog(int player, List<Card> cards) {
        if (cards.size() < 2) {
            return;
        }
        logResult.append("P" + player + "-");

        for (int i = 0; i < cards.size(); i++) {
            Rank cardRank = (Rank) cards.get(i).getRank();
            Suit cardSuit = (Suit) cards.get(i).getSuit();
            logResult.append(cardRank.getRankCardLog() + cardSuit.getSuitShortHand());
            if (i < cards.size() - 1) {
                logResult.append("-");
            }
        }
        logResult.append(",");
    }

    private void addRoundInfoToLog(int roundNumber) {
        logResult.append("Round" + roundNumber + ":");
    }

    private void addEndOfRoundToLog() {
        logResult.append("Score:");
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
    }

    private void addEndOfGameToLog(List<Integer> winners) {
        logResult.append("EndGame:");
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
        logResult.append("Winners:" + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList())));
    }

    private void playGame() {
        // End trump suit
        int winner = 0;
        int roundNumber = 1;
        for (int i = 0; i < nbPlayers; i++) updateScore(i);

        List<Card>cardsPlayed = new ArrayList<>();  //discard
        addRoundInfoToLog(roundNumber);

        int nextPlayer = 0;
        while(roundNumber <= 4) {
            selected = playerAdaptors[nextPlayer].getSelectedCard(dealtCard(nextPlayer),cardsPlayed,playingArea.getCardList());
            addCardPlayedToLog(nextPlayer, playerAdaptors[nextPlayer].getPlayer().getHand().getCardList());
            if (selected != null) {
                cardsPlayed.add(selected);
                selected.setVerso(false);  // In case it is upside down
                delay(delayTime);
                // End Follow
            }

            nextPlayer = (nextPlayer + 1) % nbPlayers;

            if (nextPlayer == 0) {
                roundNumber ++;
                addEndOfRoundToLog();

                if (roundNumber <= 4) {
                    addRoundInfoToLog(roundNumber);
                }
            }

            if (roundNumber > 4) {
                calculateScoreEndOfRound();
            }
            delay(delayTime);
        }
    }

    private void setupPlayerAutoMovements() {
        String player0AutoMovement = properties.getProperty("players.0.cardsPlayed");
        String player1AutoMovement = properties.getProperty("players.1.cardsPlayed");
        String player2AutoMovement = properties.getProperty("players.2.cardsPlayed");
        String player3AutoMovement = properties.getProperty("players.3.cardsPlayed");

        String[] playerMovements = new String[] {"", "", "", ""};
        if (player0AutoMovement != null) {
            playerMovements[0] = player0AutoMovement;
        }

        if (player1AutoMovement != null) {
            playerMovements[1] = player1AutoMovement;
        }

        if (player2AutoMovement != null) {
            playerMovements[2] = player2AutoMovement;
        }

        if (player3AutoMovement != null) {
            playerMovements[3] = player3AutoMovement;
        }

        for (int i = 0; i < playerMovements.length; i++) {
            String movementString = playerMovements[i];
            if (movementString.equals("")) {
                playerAdaptors[i].getPlayer().setPlayerAutoMovement(new ArrayList<>());
                continue;
            }
            List<String> movements = Arrays.asList(movementString.split(","));
            playerAdaptors[i].getPlayer().setPlayerAutoMovement(movements);
        }
    }

    public String runApp() {
        setTitle("LuckyThirteen (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScores();
        initScore();
        setupPlayerAutoMovements();
        initGame();
        playGame();

        for (int i = 0; i < nbPlayers; i++) updateScore(i);
        int maxScore = 0;
        for (int i = 0; i < nbPlayers; i++) if (scores[i] > maxScore) maxScore = scores[i];
        List<Integer> winners = new ArrayList<Integer>();
        for (int i = 0; i < nbPlayers; i++) if (scores[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText(winText);
        refresh();
        addEndOfGameToLog(winners);

        return logResult.toString();
    }

    public LuckyThirdteen(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        delayTime = Integer.parseInt(properties.getProperty("delayTime", "50"));
        PlayerFactory factory = PlayerFactory.getInstance();
        players = factory.getPlayers(properties);
        playerAdaptors = factory.getPlayerAdaptors(properties);
    }

}
