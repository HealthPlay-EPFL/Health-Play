package ch.epfl.sdp.healthplay.database;

/**
 * Defines a Lobby entity in Firebase that User can join to play a game together
 */
public class Lobby {
    private String name, password;
    private int nbrPlayers = 0;
    private String playerUid1, playerUid2, playerUid3;
    private int playerScore1 = 0, playerScore2 = 0, playerScore3 = 0;
    private int remainingTime;
    private static final int maxNbrPlayers = 3;
    private String status;

    private static final String WAITING_STRING = "waiting", READY_STRING = "ready", PLAYING_STRING = "playing", FINISHED_STRING = "finished";

    //Current status of the game in the lobby
    private enum GameStatus{
        WAITING, READY, PLAYING, FINISHED;
    }

    public Lobby(String name, String password, String hostUid, int remainingTime){
        this.name = name;
        this.password = password;
        playerUid1 = hostUid;
        playerScore1 = 0;
        this.remainingTime = remainingTime;
        nbrPlayers++;
        setStatus(GameStatus.WAITING);
    }


    //Getter methods for all fields appearing in Firebase

    public String getName(){
        return name;
    }
    public String getPassword(){
        return password;
    }
    public String getStatus(){
        return status;
    }

    private void setStatus(GameStatus s){
        switch (s){
            case WAITING:
                status = WAITING_STRING;
                break;
            case READY:
                status = READY_STRING;
                break;
            case PLAYING:
                status = PLAYING_STRING;
                break;
            default:
                status = FINISHED_STRING;
                break;
        }
    }

    public int getNbrPlayers(){
        return nbrPlayers;
    }

    public int getRemainingTime(){
        return remainingTime;
    }

    public String getPlayerUid1(){
        return playerUid1;
    }
    public String getPlayerUid2(){
        return playerUid2;
    }
    public String getPlayerUid3(){
        return playerUid3;
    }

    public int getPlayerScore1(){
        return playerScore1;
    }
    public int getPlayerScore2(){
        return playerScore2;
    }
    public int getPlayerScore3(){
        return playerScore3;
    }

    public void addPlayer(){
        nbrPlayers++;
    }
}
