package at.ac.uibk.igwee.lucene.config;

/**
 * An observer for LuceneApplication Configuration
 * @author Joseph
 *
 */
public interface LuceneApplicationConfigObserver {

	/**
	 * This method is called if the lucene application base dir is changed.
	 * @param luceneBaseDir
	 * @throws Exception
	 */
	void changeLuceneApplicationBaseDir(String luceneBaseDir) throws Exception ;

}
