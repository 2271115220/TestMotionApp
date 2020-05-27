package rjw.net.testmotion.util;



import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

/**
 * @author zhd: 好好写
 * @date 2020/4/13 13:33
 * @desc
 */
public class GsonUtils {

    public static <T> T fromJson(String json, Type t) {
        return new Gson().fromJson(json, t);
    }
    public static <T> T fromJson(JsonElement json, Type t) {
        return fromJson(json.toString(), t);
    }
    public static String getJson(Object object) {
        return new Gson().toJson(object);
    }

}
