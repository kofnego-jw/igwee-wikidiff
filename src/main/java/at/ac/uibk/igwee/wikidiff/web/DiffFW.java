package at.ac.uibk.igwee.wikidiff.web;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * DataValueType for Diff Results
 *
 * Created by Joseph on 03.06.2016.
 */
public class DiffFW {

    /**
     * The first text
     */
    public String text1;

    /**
     * The second text
     */
    public String text2;

    /**
     * The diff. Using &lt;ins&gt; and &lt;del&gt; to encode the differences
     */
    public String diff;

    public DiffFW() {
    }

    public DiffFW(String text1, String text2, String diff) {
        this.text1 = text1;
        this.text2 = text2;
        this.diff = diff;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public void recode() {
        text1 = StringEscapeUtils.escapeXml10(text1);
        text2 = StringEscapeUtils.escapeXml10(text2);
        diff = StringEscapeUtils.escapeXml10(diff);
        diff = recode(diff);
    }

    private static String recode(String s) {
        return s.replace("&lt;uibk:ins&gt;", "<ins>")
                .replace("&lt;/uibk:ins&gt;", "</ins>")
                .replace("&lt;uibk:del&gt;", "<del>")
                .replace("&lt;/uibk:del&gt;", "</del>");
    }
}
