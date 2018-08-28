package cn.snailpad.easyjson;

import java.util.Map;

import cn.snailpad.easyjson.json.JSONArray;
import cn.snailpad.easyjson.json.JSONObject;
import cn.snailpad.easyjson.json.JSONTokener;

public class EasyJSONObject {
    private JSONObject jsonObject;

    public EasyJSONObject(Map copyFrom) {
        jsonObject = new JSONObject(copyFrom);
    }

    public EasyJSONObject(JSONTokener readFrom) {
        try {
            jsonObject = new JSONObject(readFrom);
        } catch (EasyJSONException e) {
            e.printStackTrace();
        }
    }

    public EasyJSONObject(String json) {
        try {
            jsonObject = new JSONObject(json);
        } catch (EasyJSONException e) {
            e.printStackTrace();
        }
    }

    public EasyJSONObject(JSONObject copyFrom, String[] names) {
        try {
            jsonObject = new JSONObject(copyFrom, names);
        } catch (EasyJSONException e) {
            e.printStackTrace();
        }
    }

    public EasyJSONObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     *
     {
     "code": 200,
     "data": [
     {
     "a": 100,
     "b": 200
     },
     {
     "a": 100,
     "c": 300
     }
     ]
     }
     *
     *
     * 根据json路径，获取相应的值
     * @param path json路径
     *             例如对上面的json数据
     *             当path = "code"时， 返回200
     *             当path = "data"时， 返回data所表示的JSONArray
     *             当path = "data[0]时， 返回 data数组第1个元素的对象（下标从0开始）
     *             当path = "data[1].c时，返回300
     * @return
     */
    public Object get(String path) throws EasyJSONException {
        String[] names = path.split("\\."); // 用点号来分隔不同的层级
        Object obj = null;
        for (String name : names) {
            if (obj == null) {
                obj = jsonObject;
            }

            if (name.endsWith("]")) {  // 以中括号结尾，表明是个数组
                int bracketIndex = name.indexOf("[");
                String arrayName = name.substring(0, bracketIndex);
                JSONArray jsonArray = ((JSONObject)obj).getJSONArray(arrayName);

                String indexStr = name.substring(bracketIndex + 1, name.length() - 1);
                int index = Integer.parseInt(indexStr);
                obj = jsonArray.get(index);
            }
            else {
                obj = ((JSONObject)obj).get(name);
            }
        }
        return obj;
    }

    public JSONArray getJSONArray(String path) throws EasyJSONException {
        return (JSONArray)get(path);
    }

    public EasyJSONArray getEasyJsonArray(String path) throws EasyJSONException {
        return new EasyJSONArray((JSONArray)get(path));
    }

    public JSONObject getJSONObject(String path) throws EasyJSONException {
        return (JSONObject)get(path);
    }

    public boolean getBoolean(String path) throws EasyJSONException {
        return (boolean)get(path);
    }

    public int getInt(String path) throws EasyJSONException {
        return (int)get(path);
    }

    public double getDouble(String path) throws EasyJSONException {
        return (double)get(path);
    }

    public String getString(String path) throws EasyJSONException {
        return get(path).toString();
    }

    /**
     * 判断路径或字段是否存在
     * @param path
     * @return
     */
    public boolean exists(String path) {
        boolean exists = true;
        try {
            get(path);
        } catch (EasyJSONException e) {
            e.printStackTrace();
            exists = false;
        }
        return exists;
    }
}

