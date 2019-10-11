package com.mrl.i_note.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by apple on 2018/6/7.
 */

public class SerializableMap implements Serializable {

    private Map<String,Object> map;
    public Map<String,Object> getMap() {
        return map;
    }
    public void setMap(Map<String,Object> map) {
        this.map=map;
    }
}
