package entity;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by xiaoduo on 7/5/15.
 */

@ParseClassName("_User")
public class CustomUser extends ParseUser {

    private int faculty = 0;

    public int getFaculty() {
        return getInt("faculty");
    }

    public void setFaculty(int faculty) {
        put("faculty", faculty);
    }

}
