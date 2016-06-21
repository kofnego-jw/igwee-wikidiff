package at.ac.uibk.igwee.wikidiff.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DataValueType for RevisionList
 *
 * contains information on revisions and on contributors.
 *
 * Created by Joseph on 03.06.2016.
 */
public class RevisionFWList {

    public List<RevisionFW> revisionList;

    public List<ContributorFW> contributorList;

    public RevisionFWList() {
    }

    public RevisionFWList(List<RevisionFW> revisionList, List<ContributorFW> contributorList) {
        this.revisionList = revisionList;
        this.contributorList = contributorList;
    }

    public List<RevisionFW> getRevisionList() {
        return revisionList;
    }

    public void setRevisionList(List<RevisionFW> revisionList) {
        this.revisionList = revisionList;
    }

    public List<ContributorFW> getContributorList() {
        return contributorList;
    }

    public void setContributorList(List<ContributorFW> contributorList) {
        this.contributorList = contributorList;
    }

    @Override
    public String toString() {
        return "RevisionFWList{" +
                "revisionList=" + revisionList +
                ", contributorList=" + contributorList +
                '}';
    }

    public void calculateContributors() {
        if (revisionList==null || revisionList.isEmpty()) {
            contributorList = Collections.emptyList();
            return;
        }
        Map<Contributor, List<RevisionFW>> collect = revisionList.stream()
                .collect(Collectors.groupingBy(rev -> rev.getContributor()));
        List<ContributorFW> contributes = collect.entrySet()
                .stream()
                .map(entry -> new ContributorFW(entry.getKey().username, entry.getKey().id, entry.getValue().size(), entry.getValue().get(0).timestamp))
                .sorted(ContributorFWComparator.DEFAULT_COMPARATOR)
                .collect(Collectors.toList());
        this.contributorList = contributes;
    }
}
