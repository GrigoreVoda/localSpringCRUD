package com.grigore.mongo.service;

import com.grigore.mongo.model.Gender;

import java.util.Locale;
import java.util.Map;

/**
 * Resolves the inverse of a relative-type label so relations can be kept in sync
 * on both sides, e.g. if A's relative type for B is "father", B's relative type
 * for A should become "son" or "daughter" depending on A's gender. Labels that
 * aren't recognized (free text) are treated as symmetric: the same label is used
 * on both sides.
 */
public final class RelativeTypeResolver {

    private enum Category { PARENT, CHILD, SIBLING, SPOUSE }

    private static final Map<String, Category> CATEGORIES = Map.ofEntries(
            Map.entry("father", Category.PARENT), Map.entry("mother", Category.PARENT), Map.entry("parent", Category.PARENT),
            Map.entry("son", Category.CHILD), Map.entry("daughter", Category.CHILD), Map.entry("child", Category.CHILD),
            Map.entry("brother", Category.SIBLING), Map.entry("sister", Category.SIBLING), Map.entry("sibling", Category.SIBLING),
            Map.entry("husband", Category.SPOUSE), Map.entry("wife", Category.SPOUSE), Map.entry("spouse", Category.SPOUSE)
    );

    private static final Map<Category, Category> INVERSE_CATEGORY = Map.of(
            Category.PARENT, Category.CHILD,
            Category.CHILD, Category.PARENT,
            Category.SIBLING, Category.SIBLING,
            Category.SPOUSE, Category.SPOUSE
    );

    private RelativeTypeResolver() {
    }

    public static String inverseOf(String relativeType, Gender ownerGender) {
        if (relativeType == null) {
            return null;
        }
        Category category = CATEGORIES.get(relativeType.toLowerCase(Locale.ROOT));
        if (category == null) {
            return relativeType;
        }
        return label(INVERSE_CATEGORY.get(category), ownerGender);
    }

    private static String label(Category category, Gender gender) {
        boolean male = gender == Gender.MALE;
        boolean female = gender == Gender.FEMALE;
        return switch (category) {
            case PARENT -> male ? "father" : female ? "mother" : "parent";
            case CHILD -> male ? "son" : female ? "daughter" : "child";
            case SIBLING -> male ? "brother" : female ? "sister" : "sibling";
            case SPOUSE -> male ? "husband" : female ? "wife" : "spouse";
        };
    }
}
