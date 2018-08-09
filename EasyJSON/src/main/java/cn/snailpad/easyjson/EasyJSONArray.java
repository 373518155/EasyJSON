package cn.snailpad.easyjson;


import java.util.Iterator;

import cn.snailpad.easyjson.json.JSONArray;
import cn.snailpad.easyjson.json.JSONException;

/**
 * 参考  https://developer.android.google.cn/reference/org/json/JSONArray.html
 */
public class EasyJSONArray implements Iterable<EasyJSONObject> {
    private JSONArray jsonArray;

    public EasyJSONArray(String json) {
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public EasyJSONArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public Iterator<EasyJSONObject> iterator() {
        return new Iterator<EasyJSONObject>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < jsonArray.length();
            }

            @Override
            public EasyJSONObject next() {
                EasyJSONObject easyJSONObject = null;
                try {
                    easyJSONObject = new EasyJSONObject(jsonArray.getJSONObject(index++));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return easyJSONObject;
            }
        };
    }

    /**
     * 返回数组中的元素个数
     * @return
     */
    public int length() {
        return jsonArray.length();
    }
}
