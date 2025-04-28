import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.List;

public class ScoreStrategy{
    private static ScoreStrategy instance;
    ArrayList<ScoreOption> options = new ArrayList<>();
    private ScoreStrategy() {
        //add future strategy here
        options.add(new OptionPrivate());
        options.add(new OptionMix());
        options.add(new OptionAll());
    }

    public static ScoreStrategy getInstance() {
        if (instance == null) {
            instance = new ScoreStrategy();
        }
        return instance;
    }
    
    public int getScore(List<Card>privateCards,List<Card>publicCards){
        if(!getSum(privateCards, publicCards)){
            return getScorePrivateCard(privateCards.get(0)) + getScorePrivateCard(privateCards.get(1));
        }
        int max = 0;
        for (ScoreOption option : options){
            if(option.score(privateCards,publicCards )>max){
                max = option.score(privateCards,publicCards);
            }
        }
        return max;
    }

    public boolean getSum(List<Card>privateCards,List<Card>publicCards){
        for (ScoreOption option : options) {
            if (option.sum(privateCards, publicCards)) {
                return true;
            }
        }
        return false;
    }

    private int getScorePrivateCard(Card card) {
        LuckyThirdteen.Rank rank = (LuckyThirdteen.Rank) card.getRank();
        LuckyThirdteen.Suit suit = (LuckyThirdteen.Suit) card.getSuit();

        return rank.getScoreCardValue() * suit.getMultiplicationFactor();
    }



}