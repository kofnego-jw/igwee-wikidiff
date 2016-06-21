package at.ac.uibk.igwee.lucene.api.searching;

import java.util.ArrayList;
import java.util.List;

import at.ac.uibk.igwee.lucene.api.Constants;
import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;

/**
 * Setting for searching.
 * @author Joseph
 *
 */
public class SearchSetting implements java.io.Serializable {
	/**
	 * Default starttag
	 */
	private static final String DEFAULT_STARTTAG = "<b>";
	/**
	 * default endtag
	 */
	private static final String DEFAULT_ENDTAG = "</b>";
	
	private static final ResultSetting EMPTY_RESULTSETTING = new ResultSetting("", ResultMode.NEVER);
	
	private static final long serialVersionUID = 201312091438L;
	/**
	 * Name of the index-
	 */
	private String indexName;
	/**
	 * default field used for searching.
	 */
	private String defaultSearchField;
	
	/**
	 * resultSettings.
	 */
	private List<ResultSetting> resultSettings;

	/**
	 * starttag
	 */
	private String highlightStartTag = DEFAULT_STARTTAG;
	/**
	 * endtag
	 */
	private String highlightEndTag = DEFAULT_ENDTAG;
	/**
	 * Should the highlight occur with PostingsHighlighter? Default is no
	 */
	private boolean usePostingsHighlighter = false;
	
	/**
	 * Maximum length used for PostingsHighlighter
	 */
	private int postingsHighlighterMaxLength = 10000;
	
	/**
	 * Name of the custom id field. Should be IndexingService.DOC_ID_FIELDNAME really.
	 */
	private String idFieldname;

	/**
	 * Maximal size of the facet list.
	 */
	private int maxFacetListSize = 10;
	
	public SearchSetting() {
		super();
	}
		
	/**
	 * 
	 * @param fieldname
	 * @return the result setting for the given fieldname, if not found, it will return EMPTY_RESULTSETTING.
	 */
	public ResultSetting getResultSetting(String fieldname) {
		for (ResultSetting now: resultSettings)
			if (now.getFieldname().equals(fieldname)) return now;
		return EMPTY_RESULTSETTING;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return indexName;
	}

	/**
	 * @param indexName the indexName to set
	 */
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	/**
	 * @return the defaultSearchField
	 */
	public String getDefaultSearchField() {
		return defaultSearchField;
	}

	/**
	 * @param defaultSearchField the defaultSearchField to set
	 */
	public void setDefaultSearchField(String defaultSearchField) {
		this.defaultSearchField = defaultSearchField;
	}

	/**
	 * @return the resultSettings
	 */
	public List<ResultSetting> getResultSettings() {
		return resultSettings;
	}

	/**
	 * @param resultSettings the resultSettings to set
	 */
	public void setResultSettings(List<ResultSetting> resultSettings) {
		nonNullResultSettings();
		this.resultSettings.clear();
		this.resultSettings.addAll(resultSettings);
	}
	
	public void addResultSetting(ResultSetting rs) {
		nonNullResultSettings();
		if (!this.resultSettings.contains(rs))
			this.resultSettings.add(rs);
	}
	
	public void removeResultSetting(ResultSetting rs) {
		this.resultSettings.remove(rs);
	}
	
	public void removeResultSetting(String fieldname) {
		ResultSetting toRem = null;
		for (ResultSetting now: this.resultSettings)
			if (now.getFieldname().equals(fieldname)) {
				toRem = now;
				break;
			}
		if (toRem!=null)
			removeResultSetting(toRem);
	}
	
	private void nonNullResultSettings() {
		if (this.resultSettings==null) this.resultSettings = new ArrayList<ResultSetting>();
	}


	/**
	 * @return the highlightStartTag
	 */
	public String getHighlightStartTag() {
		return highlightStartTag;
	}

	/**
	 * @param highlightStartTag the highlightStartTag to set
	 */
	public void setHighlightStartTag(String highlightStartTag) {
		this.highlightStartTag = highlightStartTag;
	}

	/**
	 * @return the highlightEndTag
	 */
	public String getHighlightEndTag() {
		return highlightEndTag;
	}

	/**
	 * @param highlightEndTag the highlightEndTag to set
	 */
	public void setHighlightEndTag(String highlightEndTag) {
		this.highlightEndTag = highlightEndTag;
	}

	/**
	 * @return the usePostingsHighlighter
	 */
	public boolean isUsePostingsHighlighter() {
		return usePostingsHighlighter;
	}

	/**
	 * @param usePostingsHighlighter the usePostingsHighlighter to set
	 */
	public void setUsePostingsHighlighter(boolean usePostingsHighlighter) {
		this.usePostingsHighlighter = usePostingsHighlighter;
	}

	/**
	 * @return the idFieldname
	 */
	public String getIdFieldname() {
		return idFieldname == null || idFieldname.isEmpty() ? Constants.DOC_ID_DEFAULT_FIELDNAME : idFieldname;
	}

	/**
	 * @param idFieldname the idFieldname to set
	 */
	public void setIdFieldname(String idFieldname) {
		this.idFieldname = idFieldname;
	}
	
	

	/**
	 * @return the postingsHighlighterMaxLength
	 */
	public int getPostingsHighlighterMaxLength() {
		return postingsHighlighterMaxLength;
	}

	/**
	 * @param postingsHighlighterMaxLength the postingsHighlighterMaxLength to set
	 */
	public void setPostingsHighlighterMaxLength(int postingsHighlighterMaxLength) {
		this.postingsHighlighterMaxLength = postingsHighlighterMaxLength;
	}

	/**
	 * @return the maxFacetListSize
	 */
	public int getMaxFacetListSize() {
		return maxFacetListSize;
	}

	/**
	 * @param maxFacetListSize the maxFacetListSize to set
	 */
	public void setMaxFacetListSize(int maxFacetListSize) {
		this.maxFacetListSize = maxFacetListSize;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indexName == null) ? 0 : indexName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchSetting other = (SearchSetting) obj;
		if (indexName == null) {
			if (other.indexName != null)
				return false;
		} else if (!indexName.equals(other.indexName))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchSetting [indexName=" + indexName
				+ ", defaultSearchField=" + defaultSearchField
				+ ", resultSettings=" + resultSettings + ", highlightStartTag="
				+ highlightStartTag + ", highlightEndTag=" + highlightEndTag
				+ ", usePostingsHighlighter=" + usePostingsHighlighter
				+ ", postingsHighlighterMaxLength="
				+ postingsHighlighterMaxLength + ", idFieldname=" + idFieldname
				+ "]";
	}
	
	public SearchSetting readResolve() {
		if (this.highlightEndTag==null) this.highlightEndTag = DEFAULT_ENDTAG;
		if (this.highlightStartTag==null) this.highlightStartTag = DEFAULT_STARTTAG;
		nonNullResultSettings();
		return this;
	}

	
	
	
}
