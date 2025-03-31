package ca.tweetzy.auctionhouse.helpers;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public final class SlotHelper {

	public List<Integer> getButtonSlots(String string) {
		final List<Integer> slots = new ArrayList<>();

		try {
			int slot = Integer.parseInt(string);
			slots.add(slot);
		} catch (NumberFormatException e) {
			// multi-slot probs
			if (string.contains("-")) {
				final String[] slotSplit = string.split("-");
				slots.addAll(IntStream.rangeClosed(Integer.parseInt(slotSplit[0]), Integer.parseInt(slotSplit[1])).boxed().collect(Collectors.toList()));
			} else if (string.contains(",")) {
				final String[] slotSplit = string.split(",");
				for (String s : slotSplit) {
					slots.add(Integer.parseInt(s));
				}
			}
		}

		return slots;
	}
}
