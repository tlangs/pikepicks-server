package org.langsford.pokepics.enumeration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trevyn on 6/28/15.
 */
public enum Region {

    KANTO(1, 151), JOHTO(152, 251), HOENN(252, 386), SINNOH(387, 493), UNOVA(494, 649), KALOS(650, 718);

    private List<Integer> ids;

    Region(Integer startId, Integer endId) {
        List<Integer> ids = new ArrayList<>();
        for (int i = startId; i <= endId; i++) {
            ids.add(i);
        }
        this.ids = ids;
    }

    public List<Integer> getIds() {
        return ids;
    }
}
