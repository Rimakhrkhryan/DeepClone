package example;

import org.example.DeepCloneHelper;

import org.example.Man;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DeepCloneTest {

	@Test
	void testDeepClone() {
		var books =new ArrayList<String>();
		books.add("book1");
		books.add("book2");
		var man = new Man("Name", 25, books);
		var manCopy = DeepCloneHelper.deepClone(man);
		assertNotEquals(manCopy, man);
		assertEquals(manCopy.getAge(), man.getAge());
		assertEquals(manCopy.getName(), man.getName());
		assertEquals(manCopy.getFavoriteBooks(), man.getFavoriteBooks());

	}


}