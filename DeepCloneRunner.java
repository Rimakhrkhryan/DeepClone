package org.example;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DeepCloneRunner {
	public static void main(String[] args) {
		var books =new ArrayList<String>();
		books.add("book1");
		books.add("book2");
		var man = new Man("Name", 25, books);
		var man = new Man("Name",25,books);
		var manCopy = DeepCloneHelper.deepClone(man);
		System.out.println(manCopy == man);
	}



}
