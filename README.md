# EasyJSON
一个十分容易使用的JSON库（解析JSON、生成JSON），支持Java和Android

## 简介
EasyJSON是一个十分容易上手的JSON库（^_^所以才叫做EasyJSON），支持Java和Android
我们知道，日常的JSON操作主要是两种：
1. 从JSON字符串中解析出各种数据(int、String、boolean等)
2. 给定各种数据，生成JSON字符串

EasyJSON库就是为了方便上述两种操作而诞生的。


## 开始使用

### 引用库文件
在模块的gradle文件中引用库文件
> compile 'cn.snailpad:EasyJSON:1.0.7'

引入好库文件后，就可以使用了，下面说明一下使用方法

我们主要用到里面的3个类
* EasyJSONObject 用于解析JSON对象
* EasyJSONArray 用于解析JSON数组
* EasyJSONMap 用于生成JSON字符串


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
EasyJSONObject easyJSONObject = new EasyJSONObject(jsonStr);

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





