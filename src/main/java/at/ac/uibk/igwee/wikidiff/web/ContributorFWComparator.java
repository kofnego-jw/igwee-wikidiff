package at.ac.uibk.igwee.wikidiff.web;

import java.util.Comparator;

/**
 * Comparator for Controbutors. Use the static method to refer to the only implementation.
 *
 * Compares the contributor according to the number of revisions. More revisions = enlist first,
 *
 * then compare according to username.
 *
 * Created by joseph on 6/13/16.
 */
public class ContributorFWComparator implements Comparator<ContributorFW> {

    private ContributorFWComparator() {

    }

    @Override
    public int compare(ContributorFW o1, ContributorFW o2) {
        if (o2==null) return 1;
        if (o1==null) return -1;
        return o1.numberOfRevisions != o2.numberOfRevisions ?
                o2.numberOfRevisions - o1.numberOfRevisions :
                o1.username.compareTo(o2.username);
    }

    /**
     * Get an instance
     */
    public static ContributorFWComparator DEFAULT_COMPARATOR = new ContributorFWComparator();
}
