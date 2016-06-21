package at.ac.uibk.igwee.lucene.testmock;

import at.ac.uibk.igwee.lucene.api.searching.*;

import java.util.List;

/**
 * Helper class for outputting search request and search results to System.out..
 *
 * Created by totoro on 01.05.16.
 */
public class SOutHelper {

    public static void output(SearchRequest req) {
        System.out.println("Search Request: ");
        if (req==null) {
            System.out.println("  null!");
            return;
        }
        System.out.println("  Indexname: " + req.getIndexName());
        System.out.println("  Paging: page " + req.getPageNumber() + " by " + req.getPageSize());
        System.out.println("  QuerySettings: ");
        if (req.getQuerySettings()==null || req.getQuerySettings().isEmpty()) {
            System.out.println("    none.");
        } else {
            req.getQuerySettings().stream()
                    .forEach(x -> System.out.println("    " + x.getFieldname() + ": " + x.getQueryString()));
        }
        System.out.println("  SortSettings: ");
        if (req.getSortRules()==null || req.getSortRules().isEmpty()) {
            System.out.println("    none.");
        } else {
            req.getSortRules().forEach(s -> System.out.println("    " + s.getFieldname() + " "
                    + (s.isAscending() ? "asc" : "desc")));
        }
        System.out.println("  Facets: ");
        if (req.getDrillDownFacets()==null || req.getDrillDownFacets().isEmpty()) {
            System.out.println("    none.");
        } else {
            req.getDrillDownFacets().forEach(x -> System.out.println("    " + x.getComponents()));
        }
    }

    public static void output(SearchResult res) {
        System.out.println("SearchResult: ");
        if (res==null) {
            System.out.println("  null.");
            return;
        } else if (res.getResultDocuments()==null || res.getResultDocuments().isEmpty()) {
            System.out.println("  empty.");
            return;
        }
        System.out.println("  Totalhits: " + res.getTotalHits());
        System.out.println("  FacetResults: ");
        if (res.getFacetResults()==null || res.getFacetResults().isEmpty()) {
            System.out.println("    none.");
        } else {
            res.getFacetResults().forEach(x -> output(x, "    "));
        }
        System.out.println("  ResultDocuments:");
        res.getResultDocuments().forEach(x -> {
            System.out.println("    " + x.getId());
            x.getFields().forEach(y -> {
                System.out.println("      " + y.getFieldname());
                y.getContents().forEach(z -> System.out.println("        " + z));
            });
        });
    }

    public static void output(FacetSearchResult fr, String indent) {
        System.out.println(indent + "FacetSearchResult: ");
        if (fr==null) {
            System.out.println(indent + "  null.");
            return;
        }
        System.out.println(indent + "  " + fr.getFacetContent().getComponents() + " " + fr.getValue());
        System.out.println(indent + "  Children: " + fr.getChildCount());
        if (fr.getChildren()!=null && !fr.getChildren().isEmpty()) {
            fr.getChildren().forEach(x -> output(x, indent + "  "));
        }
    }

    public static void output(TermFrequency f, String indent) {
        System.out.println(indent + f.getTerm() + " (" + f.getField() + ") shows up "
                + f.getTotalCount() + " times in " + f.getDocumentCount() + " documents.");
    }

    public static void output(TermDocs td, String indent) {
        String reduce = String.join(", ", td.getDocIds());
        System.out.println(indent + td.getTerm() + " (" + td.getFieldname() + ") shows up in: " + reduce);
    }

    public static void outputTermFrequencies(List<TermFrequency> freqs) {
        System.out.println("TermFrequencies: ");
        if (freqs==null || freqs.isEmpty()) {
            System.out.println("  empty.");
            return;
        }
        freqs.forEach(f -> output(f, "  "));
    }

    public static void outputTermDocses(List<TermDocs> list) {
        System.out.println("TermDocsList: ");
        if (list==null || list.isEmpty()) {
            System.out.println("  empty.");
            return;
        }
        list.forEach(t -> output(t, "  "));
    }

}
