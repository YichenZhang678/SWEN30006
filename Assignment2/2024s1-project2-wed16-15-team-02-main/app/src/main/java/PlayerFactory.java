import java.util.Properties;

public class PlayerFactory {

    private static PlayerFactory instance = null;
    private PlayerFactory(){}
    public static PlayerFactory getInstance(){
        if(instance == null){
            instance  = new PlayerFactory();
        }
        return instance;
    }

    public Player[] getPlayers(Properties properties){
        int nbPlayers = 4;
        Player[] players = new Player[nbPlayers];
        String[] playerStrings = new String[nbPlayers];
        playerStrings[0] = properties.getProperty("players.0", "random");
        playerStrings[1] = properties.getProperty("players.1", "random");
        playerStrings[2] = properties.getProperty("players.2", "random");
        playerStrings[3] = properties.getProperty("players.3", "random");
        int i;
        for(i = 0; i < nbPlayers; i++){
            if(playerStrings[i].equals("human")){
                players[i] = new HumanPlayer();
            }else if(playerStrings[i].equals("basic")){
                players[i] = new BasicPlayer();
            }else if(playerStrings[i].equals("clever")){
                players[i] = new CleverPlayer();
            }else{
                players[i] = new RandomPlayer();
            }
        }

        return players;
    }

    public PlayerAdaptor[] getPlayerAdaptors(Properties properties){
        int nbPlayers = 4;
        PlayerAdaptor[] players = new PlayerAdaptor[nbPlayers];
        String[] playerStrings = new String[nbPlayers];
        playerStrings[0] = properties.getProperty("players.0", "random");
        playerStrings[1] = properties.getProperty("players.1", "random");
        playerStrings[2] = properties.getProperty("players.2", "random");
        playerStrings[3] = properties.getProperty("players.3", "random");
        int i;
        for(i = 0; i < nbPlayers; i++){
            if(playerStrings[i].equals("human")){

                players[i] = new PlayerAdaptor(new HumanPlayer());
            }else if(playerStrings[i].equals("basic")){
                players[i] = new PlayerAdaptor(new BasicPlayer());
            }else if(playerStrings[i].equals("clever")){
                players[i] = new PlayerAdaptor(new CleverPlayer());
            }else{
                players[i] = new PlayerAdaptor(new RandomPlayer());

            }
        }

        return players;
    }

}
