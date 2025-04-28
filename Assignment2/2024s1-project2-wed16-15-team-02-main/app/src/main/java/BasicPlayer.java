import ch.aplu.jcardgame.*;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jgamegrid.GameGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static java.lang.System.in;

public class BasicPlayer extends Player{
    private int score;
    private List<String> playerAutoMovement;
    private Hand hand;
    private Card selected = null;
    private String nextMovement = "";
    private int autoMovementIndex = 0;
    private boolean finishedAuto = true;
    private int delayTime = 600;

    static public final int seed = 30008;
    private int thinkingTime = 2000;
    static final Random random = new Random(seed);
    private boolean isWin = false;

    public BasicPlayer (){
        this.score = 0;
        //this.hand = new Hand(deck);
    }

    public Card selectCard(Card dealt){
        applyAutoMovement(dealt);
        //while (null == selected) GameGrid.delay(delayTime);
        selected.removeFromHand(true);
        Card dealtcard = selected;
        selected = null;
        return dealtcard;
    }

    protected Card applyAutoMovement(Card dealt) {
        dealt.removeFromHand(false);
        hand.insert(dealt, true);
        GameGrid.delay(thinkingTime);
        List<Card> privateCards = hand.getCardList();
        Card privateCard1 = privateCards.get(0);
        Card privateCard2 = privateCards.get(1);
        Card privateCard3 = privateCards.get(2);
        int score1 = getScorePrivateCard(privateCard1);
        int score2 = getScorePrivateCard(privateCard2);
        int score3 = getScorePrivateCard(privateCard3);
        if(Math.min(score1, Math.min(score2, score3)) == score1){
            System.out.println("card1");
            selected = privateCard1;
            return selected;
        }else if(Math.min(score1, Math.min(score2, score3)) == score2){
            System.out.println("card2");
            selected = privateCard2;
            return selected;
        }
        System.out.println("card3");
        selected = privateCard3;
        return selected;
    }

    public int getScore(){
        return score;
    }

    public void setPlayerAutoMovement(List<String> movement){
        this.playerAutoMovement = movement;
        if(playerAutoMovement.isEmpty()){
            finishedAuto = true;
            return;
        }
        this.nextMovement = playerAutoMovement.get(autoMovementIndex);
        this.autoMovementIndex++;
    }

    public void calculateScore(List<Card> publicCards){
        ScoreStrategy strategy = ScoreStrategy.getInstance();
        if(strategy.getSum(hand.getCardList(), publicCards)){
            this.isWin = true;
        }
        this.score = strategy.getScore(hand.getCardList(), publicCards);
    }

    private int getScorePrivateCard(Card card) {
        LuckyThirdteen.Rank rank = (LuckyThirdteen.Rank) card.getRank();
        LuckyThirdteen.Suit suit = (LuckyThirdteen.Suit) card.getSuit();

        return rank.getScoreCardValue() * suit.getMultiplicationFactor();
    }

    public void setHand(Deck deck){
        this.hand =new Hand(deck);
    }

    public void insertCard(Card card){
        hand.insert(card, false);
    }

    public void sortCard(){
        hand.sort(Hand.SortType.SUITPRIORITY, false);
    }

    public Hand getHand(){
        return hand;
    }

    public String getNextMovement(){
        return nextMovement.split("-")[0];
    }

    public boolean getIsFinishedAuto(){
        return finishedAuto;
    }
    public boolean getWinState(){
        return isWin;
    }

}