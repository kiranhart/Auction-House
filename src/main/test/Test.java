import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 2:51 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class Test {

	public static void main(String[] args) {

		final List<TestObject> list = new ArrayList<>();

		list.add(new TestObject(1, "Kiran"));
		list.add(new TestObject(2, "Carl"));
		list.add(new TestObject(3, "Orlando"));

		System.out.println(list.size());
	}

	@AllArgsConstructor
	@Getter
	static class TestObject {

		private final int id;
		private final String name;

	}

	public static long getSecondsFromString(String time) {
		time = time.toLowerCase();
		String[] tokens = time.split("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)");
		char suffix = tokens[1].charAt(0);
		int amount = Integer.parseInt(tokens[0]);

		switch (suffix) {
			case 's':
				return amount;
			case 'm':
				return (long) amount * 60;
			case 'h':
				return (long) amount * 3600;
			case 'd':
				return (long) amount * 3600 * 24;
			case 'y':
				return (long) amount * 3600 * 24 * 365;
			default:
				return 0L;
		}
	}
}
