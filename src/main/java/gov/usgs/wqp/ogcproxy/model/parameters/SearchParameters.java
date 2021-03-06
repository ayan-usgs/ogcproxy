package gov.usgs.wqp.ogcproxy.model.parameters;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("serial")
public class SearchParameters<T, K> extends ConcurrentHashMap<String, List<String>> {
	private static final int PRIME = 31;
	private int currentHash = 1;
	
	@Override
	public List<String> put(String key, List<String> value) {
		/**
		 * We need to keep track of these hash keys.  If we replace an existing
		 * one we have to make sure we remove it before placing it.
		 */
		if (super.containsKey(key)) {
			this.remove(key);
		}
		
		int newHash = PRIME + key.hashCode();
		
		for (String s : value) {
			newHash = newHash * PRIME + s.hashCode();
		}
		
		this.currentHash += newHash;
		
		return super.put(key, value);
	}
	
	@Override
	public List<String> remove(Object key) {
		List<String> oldValue = super.remove(key);
		
		if (oldValue == null) {
			return Collections.emptyList();
		}
		
		int oldHash = PRIME + ((String)key).hashCode();
		
		for (String s : oldValue) {
			oldHash = oldHash * PRIME + s.hashCode();
		}
		
		this.currentHash -= oldHash;
		
		return oldValue;
	}
	
	@Override
	public int hashCode() {
		return this.currentHash;
	}
	
	public String unsignedHashCode() {
		/**
		 * Since we want to use the hash as a descriptor for a layer, we want
		 * to use unsigned values for the hash. We also only use it as a string.
		 */
		if (this.currentHash > 0) {
			return (long)this.currentHash + "";
		}
		
		long unsignedHash = this.currentHash & (-1L >>> 32);
		
		return unsignedHash + "";
	}

	@Override
	public boolean equals(Object o) {
		/** 
		 * The super.equals(Object) is ok in our case - adding this to show that we investigated the SonarQube issue report.
		 */
		return super.equals(o);
	}

}
