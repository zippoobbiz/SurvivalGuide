package entity;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by xiaoduo on 7/5/15.
 */

@ParseClassName("Faculty")
public class Faculty extends ParseObject {

    private String name = null;

    public void setName(String name) {
        put("name","name");
    }

    public void setAcronym(String acronym) {
        put("acronym","acronym");
    }

    public String getAcronym() {

        return getString("acronym");
    }

    public String getName() {
        return getString("name");
    }

    private String acronym = null;


}
