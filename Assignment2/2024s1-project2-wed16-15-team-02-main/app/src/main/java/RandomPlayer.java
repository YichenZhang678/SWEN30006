import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.GameGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static java.lang.System.in;

public class RandomPlayer extends Player{
    private int score;
    private List<String> playerAutoMovement;
    private Hand hand;
    private Card selected = null;
    private String nextMovement = "";
    private int autoMovementIndex = 0;
    private boolean finishedAuto = false;
    private int delayTime = 600;

    static public final int seed = 30008;
    private int thinkingTime = 2000;
    static final Random random = new Random(seed);
    private boolean isWin = false;

    public RandomPlayer (){
        this.score = 0;
    }

    public Card selectCard(Card dealt){
        if(!finishedAuto){
            if (playerAutoMovement.size() >= autoMovementIndex) {
                // Apply movement for player
                selected = applyAutoMovement(dealt);
                GameGrid.delay(delayTime);
                if (selected != null) {
                    selected.removeFromHand(true);
                } else{
                    GameGrid.delay(thinkingTime);
                    int x = random.nextInt(hand.getCardList().size());
                    selected =  hand.getCardList().get(x);
                    selected.removeFromHand(true);
                }
                if(autoMovementIndex != playerAutoMovement.size()){
                    nextMovement = playerAutoMovement.get(autoMovementIndex);
                    autoMovementIndex++;
                }
            } else {
                finishedAuto = true;
            }

        }

        if(finishedAuto) {
            dealt.removeFromHand(false);
            hand.insert(dealt, true);
            GameGrid.delay(thinkingTime);
            int x = random.nextInt(hand.getCardList().size());
            selected =  hand.getCardList().get(x);
            selected.removeFromHand(true);
        }

        return selected;
    }

    protected Card applyAutoMovement(Card dealt) {
        String[] cardStrings = nextMovement.split("-");
        String cardDealtString = cardStrings[0];
        if (dealt != null) {
            dealt.removeFromHand(false);
            hand.insert(dealt, true);
        } else {
            System.out.println("cannot draw card: " + cardDealtString + " - hand: " + hand);
        }

        if (cardStrings.length > 1) {
            String cardDiscardString = cardStrings[1];
            return getCardFromList(hand.getCardList(), cardDiscardString);
        } else {
            return null;
        }
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