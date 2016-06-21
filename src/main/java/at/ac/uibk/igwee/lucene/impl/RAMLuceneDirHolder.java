package at.ac.uibk.igwee.lucene.impl;

import java.util.HashMap;
import java.util.Map;

import at.ac.uibk.igwee.lucene.api.LuceneApplicationConfig;
import at.ac.uibk.igwee.lucene.api.LuceneException;
import at.ac.uibk.igwee.lucene.api.indexing.FacetSetting;
import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class implements a LuceneDirHolder using an in-memory-directory.
 * 
 * @author Joseph
 *
 */
public class RAMLuceneDirHolder implements LuceneDirHolder {
	
	/**
	 * RAM_BASEDIR. ".:/RAMDirectory\\:." This is recognized as the "root" of the lucene application
	 */
	public static final String RAM_BASEDIR = ".:/RAMDirectory\\:.";
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RAMLuceneDirHolder.class);
	
	/**
	 * Several RAMDirectorys are used to "simulate" the directory behaviour.
	 */
	protected Map<String,RAMDirectory> ramDirectories = new HashMap<>();

    protected Map<String,FacetsConfig> facetsConfigs = new HashMap<>();

	/**
	 * LuceneApplicationConfig
	 */
	protected final LuceneApplicationConfig luceneApplicationConfig;
	
	public RAMLuceneDirHolder(LuceneApplicationConfig config) {
		super();
		this.luceneApplicationConfig = config;
		LOGGER.info("RAMLuceneDirHolder initialized.");
	}


	@Override
	public String getBaseDir() {
		return RAM_BASEDIR;
	}

	@Override
	public void close() {
		
		for (Map.Entry<String, RAMDirectory> entry: ramDirectories.entrySet()) {
			RAMDirectory dir = entry.getValue();
			if (dir!=null) dir.close();
		}
		
		ramDirectories.clear();
		
	}
	
	@Override
	public Directory getDirectory(String dirName) {
		RAMDirectory rd = ramDirectories.get(dirName);
		if (rd==null) {
			rd = new RAMDirectory();
			ramDirectories.put(dirName, rd);
		}
		
		return rd;
	}

}
