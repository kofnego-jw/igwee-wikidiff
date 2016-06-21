package at.ac.uibk.igwee.lucene.api.viewing;

import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import at.ac.uibk.igwee.lucene.api.searching.FacetSearchResult;
import at.ac.uibk.igwee.lucene.api.searching.ResultDocument;
import at.ac.uibk.igwee.lucene.api.searching.ResultField;
import at.ac.uibk.igwee.lucene.api.searching.SearchResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates ViewResult from SearchResult
 * Created by joseph on 4/4/16.
 */
public class ViewResultFactory {

    public static ViewResult createViewResult(final SearchResult searchResult,
                                              final Locale locale,
                                              final ViewSetting viewSetting) {

        List<ViewDocument> documents = createViewDocuments(searchResult, locale, viewSetting);

        List<FacetSearchResult> facetSearchResults = createFacetSearchResults(searchResult, locale, viewSetting);

        return new ViewResult(searchResult, locale, documents, facetSearchResults);

    }

    protected static List<FacetSearchResult> createFacetSearchResults(SearchResult searchResult, Locale locale, ViewSetting viewSetting) {

        List<FacetSearchResult> collect = searchResult.getFacetResults()
                .stream()
                .map(facetSearchResult -> createFacetSearchResult(facetSearchResult, locale, viewSetting))
                .collect(Collectors.toList());
        return collect;
    }

    protected static FacetSearchResult createFacetSearchResult(FacetSearchResult facetSearchResult, Locale locale, ViewSetting setting) {
        FacetContent translation = findBestTranslation(facetSearchResult.toFacetContent(), locale, setting, new LinkedList<>());
        if (facetSearchResult.getChildren()==null || facetSearchResult.getChildren().isEmpty())
            return new FacetSearchResult(facetSearchResult.getChildCount(),translation, null, facetSearchResult.getValue());
        List<FacetSearchResult> children = facetSearchResult.getChildren().stream()
                .map(childResult -> {
                    FacetContent bestTranslation = findBestTranslation(childResult.toFacetContent(), locale, setting, new LinkedList<>());
                    return new FacetSearchResult(childResult.getChildCount(), bestTranslation, null, childResult.getValue());
                })
                .collect(Collectors.toList());
        return new FacetSearchResult(facetSearchResult.getChildCount(), translation.getDimension(), children,
                translation.getPath(), facetSearchResult.getValue());
    }

    protected static FacetContent findBestTranslation(FacetContent content,
                                                      Locale locale,
                                                      ViewSetting viewSetting,
                                                      LinkedList<String> children) {
        if (content==null) return new FacetContent(children);
        Optional<FacetTranslation> any = viewSetting.getFacetTranslations()
                .stream()
                .filter(facetTranslation -> facetTranslation.isTranslationOf(content)).findAny();
        if (any.isPresent()) {
            FacetTranslation ft = any.get();
            FacetContent translation = ft.getTranslation(locale);
            List<String> components = new ArrayList<>();
            components.addAll(translation.getComponents());
            components.addAll(children);
            return new FacetContent(components);
        }
        children.addFirst(content.getLastComponent());
        return findBestTranslation(content.getParentNode(), locale, viewSetting, children);
    }

    protected static List<ViewDocument> createViewDocuments(SearchResult sr, Locale locale, ViewSetting vs) {

        return sr.getResultDocuments().stream()
                .map(resultDocument -> createViewDocument(resultDocument, locale, vs)).collect(Collectors.toList());

    }

    protected static ViewDocument createViewDocument(ResultDocument resultDocument, Locale locale, ViewSetting viewSetting) {

        List<DecoratorSetting> abstractDecoSettings = viewSetting.getDecoratorSettings()
                .stream()
                .filter(decoratorSetting -> decoratorSetting.isPartOfAbstract()).collect(Collectors.toList());

        List<ViewField> viewFields = viewSetting.getDecoratorSettings().stream()
                .map(decoratorSetting -> createViewFields(resultDocument, locale, decoratorSetting))
                .filter(vf -> vf!=null)
                .collect(Collectors.toList());

        List<ViewField> abstractFields = abstractDecoSettings.stream()
                .map(decoratorSetting -> createViewFields(resultDocument, locale, decoratorSetting))
                .filter(vf -> vf!=null)
                .collect(Collectors.toList());

        return new ViewDocument(resultDocument.getIndexName(), resultDocument.getId(), viewFields, abstractFields);
    }


    protected static ViewField createViewFields(ResultDocument resultDocument, Locale locale, DecoratorSetting decoratorSetting) {
        String fieldname = decoratorSetting.getFieldname();
        System.out.println("Search For Field " + fieldname);
        Optional<ResultField> any =
                resultDocument.getFields().stream().filter(resultField -> resultField.getFieldname().equals(fieldname)).findAny();
        if (any.isPresent()) {
            String readableName = decoratorSetting.getReadableName(locale);
            String description = decoratorSetting.getDescription(locale);
            return new ViewField(fieldname, readableName, description, any.get().getContents());
        }
        return null;
    }


}
