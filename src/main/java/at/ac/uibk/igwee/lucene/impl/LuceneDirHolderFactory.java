package at.ac.uibk.igwee.lucene.impl;

import at.ac.uibk.igwee.lucene.api.LuceneApplicationConfig;
import at.ac.uibk.igwee.lucene.api.serializer.LuceneApplicationSerializer;
import at.ac.uibk.igwee.lucene.api.LuceneException;

/**
 * DirHolderFactory Creates the dirholder.
 * @author Joseph
 *
 */
public class LuceneDirHolderFactory {
	
	/**
	 * 
	 * @param config
	 * @return the LuceneDirHolder using the configuration object
	 * @throws LuceneException
	 */
	public static LuceneDirHolder getLuceneDirHolder(LuceneApplicationConfig config) throws LuceneException {
		return getLuceneDirHolder(config.getIndexBaseDir());
	}
	
	/**
	 * 
	 * @param baseDir
	 * @return the LuceneDirHodler using the baseDir. If baseDir = RAMLuceneDirHolder.RAM_BASEDIR, then a RAMLuceneDirHolder will
	 * be returned.
	 * @throws LuceneException
	 */
	public static LuceneDirHolder getLuceneDirHolder(String baseDir) throws LuceneException {
		if (baseDir!=null && !baseDir.equals(RAMLuceneDirHolder.RAM_BASEDIR))
			return new FileLuceneDirHolder(baseDir);
		LuceneApplicationConfig config = LuceneApplicationSerializer.loadApplicationConfig(baseDir);
		return new RAMLuceneDirHolder(config);
	}
	
	

}
