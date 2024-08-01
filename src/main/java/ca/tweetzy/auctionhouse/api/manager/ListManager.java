package ca.tweetzy.auctionhouse.api.manager;

import lombok.NonNull;

import java.util.*;

public abstract class ListManager<T> extends Manager {

	protected final List<T> managerContent = Collections.synchronizedList(new ArrayList<>());

	public ListManager(@NonNull String name) {
		super(name);
	}

	public Optional<T> get(@NonNull final T t) {
		synchronized (this.managerContent) {
			return this.managerContent.stream().filter(contents -> contents == t).findFirst();
		}
	}

	public void add(@NonNull final T t) {
		synchronized (this.managerContent) {
			if (this.managerContent.contains(t)) return;
			this.managerContent.add(t);
		}
	}

	public void addAll(@NonNull final Collection<T> t) {
		synchronized (this.managerContent) {
			this.managerContent.addAll(t);
		}
	}

	public void remove(@NonNull final T t) {
		synchronized (this.managerContent) {
			this.managerContent.remove(t);
		}
	}

	public void clear() {
		synchronized (this.managerContent) {
			this.managerContent.clear();
		}
	}

	public List<T> getManagerContent() {
		synchronized (this.managerContent) {
			return new ArrayList<>(this.managerContent);
		}
	}
}