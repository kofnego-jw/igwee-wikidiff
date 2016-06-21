package at.ac.uibk.igwee.lucene.api.indexing;

import java.util.ArrayList;
import java.util.List;

/**
 * AnalyzerSetting is a value object class capable of creating functioning
 * analyzers.
 * 
 * @author Joseph
 * 
 */
public final class AnalyzerSetting implements java.io.Serializable {

	private static final long serialVersionUID = 201410011105L;

	/**
	 * Name of the analyzer.
	 */
	private String analyzerName;
	
	/**
	 * List of FactorySetting for the character filters.
	 */
	private List<FactorySetting> charFilterSettings = new ArrayList<FactorySetting>();

	/**
	 * The FactorySetting for the TokenizerFactory.
	 */
	private FactorySetting tokenizerFactorySetting = new FactorySetting();
	/**
	 * List of FactorySetting for the token filters.
	 */
	private List<FactorySetting> tokenFilterFactorySettings = new ArrayList<FactorySetting>();

	public AnalyzerSetting() {
		super();
	}

	/**
	 * Additional Constructor
	 * 
	 * @param name
	 *            Name of the analyzer
	 * @param tokenizerFactorySetting
	 * @param tokenFilterFactorySettings
	 */
	public AnalyzerSetting(String name, FactorySetting tokenizerFactorySetting,
			List<FactorySetting> tokenFilterFactorySettings) {
		this();
		this.analyzerName = name;
		this.tokenizerFactorySetting = tokenizerFactorySetting;
		setTokenFilterFactorySettings(tokenFilterFactorySettings);
	}
	
	public AnalyzerSetting(String name, FactorySetting tokenizerFactorySetting, 
			FactorySetting tokenFilterFactorySetting) {
		this();
		this.analyzerName = name;
		this.tokenizerFactorySetting = tokenizerFactorySetting;
		this.tokenFilterFactorySettings.add(tokenFilterFactorySetting);
	}

	private Object readResolve() {
		if (charFilterSettings==null) charFilterSettings = new ArrayList<FactorySetting>();
		if (tokenFilterFactorySettings==null) tokenFilterFactorySettings = new ArrayList<FactorySetting>();
		return this;
	}

	/**
	 * @return the name of the analyzer
	 */
	public String getAnalyzerName() {
		return analyzerName;
	}

	/**
	 * @param analyzerName
	 *            name.
	 */
	public void setAnalyzerName(String analyzerName) {
		this.analyzerName = analyzerName;
	}
	
	/**
	 * @return the charFilterSettings
	 */
	public List<FactorySetting> getCharFilterSettings() {
		return charFilterSettings;
	}

	/**
	 * @param charFilterSettings the charFilterSettings to set
	 */
	public void setCharFilterSettings(List<FactorySetting> charFilterSettings) {
		this.charFilterSettings = charFilterSettings;
	}

	/**
	 * @return the FactorySetting for the TokenizerFactory
	 */
	public FactorySetting getTokenizerFactorySetting() {
		return tokenizerFactorySetting;
	}

	/**
	 * @param tokenizerFactorySetting
	 */
	public void setTokenizerFactorySetting(
			FactorySetting tokenizerFactorySetting) {
		this.tokenizerFactorySetting = tokenizerFactorySetting;
	}

	/**
	 * @return List of the FactorySetting for the TokenFilter
	 */
	public List<FactorySetting> getTokenFilterFactorySettings() {
		return tokenFilterFactorySettings;
	}

	/**
	 * @param tokenFilterFactorySettings
	 */
	public void setTokenFilterFactorySettings(
			List<FactorySetting> tokenFilterFactorySettings) {
		this.tokenFilterFactorySettings = tokenFilterFactorySettings;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((analyzerName == null) ? 0 : analyzerName.hashCode());
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
		AnalyzerSetting other = (AnalyzerSetting) obj;
		if (analyzerName == null) {
			if (other.analyzerName != null)
				return false;
		} else if (!analyzerName.equals(other.analyzerName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AnalyzerSetting [analyzerName=" + analyzerName
				+ ", tokenizerFactorySetting=" + tokenizerFactorySetting
				+ ", tokenFilterFactorySettings=" + tokenFilterFactorySettings
				+ "]";
	}
	
}
