package cn.snailpad.easyjson;


import cn.snailpad.easyjson.json.JSONArray;
import cn.snailpad.easyjson.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/*
参考  http://www.json.org/
JSON的两种操作:
1.parse 2.generate


JSON的两种数据结构:
1.对象：无序的，由name/value pairs构成
2.数组：有序的，由各种value构成

value的类型:
1.string
2.number
3.object
4.array
5.true
6.false
7.null

 */




public class EasyJSONBase {
    public static final int JSON_TYPE_INVALID = 0;  // 无效的JSON类型
    public static final int JSON_TYPE_OBJECT = 1;
    public static final int JSON_TYPE_ARRAY = 2;

    protected Object json;  // 内部表示的JSON，可能是JSONObject或JSONArray

    protected int jsonType = JSON_TYPE_INVALID;

    public int getJsonType() {
        return jsonType;
    }


    /**
     * 判断给定的字符串是否为JSON格式的字符串(支持JSON对象和数组)
     * @param jsonString
     * @return
     */
    public static boolean isJSONString(String jsonString) {
        if (jsonString == null || jsonString.length() < 1) {
            return false;
        }
        try {
            int jsonType = guessJSONType(jsonString);  // 需要先确定类型
            if (jsonType == JSON_TYPE_OBJECT) {
                JSONObject jsonObject = new JSONObject(jsonString);
            } else if (jsonType == JSON_TYPE_ARRAY) {
                JSONArray jsonArray = new JSONArray(jsonString);
            } else {
                return false;
            }

        } catch (EasyJSONException e) {
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    public static int guessJSONType(String jsonString) {
        if (jsonString == null) {
            return JSON_TYPE_INVALID;
        }

        int len = jsonString.length();
        for (int i = 0; i < len; ++i) {
            char ch = jsonString.charAt(i);
            if (ch == '{') {
                return JSON_TYPE_OBJECT;
            }
            if (ch == '[') {
                return JSON_TYPE_ARRAY;
            }
        }

        return JSON_TYPE_INVALID;
    }


    public static EasyJSONBase parse(String jsonString) {
        int jsonType = guessJSONType(jsonString);
        // SLog.info("jsonType[%s]", jsonType);
        if (jsonType == JSON_TYPE_OBJECT) {
            return new EasyJSONObject(jsonString);
        } else if (jsonType == JSON_TYPE_ARRAY) {
            return new EasyJSONArray(jsonString);
        }

        return null;
    }


    /**
     * 将path分割成各个name，例如
     * name1.name2[0][2].name3[1]  分拆成  name1, name2, [0], [2], name[3], [1]
     * [2].name1[1]
     * @param path
     * @return
     */
    private static List<String> splitPath(String path) {
        path = path.trim();
        String[] snippets = path.split("\\."); // 用点号来分隔不同的层级
        List<String> nameList = new ArrayList<>();


        for (String snippet : snippets) {
            // SLog.info("snippet[%s]", snippet);
            int len = snippet.length();
            int i = 0;
            int j = 0;
            String name;
            char beginChar = snippet.charAt(0);
            while (true) {
                char ch = snippet.charAt(j);

                if (beginChar == '[') {  // 是数组索引
                    if (ch == ']') {
                        ++j;  // 跳到中括号的下一个
                        name = snippet.substring(i, j);
                        nameList.add(name);
                        i = j;
                        if (i >= len) {
                            break;
                        }
                        beginChar = snippet.charAt(i);
                    }
                } else {  // 是对象字段
                    if (j + 1 == len) {
                        name = snippet.substring(i, j + 1);
                        nameList.add(name);
                        break;
                    }
                    if (ch == '[') {
                        name = snippet.substring(i, j);
                        nameList.add(name);
                        i = j;

                        if (i >= len) {
                            break;
                        }
                        beginChar = snippet.charAt(i);
                    }
                }
                ++j;
            }
        }

        return nameList;
    }


    public Object get(String path) throws EasyJSONException {
        List<String> nameList = splitPath(path);

        Object value = json;  // 赋初始值，从本层开始
        for (String name : nameList) {
            name = name.trim();
            if (name.startsWith("[")) {  // 以中括号开始，表明是个Array
                // 去除开始和结束的中括号
                String indexStr = name.substring(1, name.length() - 1);
                indexStr = indexStr.trim();
                // SLog.info("indexStr[%s]", indexStr);

                int index = Integer.parseInt(indexStr);  // 得出Array的索引

                value = ((JSONArray) value).get(index);
            } else {  // 否则，表明是个Object
                value = ((JSONObject)value).get(name);
            }
        }

        String valueType = "UNKNOWN";
        if (value.equals(JSONObject.NULL)) {
            valueType = "NULL";
        } else if (value instanceof Boolean) {
            valueType = "BOOLEAN";
        } else if (value instanceof String) {
            valueType = "STRING";
        } else if (value instanceof Double || value instanceof Float) {
            valueType = "DOUBLE";
        } else if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            valueType = "INTEGER";
        } else if (value instanceof JSONObject) {
            valueType = "OBJECT";
            value = new EasyJSONObject((JSONObject) value);
        } else if (value instanceof JSONArray) {
            valueType = "ARRAY";
            value = new EasyJSONArray((JSONArray) value);
        }
        // SLog.info("valueType[%s]", valueType);

        return value;
    }

    public boolean getBoolean(String path) throws EasyJSONException {
        return (boolean) get(path);
    }

    public int getInt(String path) throws EasyJSONException {
        return (int) get(path);
    }

    public double getDouble(String path) throws EasyJSONException {
        return (double) get(path);
    }


    public String getString(String path) throws EasyJSONException {
        return (String) get(path);
    }

    public EasyJSONArray getArray(String path) throws EasyJSONException {
        return (EasyJSONArray) get(path);
    }


    public EasyJSONObject getObject(String path) throws EasyJSONException {
        return (EasyJSONObject) get(path);
    }



    @Override
    public String toString() {
        if (json == null) {
            return "null";
        }

        if (jsonType == JSON_TYPE_OBJECT) {
            JSONObject jsonObject = (JSONObject) json;
            return jsonObject.toString();
        } else if (jsonType == JSON_TYPE_ARRAY) {
            JSONArray jsonArray = (JSONArray) json;
            return jsonArray.toString();
        }

        return "null";
    }

    /**
     * 以格式化的形式输出json字符串
     * @param indentSpaces 缩进空格数
     * @return
     */
    public String toString(int indentSpaces) {
        if (json == null) {
            return "null";
        }

        String jsonString = "null";
        try {
            if (jsonType == JSON_TYPE_OBJECT) {
                JSONObject jsonObject = (JSONObject) json;
                jsonString = jsonObject.toString(indentSpaces);
            } else if (jsonType == JSON_TYPE_ARRAY) {
                JSONArray jsonArray = (JSONArray) json;
                jsonString = jsonArray.toString(indentSpaces);
            }
        } catch (EasyJSONException e) {

        }

        return jsonString;
    }
}



/*
EasyJSONObject和EasyJOSNArray都支持path路径的访问
关于路径path的说明，以下面的JSON为例(通过测试)

{
	"code": 200,
	"message": "success",
	"valid": true,
	"pi": 3.14,
	"data": {
		"phone": 10086,
		"addr": "China",
		"score": {
			"math": 95,
			"music": 86
		},
		"bool_list": [false, true, false]
	},
	"int_list": [1, 2, 3, 4],
	"string_list": ["Tom", "Peter", "Jack"],
	"hybrid_list": [1, {
		"http_not_found": 404
	}, "toyota"],
	"object_list": [{
		"lang": "java",
		"type": "static"
	}, {
		"lang": "javascript",
		"type": "dynamic"
	}],
	"multi_dim": [
		[0, 1],
		[2, 3]
	]
}


------------------------------------------------------------------
路径                 |值
------------------------------------------------------------------
code                |int(200)
------------------------------------------------------------------
data                |EasyJSONObject对象
------------------------------------------------------------------
data.score.math     |int(95)
------------------------------------------------------------------
int_list            |EasyJSONArray对象
------------------------------------------------------------------
int_list[2]         |int(3)
------------------------------------------------------------------
object_list[0]      |EasyJSONObject对象
------------------------------------------------------------------
object_list[0].lang |string("java")
------------------------------------------------------------------
multi_dim[0][1]     |int(3)
------------------------------------------------------------------



对于EasyJSONArray，还支持通过index访问其中的元素(通过测试)
[
	200,
	"success",
	true,
	3.14, [1, {
		"http_not_found": 404
	}, "toyota"],
	{
		"math": 95,
		"music": 86
	},
	[{
		"lang": "java",
		"type": "static"
	}, {
		"lang": "javascript",
		"type": "dynamic"
	}],
	[
		[0, 1],
		[2, 3]
	]
]


------------------------------------------------------------------
索引                 |值
------------------------------------------------------------------
0                   |int(200)
------------------------------------------------------------------
1                   |string("success")
------------------------------------------------------------------
4                   |EasyJSONArray对象
------------------------------------------------------------------
5                   |EasyJSONObject对象
------------------------------------------------------------------



 */