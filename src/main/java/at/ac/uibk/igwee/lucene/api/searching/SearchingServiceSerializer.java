package at.ac.uibk.igwee.lucene.api.searching;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serializer and Deserializer for Searching.
 * Useful for De-Serializing SearchRequest and SearchResults.
 *
 * Added Dependency on Jackson... Is this good?
 *
 * Created by joseph on 5/17/16.
 */
public class SearchingServiceSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     *
     * @param json json string of a searchRequest
     * @return SearchRequest
     * @throws LuceneSearchException if an exception occurs during conversion
     */
    public static SearchRequest json2searchRequest(String json) throws LuceneSearchException {
        try {
            return OBJECT_MAPPER.readValue(json, SearchRequest.class);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot deserialize json to SearchRequest.", e);
        }
    }

    /**
     *
     * @param request the SearchRequest to be serialized
     * @return a json string or empty string, if request is null
     * @throws LuceneSearchException when exception happens during serialization.
     */
    public static String searchRequest2json(SearchRequest request) throws LuceneSearchException {
        if (request==null) return "";
        try {
            return OBJECT_MAPPER.writeValueAsString(request);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot serialize SearchRequest to json.", e);
        }
    }

    /**
     *
     * @param json json string of a SearchResult
     * @return SearchResult
     * @throws LuceneSearchException if an exception occurs during conversion
     */
    public static SearchResult json2searchResult(String json) throws LuceneSearchException {
        try {
            return OBJECT_MAPPER.readValue(json, SearchResult.class);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot deserialize json to SearchResult.", e);
        }
    }

    /**
     *
     * @param result the SearchResult to be serialized
     * @return a json string or empty string, if request is null
     * @throws LuceneSearchException when exception happens during serialization.
     */
    public static String searchResult2json(SearchResult result) throws LuceneSearchException {
        if (result==null) return "";
        try {
            return OBJECT_MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot serialize SearchRequest to json.", e);
        }
    }

    /**
     *
     * @param json json string of a FacetSearchResult
     * @return FacetSearchResult
     * @throws LuceneSearchException if an exception occurs during conversion
     */
    public static FacetSearchResult json2facetSearchResult(String json) throws LuceneSearchException {
        try {
            return OBJECT_MAPPER.readValue(json, FacetSearchResult.class);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot deserialize json to SearchResult.", e);
        }
    }

    /**
     *
     * @param result the FacetSearchResult to be serialized
     * @return a json string or empty string, if request is null
     * @throws LuceneSearchException when exception happens during serialization.
     */
    public static String facetSearchResult2json(FacetSearchResult result) throws LuceneSearchException {
        if (result==null) return "";
        try {
            return OBJECT_MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot serialize SearchRequest to json.", e);
        }
    }


}
