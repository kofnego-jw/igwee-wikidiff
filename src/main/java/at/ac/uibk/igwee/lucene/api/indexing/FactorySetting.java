package at.ac.uibk.igwee.lucene.api.indexing;

import java.util.HashMap;
import java.util.Map;

/**
 * FactorySetting is a configuration class for Lucene Tokenizers and Lucene TokenStreams.
 * @author Joseph
 *
 */
public final class FactorySetting implements java.io.Serializable {
	
	private static final long serialVersionUID = 201310211106L;
	
	/**
	 * factory name. Use TokenizerFactory.availableTokenizers() or TokenFilterFactory.availableTokenFilters() for 
	 * possible names.
	 */
	private String factoryName;
	
	/**
	 * Initation parameters.
	 */
	private Map<String,String> parameters;
	
	public FactorySetting() {
		super();
		parameters = new HashMap<String,String>();
	}
	
	private Object readResolve() {
		if (parameters==null) parameters = new HashMap<String,String>();
		return this;
	}

	/**
	 * Constructor with name and params.
	 * @param factoryName
	 * @param parameters
	 */
	public FactorySetting(String factoryName, Map<String,String> parameters) {
		this();
		this.factoryName = factoryName;
		setParameters(parameters);
	}
	
	
	
	
	
	/**
	 * Clones a map.
	 * @param params
	 * @return another HashMap<String,String> with the same content.
	 */
	private Map<String,String> cloneMap(Map<String,String> params) {
		Map<String,String> result = new HashMap<String,String>();
		result.putAll(params);
		return result;
	}
	/**
	 * 
	 * @return the name of the factory
	 */
	public String getFactoryName() {
		return factoryName;
	}

	/**
	 * 
	 * @param factoryName the name of the factory. 
	 * Uses values from TokenFilterFactory.availableFactories() 
	 * or TokenizerFactory.availableFactories().
	 */
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	/**
	 * 
	 * @return Parameters
	 */
	public Map<String, String> getParameters() {
		return cloneMap(parameters);
	}

	/**
	 * @param parameters
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	/**
	 * Adds a parameter.
	 * @param key
	 * @param value
	 */
	public void addParameter(String key, String value) {
		this.parameters.put(key, value);
	}
	/**
	 * Removes a parameter.
	 * @param key
	 */
	public void removeParameter(String key) {
		this.parameters.remove(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((factoryName == null) ? 0 : factoryName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FactorySetting other = (FactorySetting) obj;
		if (factoryName == null) {
			if (other.factoryName != null)
				return false;
		} else if (!factoryName.equals(other.factoryName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FactorySetting [factoryName=" + factoryName + ", parameters="
				+ parameters + "]";
	}

}
