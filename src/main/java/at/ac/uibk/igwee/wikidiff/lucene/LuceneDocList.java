package at.ac.uibk.igwee.wikidiff.lucene;

import java.util.List;

/**
 * DataValueType for a list of LuceneDocs
 *
 * Created by joseph on 6/6/16.
 */
public class LuceneDocList {

    /**
     * List of LuceneDocs
     */
    public List<LuceneDoc> luceneDocs;

    public LuceneDocList() {
    }

    public LuceneDocList(List<LuceneDoc> luceneDocs) {
        this.luceneDocs = luceneDocs;
    }

    public List<LuceneDoc> getLuceneDocs() {
        return luceneDocs;
    }

    public void setLuceneDocs(List<LuceneDoc> luceneDocs) {
        this.luceneDocs = luceneDocs;
    }

    @Override
    public String toString() {
        return "LuceneDocList{" +
                "luceneDocs=" + luceneDocs +
                '}';
    }
}
