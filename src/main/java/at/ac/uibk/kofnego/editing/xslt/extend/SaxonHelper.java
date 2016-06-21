package at.ac.uibk.kofnego.editing.xslt.extend;

import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 2/29/16.
 */
public class SaxonHelper {

    public static Sequence createListSequenceIterator(List<String> list) throws XPathException {
        if (list==null) list = new ArrayList<>();
        return SequenceTool.toLazySequence(new ListSequenceIterator(list));
    }

    public static class ListSequenceIterator implements SequenceIterator {

        private int counter;
        private final List<String> list;

        private ListSequenceIterator(List<String> list) {
            this.list = list;
            counter = -1;
        }

        @Override
        public Item next() throws XPathException {
            counter++;
            if (counter>=list.size()) return null;
            return new StringValue(list.get(counter));
        }

        @Override
        public void close() {
            // Do nothing
        }

        @Override
        public SequenceIterator getAnother() throws XPathException {
            return new ListSequenceIterator(list);
        }

        @Override
        public int getProperties() {
            return 0;
        }
    }
}
