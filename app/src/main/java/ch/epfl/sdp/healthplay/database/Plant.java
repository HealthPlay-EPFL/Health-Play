package ch.epfl.sdp.healthplay.database;

public class Plant {
    private String imagePath, name, date;

    public String GetImagePath(){
        return imagePath;
    }

    public String GetName(){
        return name;
    }

    public String GetDate(){
        return date;
    }

    public Plant(String imagePath, String name, String date){
        this.imagePath = imagePath;
        this.name = name;
        this.date = date;
    }
}
