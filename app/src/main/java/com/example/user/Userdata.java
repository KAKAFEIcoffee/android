/**************************************************/
/**********生成Userdata类，定义用户设置信息**********/
/**************************************************/
package com.example.user;

public class Userdata {
    int _id;
    String name;
    int sound_and;
    int setting;
    String picture;
    String sound_wav;

    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }

    public String getname() {
        return name;
    }
    public void setname(String name) {
        this.name = name;
    }

    public int getsound_and() {
        return sound_and;
    }
    public void setsound_and(int sound_and) {
        this.sound_and = sound_and;
    }

    public int getsetting() {
        return setting;
    }
    public void setsetting(int setting) {
        this.setting = setting;
    }

    public String getpicture() {
        return picture;
    }
    public void setpicture(String picture) {
        this.picture = picture;
    }

    public String getsound_wav() {
        return sound_wav;
    }
    public void setsound_wav(String sound_wav) {
        this.sound_wav = sound_wav;
    }

    public void set_all(int _id,String name,int sound_and,int setting,String picture,String sound_wav) {
        this._id = _id;
        this.name = name;
        this.sound_and = sound_and;
        this.setting = setting;
        this.picture = picture;
        this.sound_wav = sound_wav;
    }
}
