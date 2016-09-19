package entity;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by xiaoduo on 7/14/15.
 */
@ParseClassName("Images")
public class Images extends ParseObject{

    private ParseFile image;

    private int position;

    private String description;

    public ParseFile getImage() {
        return getParseFile("image");
    }

    public void setImage(ParseFile image) {
        put("image", image);
    }

    public void setPosition(int position) {
        put("position", position);
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public int getPosition() {
        return getInt("position");
    }

    public String getDescription() {
        return getString("description");
    }
}
