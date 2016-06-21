package at.ac.uibk.igwee.wikidiff.web;

/**
 * DataValueType for Revisions
 *
 * Created by Joseph on 03.06.2016.
 */
public class RevisionFW implements Comparable<RevisionFW> {

    public String id;
    public String timestamp;
    public Contributor contributor;
    public String comment;

    public RevisionFW() {
    }

    public RevisionFW(String id, String timestamp, Contributor contributor, String comment) {
        this.id = id;
        this.timestamp = timestamp;
        this.contributor = contributor;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int compareTo(RevisionFW o) {
        if (o==null || o.timestamp==null) return 1;
        return timestamp.compareTo(o.timestamp);
    }

    @Override
    public String toString() {
        return "RevisionFW{" +
                "id='" + id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", contributor=" + contributor +
                ", comment='" + comment + '\'' +
                '}';
    }
}
