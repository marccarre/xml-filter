package com.carmatechnologies.utilities.xml.common;

public final class MutablePair<First, Second> implements Pair<First, Second> {
    private First first;
    private Second second;

    private MutablePair(final First first, final Second second) {
        first(first);
        second(second);
    }

    @Override
    public First first() {
        return first;
    }

    public void first(final First first) {
        this.first = first;
    }

    @Override
    public Second second() {
        return second;
    }

    public void second(final Second second) {
        this.second = second;
    }

    /**
     * Partially initializes the pair with the provided {@code first} element.
     * The {@code second} element will remain {@code null}, until modified using {@link MutablePair#second(Second) second}
     *
     * @param first value used to initialize {@code first}
     * @return partially initialized pair.
     */
    public static <First, Second> MutablePair<First, Second> withFirst(final First first) {
        return new MutablePair(first, null);
    }

    /**
     * Partially initializes the pair with the provided {@code second} element.
     * The {@code first} element will remain {@code null}, until modified using {@link MutablePair#first(First) first}
     *
     * @param second value used to initialize {@code second}
     * @return partially initialized pair.
     */
    public static <First, Second> MutablePair<First, Second> withSecond(final Second second) {
        return new MutablePair(null, second);
    }

    public static <First, Second> MutablePair<First, Second> of(final First first, final Second second) {
        return new MutablePair(first, second);
    }
}
