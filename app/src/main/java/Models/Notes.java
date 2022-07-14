package Models;

public class Notes {
    //Variables.
    String id;
    String contents_Notes;
    String templeTime;
    int imagePath;
    //default constructor.
    public Notes() {
    }
    // constructor
    public Notes(String id, String contents_Notes, String templeTime, int imagePath) {
        this.id = id;
        this.contents_Notes = contents_Notes;
        this.templeTime = templeTime;
        this.imagePath = imagePath;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public String getContents_Notes() {
        return contents_Notes;
    }

    public void setContents_Notes(String contents_Notes) {
        this.contents_Notes = contents_Notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTempleTime() {
        return templeTime;
    }

    public void setTempleTime(String templeTime) {
        this.templeTime = templeTime;
    }
}
