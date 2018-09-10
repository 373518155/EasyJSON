package cn.snailpad.easyjson;

import java.util.ArrayList;
import java.util.HashMap;

import cn.snailpad.easyjson.json.JSONArray;
import cn.snailpad.easyjson.json.JSONObject;

/**
 * Created by zwm on 2017/10/15.
 */



/*
功能：组建json数据
用法示例:


没有格式化的
System.out.println(new EasyJSONMap(
                "AA", 11,
                "BB", 22,
                "CC", new EasyJSONMap[] {new EasyJSONMap("a", 1), new EasyJSONMap("b", 2, "c", 3)},
                "DD", new EasyJSONMap("x", true, "y", false)));
会输出如下：
{"AA":11,"BB":22,"CC":[{"a":1},{"b":2,"c":3}],"DD":{"x":true,"y":false}}



格式化的
System.out.println(new EasyJSONMap(
                "AA", 11,
                "BB", 22,
                "CC", new EasyJSONMap[] {new EasyJSONMap("a", 1), new EasyJSONMap("b", 2, "c", 3)},
                "DD", new EasyJSONMap("x", true, "y", false)).toString(2));    // toString的传入参数2表示以2个空格缩进来格式化json字符串
会输出如下：
{
  "AA": 11,
  "BB": 22,
  "CC": [
    {
      "a": 1
    },
    {
      "b": 2,
      "c": 3
    }
  ],
  "DD": {
    "x": true,
    "y": false
  }
}


EasyJSONList data2 = new EasyJSONList();
for (int i = 0; i < 5; ++i) {
    data2.add(i);
}
EasyJSONMap response = new EasyJSONMap(
        "c", "Sqlite",
        "a", "response",
        "data", new EasyJSONList("test", 1, true),
        "data2", data2
);

// response.toString();
// 返回 {"a":"response","c":"Sqlite","data":["test",1,true],"data2":[0,1,2,3,4]}


EasyJSONList easyJSONList = new EasyJSONList(response, 234);
// easyJSONList.toString();
// 返回 [{"a":"response","c":"Sqlite","data":["test",1,true],"data2":[0,1,2,3,4]}, 234]


*/

public class EasyJSONMap extends HashMap<String, Object> {
    public EasyJSONMap(Object... args) {
        super();

        if (args.length % 2 != 0) {  // 长度必须为2的倍数
            throw new IllegalArgumentException("Odd number of arguments");
        }

        int count = 0;
        String key = "";
        for (Object arg : args) {
            if (count % 2 == 0) {
                key = (String)arg;
            }
            else {
                if (arg instanceof EasyJSONList) {
                    arg = new JSONArray(((EasyJSONList) arg).getList());
                }
                put(key, arg);
            }
            ++count;
        }
    }

    @Override
    public String toString() {
        return (new JSONObject(this)).toString();
    }

    /**
     * 以格式化的形式输出json字符串
     * @param indentSpaces 缩进空格数
     * @return
     * @throws EasyJSONException
     */
    public String toString(int indentSpaces) throws EasyJSONException {
        return (new JSONObject(this)).toString(indentSpaces);
    }
}
