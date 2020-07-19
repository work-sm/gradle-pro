package com.sam.demo.nerver.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.junit.Assert;

/**
 * 在fastjson-1.2.12版本中，JSONField支持一个新的配置项jsonDirect，它的用途是：
 * 当你有一个字段是字符串类型，里面是json格式数据，你希望直接输入，而不是经过转义之后再输出。
 * 
 * @since https://github.com/alibaba/fastjson/wiki/JSONField_jsonDirect_cn
 * 
 * @author lry
 */
public class Model {
	
	public int id;
	@JSONField(jsonDirect = true)
	public String value;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public static void main(String[] args) {
		Model model = new Model();
		model.id = 1001;
		model.value = "{\"id\":1001,\"value\":{}}";

		String json = JSON.toJSONString(model);
		System.out.println(json);
		Assert.assertEquals("{\"id\":1001,\"value\":{\"id\":1001,\"value\":{}}}", json);
	}
}
