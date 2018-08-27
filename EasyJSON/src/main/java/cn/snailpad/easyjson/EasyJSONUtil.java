package cn.snailpad.easyjson;

import cn.snailpad.easyjson.json.JSONObject;

/**
 * Created by zwm
 * 2018/8/27 16:10
 */
public class EasyJSONUtil {
    public static boolean isJSONString(String str) {
        if (str == null || str.length() < 1) {
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(str);
        } catch (EasyJSONException e) {
            // e.printStackTrace();
            return false;
        }
        return true;
    }
}
