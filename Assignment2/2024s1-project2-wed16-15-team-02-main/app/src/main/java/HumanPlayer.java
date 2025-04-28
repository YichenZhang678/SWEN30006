import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.GameGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static java.lang.System.in;

public class HumanPlayer extends Player{
    private int score;
    private List<String> playerAutoMovement;
    private Hand hand;
    private Card selected = null;
    private String nextMovement = "";
    private int autoMovementIndex = 0;
    private boolean finishedAuto = false;
    private boolean listenerSet = false;
    private int delayTime = 600;

    static public final int seed = 30008;
    private int thinkingTime = 2000;
    static final Random random = new Random(seed);
    private boolean isWin = false;

    public HumanPlayer (){
        this.score = 0;
        //this.hand = new Hand(deck);
        this.finishedAuto = true;
    }

    public Card selectCard(Card dealt){
        if(!listenerSet){
            CardListener cardListener = new CardAdapter()  // Human Player plays card
            {
                public void leftDoubleClicked(Card card) {
                    selected = card;
                    hand.setTouchEnabled(false);
                }
            };
            hand.addCardListener(cardListener);
            listenerSet = true;
        }
        hand.setTouchEnabled(true);

        applyAutoMovement(dealt);
        selected.removeFromHand(true);
        Card dealtcard = selected;
        selected = null;
        return dealtcard;
    }

    protected Card applyAutoMovement(Card dealt) {
        dealt.removeFromHand(false);
        hand.insert(dealt, true);
        while (null == selected) GameGrid.delay(delayTime);
        //selected.removeFromHand(true);
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
