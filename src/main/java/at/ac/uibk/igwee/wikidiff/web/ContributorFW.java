package at.ac.uibk.igwee.wikidiff.web;

/**
 * DataValueType for Contributor, having more information
 *
 * Created by joseph on 6/13/16.
 */
public class ContributorFW {

    public String username;
    public String id;
    public int numberOfRevisions;
    public String firstRevisionTimestamp;

    public ContributorFW() {
    }

    public ContributorFW(String username, String id, int numberOfRevisions, String firstRevisionTimestamp) {
        this.username = username;
        this.id = id;
        this.numberOfRevisions = numberOfRevisions;
        this.firstRevisionTimestamp = firstRevisionTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumberOfRevisions() {
        return numberOfRevisions;
    }

    public void setNumberOfRevisions(int numberOfRevisions) {
        this.numberOfRevisions = numberOfRevisions;
    }

    public String getFirstRevisionTimestamp() {
        return firstRevisionTimestamp;
    }

    public void setFirstRevisionTimestamp(String firstRevisionTimestamp) {
        this.firstRevisionTimestamp = firstRevisionTimestamp;
    }

    @Override
    public String toString() {
        return "ContributorFW{" +
                "username='" + username + '\'' +
                ", id='" + id + '\'' +
                ", numberOfRevisions=" + numberOfRevisions +
                ", firstRevisionTimestamp='" + firstRevisionTimestamp + '\'' +
                '}';
    }
}
