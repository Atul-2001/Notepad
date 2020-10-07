package org.signature.util;

import org.signature.model.Position;

import java.util.*;
import java.util.regex.Matcher;

public final class MatcherResultList {

    public static List<Position> getList(Matcher matcher) {
        Objects.requireNonNull(matcher);
        List<Position> positionList = new ArrayList<>();

        while (matcher.find()) {
            positionList.add(new Position(matcher.start(), matcher.end()));
        }

        return positionList;
    }
}

