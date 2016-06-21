package at.ac.uibk.igwee.lucene.impl;

import java.io.Closeable;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.uibk.igwee.lucene.api.LuceneApplicationConfig;
import at.ac.uibk.igwee.lucene.api.serializer.LuceneApplicationSerializer;
import at.ac.uibk.igwee.lucene.api.LuceneException;
import at.ac.uibk.igwee.lucene.api.highlighting.HighlightingService;
import at.ac.uibk.igwee.lucene.api.indexing.IndexingService;
import at.ac.uibk.igwee.lucene.api.searching.SearchingService;
import at.ac.uibk.igwee.lucene.api.viewing.ViewingService;
import at.ac.uibk.igwee.lucene.config.LuceneApplicationConfigObserver;
import at.ac.uibk.igwee.lucene.impl.highlighting.HighlightingServiceImpl;
import at.ac.uibk.igwee.lucene.impl.indexing.IndexingServiceImpl;
import at.ac.uibk.igwee.lucene.impl.searching.SearchingServiceImpl;
import at.ac.uibk.igwee.lucene.impl.viewing.ViewingServiceImpl;

/**
 * Main Implementation class for the Lucene Application
 * @author Joseph
 *
 */
public class LuceneApplication implements LuceneApplicationConfigObserver, Closeable {

	/**
	 * Default ending for a taxonomy directory
	 */
	public static final String TAXONOMY_DIRECTORY_DEFAULT_ENDING = "_taxonomy";

	/**
	 * Configuration
	 */
	private LuceneApplicationConfig configuration;

	/**
	 * DirHolder
	 */
	private LuceneDirHolder dirHolder;

	/**
	 * FacetsConfigHolder
	 */
	private FacetsConfigHolder facetsConfigHolder;

	/**
	 * IndexingServiceImpl
	 */
	private IndexingServiceImpl indexingService;
	
	/**
	 * SearchingServiceImpl
	 */
	private SearchingServiceImpl searchingService;
	
	/**
	 * HighlightingServiceImpl
	 */
	private HighlightingServiceImpl highlightingService;
	
	/**
	 * ViewingServiceImpl
	 */
	private ViewingServiceImpl viewingService;

	private static final Logger LOGGER = LoggerFactory.getLogger(LuceneApplication.class);
	

	public LuceneApplication() {
		super();
	}

	/**
	 * Tries to load the application using the configuration found in the baseDir.
	 * @param baseDir The baseDir (not the configuration dir)
	 * @throws LuceneException
	 */
	public LuceneApplication(String baseDir) throws LuceneException {
		this();
		LOGGER.info("Creating Lucene Application with basedir {}.", baseDir);
		loadApplicationConfig(baseDir);
		createDirHolder();
		createFacetsConfigHolder();
	}

	/**
	 * 
	 * @param config Configuration object
	 * @throws LuceneException
	 */
	public LuceneApplication(LuceneApplicationConfig config)
			throws LuceneException {
		this();
        LOGGER.info("Creating LuceneApplication with existent LuceneApplicationConfig.");
		this.configuration = config;
		createDirHolder();
		createFacetsConfigHolder();
	}
	
	
	@Override
	public void close() {
		LOGGER.info("LuceneApplication closing...");
		try {
			// TODO Closing all services.
			if (searchingService!=null) searchingService.close();
			if (dirHolder!=null) 
				dirHolder.close();
			
		} catch (Exception e) {
			LOGGER.warn("Error while closing the LuceneApplication. ", e);
//			e.printStackTrace();
		}
		dirHolder = null;
		this.configuration = null;
		this.searchingService = null;
		this.indexingService = null;
		this.viewingService = null;
		this.facetsConfigHolder = null;
		LOGGER.info("LuceneApplication close successful.");
	}
	
	
	private void createDirHolder() throws LuceneException {
		if (dirHolder != null)
			dirHolder.close();
		dirHolder = LuceneDirHolderFactory.getLuceneDirHolder(configuration);
		LOGGER.info("Start Lucene Application with dirHolder to: " + dirHolder.getBaseDir());
	}

	private void createFacetsConfigHolder() {
		if (this.configuration!=null)
			facetsConfigHolder = new FacetsConfigHolder(this.configuration.getIndexSettings());
		else
			facetsConfigHolder = new FacetsConfigHolder(Collections.emptyList());
	}

	
	
