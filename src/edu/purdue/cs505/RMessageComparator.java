package edu.purdue.cs505;

import java.util.Comparator;

/** Implementation of the Comparator interface. This is used for inserting
 * messages into a PriorityQueue which is sorted on timeout values. */
public class RMessageComparator implements Comparator<RMessage> {
    @Override
    public int compare(RMessage m1, RMessage m2) {
        if (m1 == null && m2 == null) return 0;
        if (m1 == null || m2 == null) return m1 == null ? 1 : -1;

        return (int)(m1.getTimeout() - m2.getTimeout());
    }
}