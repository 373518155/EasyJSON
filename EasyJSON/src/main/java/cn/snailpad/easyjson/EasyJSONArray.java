package cn.snailpad.easyjson;


import cn.snailpad.easyjson.json.JSONArray;
import cn.snailpad.easyjson.json.JSONObject;

import java.util.Iterator;

public class EasyJSONArray extends EasyJSONBase implements Iterable<Object> {
    /**
     * EasyJSONArray的内部表示
     */
    private JSONArray jsonArray;

    /**
     * 生成一个EasyJSONArray
     * @param args
     * @return
     */
    public static EasyJSONArray generate(Object... args) {
        EasyJSONArray easyJSONArray = new EasyJSONArray();
        for (Object arg : args) {
            easyJSONArray.append(arg);
        }

        return easyJSONArray;
    }

    /**
     * 构建一个空的EasyJSONArray
     */
    public EasyJSONArray() {
        jsonArray = new JSONArray();
        json = jsonArray;
        jsonType = JSON_TYPE_ARRAY;
    }

    /**
     * 從iterable構建一個EasyJSONArray
     * @param iterable
     * @return
     */
    public static EasyJSONArray from(Iterable iterable) {
        EasyJSONArray easyJSONArray = EasyJSONArray.generate();
        for (Object object :iterable) {
            easyJSONArray.append(object);
        }

        return easyJSONArray;
    }

    /**
     * 从一个JSONArray构建一个EasyJSONArray
     * @param jsonArray
     */
    public EasyJSONArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        json = jsonArray;
        jsonType = JSON_TYPE_ARRAY;
    }

    /**
     * 从JSON字符串构造一个EasyJSONArray
     * @param jsonString
     */
    public EasyJSONArray(String jsonString) {
        try {
            jsonArray = new JSONArray(jsonString);
            json = jsonArray;
        } catch (EasyJSONException e) {
            e.printStackTrace();
        }

        jsonType = JSON_TYPE_ARRAY;
    }


    /**
     * 在EasyJSONArray尾部插入一个值
     * @param value
     * @return
     */
    public EasyJSONArray append(Object value) {
        if (value == null) {
            // SLog.info("value is NULL");
            value = JSONObject.NULL;
        }

        // 类型转换
        if (value instanceof EasyJSONObject) {
            value = ((EasyJSONObject) value).getJSONObject();
        } else if (value instanceof EasyJSONArray) {
            value = ((EasyJSONArray) value).getJSONArray();
        }
        jsonArray.put(value);
        return this;
    }

    /**
     * 获取EasyJSONArray内部表示的JSONArray
     * @return
     */
    public JSONArray getJSONArray() {
        return jsonArray;
    }


    /**
     * 在EasyJSONArray中获取指定索引的值
     * @param index
     * @return
     */
    public Object get(int index) throws EasyJSONException {
        if (jsonArray == null) {
            return null;
        }

        Object value = jsonArray.get(index);

        if (value instanceof JSONObject) {
            value = new EasyJSONObject((JSONObject) value);
        } else if (value instanceof JSONArray) {
            value = new EasyJSONArray((JSONArray) value);
        }
        // SLog.info("valueClass[%s]", value.getClass());

        return value;
    }

    /**
     * 在EasyJSONArray中设置指定索引的值
     * @param index
     * @param value
     * @return
     * @throws EasyJSONException
     */
    public EasyJSONArray set(int index, Object value) throws EasyJSONException {
        if (jsonArray == null || jsonArray.length() <= index) {
            String errMsg = String.format("Array Index Out Of Bounds, length: %d, index: %d", jsonArray.length(), index);
            throw new EasyJSONException(errMsg);
        }

        jsonArray.put(index, value);

        return this;
    }


    public boolean getBoolean(int index) throws EasyJSONException {
        return (boolean) get(index);
    }

    public int getInt(int index) throws EasyJSONException {
        return (int) get(index);
    }

    public double getDouble(int index) throws EasyJSONException {
        return (double) get(index);
    }


    public String getString(int index) throws EasyJSONException {
        return (String) get(index);
    }

    public EasyJSONArray getArray(int index) throws EasyJSONException {
        return (EasyJSONArray) get(index);
    }


    public EasyJSONObject getObject(int index) throws EasyJSONException {
        return (EasyJSONObject) get(index);
    }


    /**
     * 获取当前元素个数
     * @return
     */
    public int length() {
        return jsonArray.length();
    }


    /**
     * 迭代器
     * @return
     */
    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < jsonArray.length();
            }

            @Override
            public Object next() {
                Object obj = null;
                try {
                    obj = jsonArray.get(index++);
                    if (obj instanceof JSONObject) {
                        return new EasyJSONObject((JSONObject) obj);
                    } else if (obj instanceof JSONArray) {
                        return new EasyJSONArray((JSONArray) obj);
                    }
                } catch (EasyJSONException e) {
                    e.printStackTrace();
                }
                return obj;
            }
        };
    }
}
