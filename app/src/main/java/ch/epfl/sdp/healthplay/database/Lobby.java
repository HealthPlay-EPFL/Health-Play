package ch.epfl.sdp.healthplay.database;

/**
 * Defines a Lobby entity in Firebase that User can join to play a game together
 */
public class Lobby {
    private String name, password;
    private int nbrPlayers = 0;
    private int playersReady = 0;
    private int playersGone = 0;
    private String playerUid1, playerUid2, playerUid3;
    private int playerScore1 = 0, playerScore2 = 0, playerScore3 = 0;
    private boolean playerReady1 = false, playerReady2 = false, playerReady3 = false;
    private int remainingTime = 500;
    private int maxNbrPlayers;

    public Lobby(String name, String password, String hostUid, int remainingTime, int maxNbrPlayers){
        this.name = name;
        this.password = password;
        playerUid1 = hostUid;
        playerUid2 = "";
        playerUid3 = "";
        this.remainingTime = remainingTime;
        this.maxNbrPlayers = maxNbrPlayers;
        nbrPlayers = 1;
    }



    //Get lobby name
    public String getName(){
        return name;
    }

    //Get lobby password
    public String getPassword(){
        return password;
    }


    //Get how many players the lobby currently has
    public int getNbrPlayers(){
        return nbrPlayers;
    }

    //Get how many players the lobby can hold
    public int getMaxNbrPlayers(){
        return maxNbrPlayers;
    }

    //Get how many players are ready in the lobby
    public int getPlayersReady(){
        return playersReady;
    }

    //Get how many players have left the lobby
    public int getPlayersGone(){
        return playersGone;
    }

    //Get remaining time in the lobby
    public int getRemainingTime(){
        return remainingTime;
    }


    //Get player names in the lobby
    public String getPlayerUid1(){
        return playerUid1;
    }
    public String getPlayerUid2(){
        return playerUid2;
    }
    public String getPlayerUid3(){
        return playerUid3;
    }


    //Get player scores in the lobby
    public int getPlayerScore1(){
        return playerScore1;
    }
    public int getPlayerScore2(){
        return playerScore2;
    }
    public int getPlayerScore3(){ return playerScore3; }


    //Get which player is ready in the lobby
    public boolean getPlayerReady1(){
        return playerReady1;
    }
    public boolean getPlayerReady2(){ return playerReady2; }
    public boolean getPlayerReady3(){
        return playerReady3;
    }
}
