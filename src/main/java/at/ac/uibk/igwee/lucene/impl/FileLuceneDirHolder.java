package at.ac.uibk.igwee.lucene.impl;

import java.io.Closeable;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import at.ac.uibk.igwee.lucene.api.indexing.FacetSetting;
import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.uibk.igwee.lucene.api.LuceneApplicationConfig;
import at.ac.uibk.igwee.lucene.api.LuceneException;
import at.ac.uibk.igwee.lucene.api.indexing.LuceneIndexException;
/**
 * LuceneDirholder using a File as root.
 * @author Joseph
 *
 */
public final class FileLuceneDirHolder implements Closeable, LuceneDirHolder {

	/**
	 * Directories
	 */
	private LinkedHashMap<String, Directory> directories = new LinkedHashMap<>();

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileLuceneDirHolder.class);
	
	/**
	 * Base directory = root
	 */
	private File baseDir;
	
	/**
	 * A string pointing to the base directory --> LuceneApplicationRoot
	 * @param baseDir
	 */
	public FileLuceneDirHolder(String baseDir) {
		super();
		setBaseDirectory(baseDir);
	}

	/**
	 * Use a config object. The config object should have its IndexBaseDir set.
	 * @param config
	 * @throws LuceneException
	 */
	public FileLuceneDirHolder(LuceneApplicationConfig config)
			throws LuceneException {
		this(config.getIndexBaseDir());
		init(config.getAllIndexNames());
	}

	
	private void init(Set<String> indexNames) {
		if (directories != null && !directories.isEmpty()) {
			close();
		}
		directories = new LinkedHashMap<>();
		if (indexNames==null) 
			return;
		for (String in: indexNames) {
			try {
				addDirectory(in);
			} catch (Exception e) {
				LOGGER.error("Cannot create the LuceneDirectory '" + in + "'.", e);
			}
		}
	}
	
	/**
	 * Adds a directory to the map.
	 * @param indexName
	 * @return
	 * @throws LuceneException
	 */
	private Directory addDirectory(String indexName) throws LuceneException {
		Directory dir = createIndexDirectory(indexName);
		if (dir!=null)
			directories.put(indexName, dir);
		return dir;
	}
	
	/**
	 * returns the directory called by the index name
	 */
	public Directory getDirectory(String indexName) throws LuceneException {
		Directory dir = directories.get(indexName);
		if (dir!=null)
			return dir;
		return addDirectory(indexName);
	}

	/**
	 * Closes the DirHolder
	 */
	@Override
	public void close() {
		if (directories == null || directories.isEmpty()) {
			return;
		}
		Set<String> dirNames = new HashSet<String>();
		dirNames.addAll(directories.keySet());
		for (String now: dirNames) {
			try {
				close(now);
			} catch (Exception e) {
				LOGGER.warn("Cannot close index '" + now + "'.", e);
			}
		}
		assert(directories.isEmpty());
		directories = null;
	}

	/**
	 * Closes an index.
	 * @param indexName
	 * @throws LuceneException
	 */
	public void close(String indexName) throws LuceneException {
		Directory dir = directories.get(indexName);
		try {
			if (dir!=null) dir.close();
		} catch (Exception e) {
			throw new LuceneException("Cannot close the directory '"
					+ indexName + "'.", e);
		} finally {
			directories.remove(indexName);
		}
	}

	/**
	 * 
	 * @param indexName
	 * @return a Lucene-Directory in baseDir/indexName
	 * @throws LuceneIndexException
	 */
	private Directory createIndexDirectory(String indexName)
			throws LuceneIndexException {
		File dir = new File(baseDir, indexName);
		if (!dir.exists())
			dir.mkdir();
		try {
			return FSDirectory.open(dir.toPath());
		} catch (Exception e) {
			throw new LuceneIndexException("Cannot create a directory '"
					+ indexName + "'.", e);
		}
	}
	
	/**
	 * The absolute path of the base dir.
	 */
	public String getBaseDir() {
		return baseDir.getAbsolutePath();
	}

	/**
	 * Sets the base Dir. Should not be called from outside.
	 * @param baseDir
	 */
	protected void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
		LOGGER.info("FileLuceneDirHolder uses {} as base directory.", baseDir);
	}

	/**
	 * Use this method to set the LuceneApplicationRoot.
	 * @param baseDir
	 */
	public void setBaseDirectory(String baseDir) {
		File test = new File(baseDir);
		if (!test.exists())
			if (!test.mkdirs())
				return;
		if (!test.isDirectory()) {
			test = test.getParentFile();
		}
		setBaseDir(test);
	}

	

}
