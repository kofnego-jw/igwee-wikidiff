package at.ac.uibk.igwee.lucene.impl;

import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.store.Directory;

import at.ac.uibk.igwee.lucene.api.LuceneException;

/**
 * Contract for LuceneDirHolder
 * @author Joseph
 *
 */
public interface LuceneDirHolder {
	
	/**
	 * Closes the dir holder.
	 * @throws LuceneException
	 */
	void close() throws LuceneException;
	
	/**
	 * 
	 * @return BaseDir 
	 */
	String getBaseDir();

	/**
	 * 
	 * @param dirName
	 * @return The directory with the dirName as name
	 * @throws LuceneException
	 */
	Directory getDirectory(String dirName) throws LuceneException;


}
