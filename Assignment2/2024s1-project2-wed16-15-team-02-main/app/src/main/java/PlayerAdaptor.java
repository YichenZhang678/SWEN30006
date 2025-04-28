import ch.aplu.jcardgame.Card;

import java.util.List;

public class PlayerAdaptor {
    private Player player;
    public PlayerAdaptor(Player player){
        this.player = player;
    }
    public Player getPlayer(){
        return this.player;
    }
    public Card getSelectedCard(Card dealt, List<Card> discardCards,List<Card> publicCards){
        return this.player.selectCard(dealt,discardCards,publicCards);
    }
    public void calculateScore(List<Card> publicCards){
        this.player.calculateScore(publicCards);
    }

}
