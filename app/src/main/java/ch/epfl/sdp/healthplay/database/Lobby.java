package ch.epfl.sdp.healthplay.database;

import java.util.Map;

public class Lobby {
    private String name, password;
    private int nbrPlayers = 0;
    private String playerUid_1, playerUid_2, playerUid_3;
    private int playerScore_1 = 0, playerScore_2 = 0, playerScore_3 = 0;
    private int remainingTime;
    private static final int maxNbrPlayers = 3;
    private String status;
  
    private enum GameStatus{
        WAITING, READY, PLAYING, FINISHED;
    }

    public Lobby(String name, String password, String hostUid, int remainingTime){
        this.name = name;
        this.password = password;
        playerUid_1 = hostUid;
        playerScore_1 = 0;
        this.remainingTime = remainingTime;
        nbrPlayers++;
        setStatus(GameStatus.WAITING);
    }

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
                status = "waiting";
                break;
            case READY:
                status = "ready";
                break;
            case PLAYING:
                status = "playing";
                break;
            default:
                status = "finished";
                break;
        }
    }

    public int getNbrPlayers(){
        return nbrPlayers;
    }

    public int getRemainingTime(){
        return remainingTime;
    }

    public String getPlayerUid_1(){
        return playerUid_1;
    }
    public String getPlayerUid_2(){
        return playerUid_2;
    }
    public String getPlayerUid_3(){
        return playerUid_3;
    }

    public int getPlayerScore_1(){
        return playerScore_1;
    }
    public int getPlayerScore_2(){
        return playerScore_2;
    }
    public int getPlayerScore_3(){
        return playerScore_3;
    }

    public void addPlayer(){
        nbrPlayers++;
    }
}
