import cn.snailpad.easyjson.EasyJSONArray;
import cn.snailpad.easyjson.EasyJSONBase;
import cn.snailpad.easyjson.EasyJSONObject;
import cn.snailpad.easyjson.FieldName;

import static cn.snailpad.easyjson.EasyJSONBase.JSON_TYPE_ARRAY;
import static cn.snailpad.easyjson.EasyJSONBase.JSON_TYPE_OBJECT;

public class Lab {
    /////////////////////////////////////////////////////
    //  Java Bean类
    //  用于展示EasyJSONBase.jsonEncode()和EasyJSONBase.jsonDecode()方法的使用
    /////////////////////////////////////////////////////
    public static class Data {
        // 访问属性必须指定为public
        public int id;
        public String name;

        // 如果使用EasyJSONBase.jsonDecode()方法，必须要有默认构造函数，或不能定义任何构造函数
        public Data() {
        }

        public Data(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    /////////////////////////////////////////////////////
    //  Java Bean类
    //  用于展示重命名字段的使用
    /////////////////////////////////////////////////////
    public static class Data2 {
        // 访问属性必须指定为public
        public int id;
        // jsonEncode时，将字段name重命名为MyName
        @FieldName("MyName")
        public String name;

        public Data2(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static void main(String[] args) throws Exception {

        /////////////////////////////////////////////////////
        //  生成JSON字符串(对象)示例
        /////////////////////////////////////////////////////
        EasyJSONObject object1 = EasyJSONObject.generate("name", "Hello", "female", false);
        System.out.println(object1.toString());
        // 输出: {"name":"Hello","female":false}

        // 添加字段age
        object1.put("age", 18);
        System.out.println(object1.toString());
        // 输出: {"name":"Hello","female":false,"age":18}

        // 修改字段age(如果字段age不存在，则添加;如果已经存在，则修改)
        object1.put("age", 19);
        System.out.println(object1.toString());
        // 输出: {"name":"Hello","female":false,"age":19}

        // 添加数组
        object1.put("language", EasyJSONArray.generate("Chinese", "English", "French"));
        System.out.println(object1.toString());
        // 输出: {"name":"Hello","female":false,"age":19,"language":["Chinese","English","French"]}

        // 上述JSON字符串也可以用一条语句直接生成
        EasyJSONObject object2 = EasyJSONObject.generate(
                "name", "Hello",
                "female", false,
                "age", 19,
                "language", EasyJSONArray.generate("Chinese", "English", "French"));
        System.out.println(object2.toString());
        // 输出: {"name":"Hello","female":false,"age":19,"language":["Chinese","English","French"]}



        /////////////////////////////////////////////////////
        //  生成JSON字符串(数组)
        /////////////////////////////////////////////////////
        EasyJSONArray array1 = EasyJSONArray.generate("Hello", 2, true, EasyJSONObject.generate("age", 18));
        System.out.println(array1.toString());
        // 输出: ["Hello",2,true,{"age":18}]
        // 共有4个元素，其中第1个为字符串，第2个为数字，第3个为布尔值，第4个为对象

        // 在尾部添加一个元素
        array1.put("World");
        System.out.println(array1.toString());
        // 输出: ["Hello",2,true,{"age":18},"World"]

        // 在尾部添加一个数组
        array1.put(EasyJSONArray.generate("new", "old"));
        System.out.println(array1.toString());
        // 输出: ["Hello",2,true,{"age":18},"World",["new","old"]]

        // 在尾部添加一个对象
        array1.put(EasyJSONObject.generate("female", false));
        System.out.println(array1.toString());
        // 输出: ["Hello",2,true,{"age":18},"World",["new","old"],{"female":false}]


        /////////////////////////////////////////////////////
        //  从JSON字符串解析回EasyJSONObject或EasyJSONArray
        //  解析JSON对象字符串 {"name":"Hello","female":false,"age":18}
        /////////////////////////////////////////////////////
        String jsonStr1 = "{\"name\":\"Hello\",\"female\":false,\"age\":18}";
        EasyJSONObject object3 = (EasyJSONObject) EasyJSONObject.parse(jsonStr1);
        String name = object3.getString("name");  // 值为 "Hello"
        int age = object3.getInt("age");  // 值为 18



        /////////////////////////////////////////////////////
        //  从JSON字符串解析回EasyJSONObject或EasyJSONArray
        //  解析JSON数组字符串 ["Hello",2,true,{"age":18},"World",["new","old"],{"female":false}]
        /////////////////////////////////////////////////////
        String jsonStr2 = "[\"Hello\",2,true,{\"age\":18},\"World\",[\"new\",\"old\"],{\"female\":false}]";
        EasyJSONArray array2 = (EasyJSONArray) EasyJSONArray.parse(jsonStr2);
        String str = array2.getString(0);
        System.out.println(str); // 输出: Hello
        int val = array2.getInt(1);
        System.out.println(val); // 输出: 2
        boolean bool = array2.getBoolean(2);
        System.out.println(bool); // 输出: true
        EasyJSONObject object4 = array2.getObject(3);
        System.out.println(object4); // 输出: {"age":18}
        EasyJSONArray array3 = array2.getArray(5);
        System.out.println(array3); // 输出: ["new","old"]


        /////////////////////////////////////////////////////
        //  编码Java Bean
        /////////////////////////////////////////////////////
        Data data1 = new Data(3, "Hello");
        String data1JSON = EasyJSONBase.jsonEncode(data1);
        System.out.println(data1JSON); // 输出: {"id":3,"name":"Hello"}


        /////////////////////////////////////////////////////
        //  重命名字段
        /////////////////////////////////////////////////////
        Data2 data2 = new Data2(4, "World");
        String data2JSON = EasyJSONBase.jsonEncode(data2);
        System.out.println(data2JSON); // 输出: {"id":4,"MyName":"World"}

        String newDataJSON = "{\"id\":3,\"name\":\"Hello\"}";
        Data newData = (Data) EasyJSONBase.jsonDecode(Data.class, newDataJSON);
        System.out.println(newData.id);  // 输出: 3
        System.out.println(newData.name); // 输出: Hello


        /////////////////////////////////////////////////////
        //  检测JSON字符串类型: 是否为对象或数组
        /////////////////////////////////////////////////////
        String jsonString1 = "{\"id\":3,\"name\":\"Hello\"}";
        System.out.println(EasyJSONBase.guessJSONType(jsonString1) == JSON_TYPE_OBJECT); // 输出: true
        System.out.println(EasyJSONBase.guessJSONType(jsonString1) == JSON_TYPE_ARRAY); // 输出: false
        String jsonString2 = "[1,2,3]";
        System.out.println(EasyJSONBase.guessJSONType(jsonString2) == JSON_TYPE_OBJECT); // 输出: false
        System.out.println(EasyJSONBase.guessJSONType(jsonString2) == JSON_TYPE_ARRAY); // 输出: true
    }
}
