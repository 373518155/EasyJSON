# EasyJSON
一个十分容易使用的JSON库（解析JSON、生成JSON），支持Java和Android
EasyJSON库的[Go语言版](https://github.com/373518155/EasyJSONGo)

## 简介
EasyJSON是一个十分容易上手的JSON库（^_^所以才叫做EasyJSON），支持Java和Android
我们知道，日常的JSON操作主要是两种：
1. 从JSON字符串中解析出各种数据(int、String、boolean等)
2. 给定各种数据，生成JSON字符串

EasyJSON库就是为了方便上述两种操作而诞生的。


## 开始使用

### 引用库文件
在模块的gradle文件中引用库文件
> compile 'cn.snailpad:EasyJSON:1.0.10'

引入好库文件后，就可以使用了，下面说明一下使用方法

我们主要用到里面的3个类
* EasyJSONObject 用于解析JSON对象
* EasyJSONArray 用于解析JSON数组
* EasyJSONBase EasyJSONObject和EasyJSONArray的基类

最好的方法是，直接上[示例代码](https://github.com/373518155/EasyJSON/blob/master/demo/Lab.java).
```java
public class Lab {
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
}
```


### 解析JSON
以下面描述一本Java教材的属性的JSON为例
```json
{
	"name": "Java in Action",
	"price": 138.27,
	"authors": ["Raoul-Gabriel Urma", "Mario Fusco", "Alan Mycroft"],
	"pages": 550,
	"available": true,
	"publisher": null,
	"chapters": [{
			"title": "Introduction",
			"pages": 22
		},
		{
			"title": "Basic Java",
			"pages": 33
		},
		{
			"title": "Advanced Java",
			"pages": 44
		}
	]
}
```

```java
// 上面的JSON字符串用Java字符串表示
String jsonStr = "{\"name\":\"Java in Action\",\"price\":138.27,\"authors\":[\"Raoul-Gabriel Urma\",\"Mario Fusco\",\"Alan Mycroft\"],\"pages\":550,\"available\":true,\"publisher\":null,\"chapters\":[{\"title\":\"Introduction\",\"pages\":22},{\"title\":\"Basic Java\",\"pages\":33},{\"title\":\"Advanced Java\",\"pages\":44}]}";

// 用JSON字符串构造EasyJSONObject对象
EasyJSONObject easyJSONObject = EasyJSONObject.parse(jsonStr);

// 使用 EasyJSONObject.getXXX(String path);方法获取数据

String name = easyJSONObject.getString("name");  // Java in Action

double price = easyJSONObject.getDouble("price"); // 138.27

String author1 = easyJSONObject.getString("authors[0]");  // Raoul-Gabriel Urma

String author2 = easyJSONObject.getString("authors[1]");  // Mario Fusco

int pages = easyJSONObject.getInt("pages"); // 550

boolean available = easyJSONObject.getBoolean("available");  // true

Object publisher = easyJSONObject.get("publisher");  // null

String title1 = easyJSONObject.getString("chapters[0].title");  // Introduction

int pages1 = easyJSONObject.getInt("chapters[0].pages");  // 22

String title2 = easyJSONObject.getString("chapters[1].title");  // Basic Java

int pages2 = easyJSONObject.getInt("chapters[1].pages"); // 33

```


EasyJSONObject.getXXX(String path);  // XXX代表具体的类型,如Int, String, Boolean等
参数path表示字段的路径

比如，对于一张图片属性的JSON数据
```json
{
	"format": "png",
	"dimensions": {
		"width": 800,
		"height": 600
	},
	"colors": ["red", "green", "blue"]
}
```

字段路径的规则如下:
1. 对于最外层的字段，路径就是字段名 // 路径path为 format时，值为 png
2. 不同层级之间的字段，用点号.间隔  // 路径path为 dimensions.width时，值为 800
3. 如果需要索引数组里面的内容，需要使用中括号，下标从0开始  // 路径path为 colors[0]时，值为 red





