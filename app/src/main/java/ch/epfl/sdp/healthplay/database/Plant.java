package ch.epfl.sdp.healthplay.database;

public class Plant {
    public String imagePath, name, date;
    public int imageId;

    public Plant(String imagePath, String name, String date, int imageId){
        this.imagePath = imagePath;
        this.name = name;
        this.date = date;
        this.imageId = imageId;
    }
}
