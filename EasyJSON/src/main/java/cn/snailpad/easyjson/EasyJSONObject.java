package cn.snailpad.easyjson;



import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.snailpad.easyjson.json.JSONArray;
import cn.snailpad.easyjson.json.JSONObject;

public class EasyJSONObject extends EasyJSONBase {
    /**
     * EasyJSONObject的内部表示
     */
    private JSONObject jsonObject;

    /**
     * 生成一个EasyJSONObject
     * @param args
     * @return
     */
    public static EasyJSONObject generate(Object... args) {
        if (args.length % 2 != 0) {  // 长度必须为2的倍数
            return null;
        }

        EasyJSONObject easyJSONObject = new EasyJSONObject();

        int counter = 0;
        String name = null;
        for (Object arg : args) {
            if (counter % 2 == 0) {  // name
                if (!(arg instanceof String)) {  // JSON对的键名必须为字符串类型
                    return null;
                }

                name = (String) arg;
            } else { // value
                try {
                    // 添加name/value对
                    easyJSONObject.set(name, arg);
                } catch (EasyJSONException e) {
                    return null;
                }
            }
            ++counter;
        }

        return easyJSONObject;
    }


    /**
     * 构建一个空的EasyJSONObject
     */
    public EasyJSONObject() {
        jsonObject = new JSONObject();
        json = jsonObject;
        jsonType = JSON_TYPE_OBJECT;
    }


    /**
     * 从一个JSONObject构建一个EasyJSONObject
     * @param jsonObject
     */
    public EasyJSONObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        json = jsonObject;
        jsonType = JSON_TYPE_OBJECT;
    }


    /**
     * 从JSON字符串构造一个EasyJSONObject
     * @param jsonString
     */
    public EasyJSONObject(String jsonString) {
        try {
            jsonObject = new JSONObject(jsonString);
            json = jsonObject;
        } catch (EasyJSONException e) {
            e.printStackTrace();
        }

        jsonType = JSON_TYPE_OBJECT;
    }


    /**
     * 从map构建一个EasyJSONObject
     */
    public EasyJSONObject(Map<String, ?> map) {
        // 先构建一个空的EasyJSONObject
        this();

        if (map == null) {
            return;
        }
        // 不能直接用 public JSONObject(Map copyFrom); 方法，因为还涉及到类型转换，
        // 需要用set();方法
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                set(entry.getKey(), entry.getValue());
            } catch (EasyJSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取EasyJSONObject内部表示的JSONObject
     * @return
     */
    public JSONObject getJSONObject() {
        return jsonObject;
    }

    /**
     * 在EasyJSONObject中设置一个name/value对
     * @param name
     * @param value
     * @return
     * @throws EasyJSONException
     */
    public EasyJSONObject set(String name, Object value) throws EasyJSONException {
        jsonObject.put(name, JSONObject.wrap(value));
        return this;
    }

    /**
     * 判断path是否存在
     * @param path
     * @return
     */
    public boolean exists(String path) {
        boolean exists = true;
        try {
            getRaw(path);
        } catch (Exception e) {
            exists = false;
        }

        return exists;
    }


    /**
     * 返回entrySet，可用于遍历JSONObject
     * @return
     */
    public Set<Map.Entry<String,Object>> entrySet() {
        return jsonObject.entrySet();
    }


    public HashMap<String, Object> getHashMap() {
        return jsonObject.getHashMap();
    }
}