	/**
	 * @return the dirHolder
	 */
	public LuceneDirHolder getDirHolder() {
		return dirHolder;
	}

	/**
	 * @param dirHolder the dirHolder to set
	 */
	public void setDirHolder(LuceneDirHolder dirHolder) {
		this.dirHolder = dirHolder;
	}

	/**
	 * 
	 * @return IndexingService
	 * @throws LuceneException
	 */
	public IndexingService getIndexingService() throws LuceneException {
		if (dirHolder == null)
			createDirHolder();
		if (facetsConfigHolder==null)
			createFacetsConfigHolder();
		if (indexingService == null) {
			indexingService = new IndexingServiceImpl(dirHolder, facetsConfigHolder,
					configuration.getIndexSettings());
		}
		return indexingService;
	}
	
	/**
	 * 
	 * @return SearchingService
	 * @throws LuceneException
	 */
	public SearchingService getSearchingService() throws LuceneException {
		if (dirHolder == null)
			createDirHolder();
		if (facetsConfigHolder==null)
			createFacetsConfigHolder();
		if (searchingService == null) {
			searchingService = new SearchingServiceImpl(dirHolder, facetsConfigHolder,
					configuration.getIndexSettings(),
					configuration.getSearchSettings());
		}
		return searchingService;
	}
	
	/**
	 * 
	 * @return ViewingService
	 * @throws LuceneException
	 */
	public ViewingService getViewingService() throws LuceneException {
		if (dirHolder==null)
			createDirHolder();
		if (viewingService==null) {
			viewingService = new ViewingServiceImpl(configuration.getViewSettings());
		}
		return viewingService;
	}

	/**
	 *
	 * @return the HighlightlingService
	 * @throws LuceneException
     */
	public HighlightingService getHighlightingService() throws LuceneException {
		if (dirHolder==null)
			createDirHolder();
		if (facetsConfigHolder==null)
			createFacetsConfigHolder();
		if (highlightingService == null) {
			highlightingService = new HighlightingServiceImpl(facetsConfigHolder, configuration.getIndexSettings(), configuration.getSearchSettings());
		}
		return this.highlightingService;
	}

	/**
	 * 
	 * @return the Configuration object
	 */
	public LuceneApplicationConfig getConfiguration() {
		if (configuration==null)
			configuration = new LuceneApplicationConfig();
		return configuration;
	}

	/**
	 * Will close the configuration object first, if it is not null. 
	 * @param configuration
	 * @throws LuceneException
	 */
	public void setConfiguration(LuceneApplicationConfig configuration)
			throws LuceneException {
		if (configuration==this.configuration) return;
		if (configuration != null)
			close();
		this.configuration = configuration;
		createDirHolder();
	}

	/**
	 * Saves the configuration.
	 * @throws LuceneException
	 */
	public void saveConfiguration() throws LuceneException {
		LOGGER.info("Saving Configuration");
		LuceneApplicationSerializer.saveLuceneApplicationConfigFile(configuration);
	}

	/**
	 * Loads the configuration. The current configuraiton will be closed first.
	 * @param baseDir
	 * @throws LuceneException
	 */
	public void loadApplicationConfig(String baseDir) throws LuceneException {
		
		LuceneApplicationConfig result = LuceneApplicationSerializer.loadApplicationConfig(baseDir);

		setConfiguration(result);
	}

	@Override
	public void changeLuceneApplicationBaseDir(String baseDir) throws LuceneException {
		System.out.println("changeLuceneApplicationBaseDir() in LuceneApplication called.");
		close();
		this.loadApplicationConfig(baseDir);
	}

    /**
     * static method to quickly create a LuceneApplication, based on RAM
     * @param configDir the configurationDir
     * @return a Lucene Application
     * @throws LuceneException when loading configuration dir is not successful.
     */
	public static LuceneApplication createRAMBasedLuceneApplication(String configDir) throws LuceneException {
        LuceneApplicationConfig config = LuceneApplicationSerializer.loadApplicationConfig(configDir);
        config.setIndexBaseDir(RAMLuceneDirHolder.RAM_BASEDIR);
        return new LuceneApplication(config);
	}

}
