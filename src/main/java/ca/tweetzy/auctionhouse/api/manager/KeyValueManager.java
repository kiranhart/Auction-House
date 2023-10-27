/*
 * Auction House
 * Copyright 2023 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.api.manager;

import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class KeyValueManager<K, V> extends Manager {

	protected final Map<K, V> managerContent = new ConcurrentHashMap<>();

	public KeyValueManager(@NonNull String name) {
		super(name);
	}

	public V get(@NonNull final K k) {
		return this.managerContent.getOrDefault(k, null);
	}

	public void add(@NonNull final K k, @NonNull final V v) {
		if (this.managerContent.containsKey(k)) return;
		this.managerContent.put(k, v);
	}

	public void remove(@NonNull final K k) {
		this.managerContent.remove(k);
	}

	public void clear() {
		this.managerContent.clear();
	}

	public Map<K, V> getManagerContent() {
		return this.managerContent;
	}
}