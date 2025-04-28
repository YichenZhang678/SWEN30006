import ch.aplu.jcardgame.*;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jgamegrid.GameGrid;

import java.util.*;

import static java.lang.System.in;

public class CleverPlayer extends Player{
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

    private List<Card> deckRemain;  //virtual deck act as player memory of deck

    public CleverPlayer (){
        this.score = 0;
        Deck temp = new Deck(LuckyThirdteen.Suit.values(), LuckyThirdteen.Rank.values(), "cover");
        for (Card[] cards : temp.cards){
            Collections.addAll(deckRemain, cards);
        }
    }

    public Card selectCard(Card dealt){
        applyAutoMovement(dealt);
        selected.removeFromHand(true);
        Card dealtcard = selected;
        selected = null;
        return dealtcard;
    }


    @Override
    public Card selectCard(Card dealt,List<Card>discardCards,List<Card> publicCards){  //override
        deckRemain.remove(dealt);
        for (Card card :discardCards){
            deckRemain.remove(card);
        }
        for (Card card : publicCards){
            deckRemain.remove(card);
        }

        ArrayList<Card> currentHand = this.hand.getCardList();
        currentHand.add(dealt);
        Card toRemove = null;
        Boolean ifWin = false;
        int maxScore = -1;
        int tempScore = 0;
        int winCount = -1;
        int tempCount;
        //situation for each
        for (Card card : currentHand){
            ArrayList<Card> temp = new ArrayList<>(currentHand);
            temp.remove(card);
            tempScore = ScoreStrategy.getInstance().getScore(temp,publicCards);
            if(tempScore > maxScore){
                ifWin = true;
                maxScore = tempScore;
                toRemove = card;
                continue;
            }

            //if can not, see which one has the higher chance to thirteen
            if (!ifWin) {
                tempCount = 0;
                for (Card card1 : deckRemain) {
                    for (Card privateCard : temp) {
                        List<Card> future = new ArrayList<>();
                        future.add(card1);
                        future.add(privateCard);
                        if (ScoreStrategy.getInstance().getScore(future, publicCards) > 0) {
                            tempCount += 1;
                        }
                    }
                }
                if (winCount < tempCount) {
                    winCount = tempCount;
                    toRemove = card;
                    continue;
                }
            }

        }
        return toRemove;
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