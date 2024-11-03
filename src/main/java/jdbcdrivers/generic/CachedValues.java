package jdbcdrivers.generic;

import java.math.BigDecimal;

public final class CachedValues {

    static final int MAX_CACHED_INTEGER = 100000;

    private static final char ASCII_MAX = 127;

    static final char CACHE_ONE_CHAR_MAX = ASCII_MAX;
    static final char CACHE_TWO_CHARS_MAX = ASCII_MAX;
    private static final char CACHE_THREE_CHARS_MAX = ASCII_MAX;

    private static final int TWO_CHARACTERS_SHIFT = 7;

    private static final int THREE_CHARACTERS_SHIFT = 6;
    private static final int THREE_CHARACTERS_SHIFT_TWICE = THREE_CHARACTERS_SHIFT * 2;

    private static final String EMPTY_STRING = "";

    private static final CachedValues instance = new CachedValues();

    public static CachedValues getInstance() {

        return instance;
    }

    private final Short[] cachedShorts;
    private final Integer[] cachedIntegers;

    private final String[] cachedLengthOneStrings;
    private final String[] cachedLengthTwoStrings;
    private final String[] cachedLengthThreeStrings;

    private final char[] cachedThreeCharacters;
    private final int[] threeCharacterIndices;

    private CachedValues() {

        final short maxCachedShort = Short.MAX_VALUE;

        this.cachedShorts = new Short[maxCachedShort + 1];

        for (int i = 0; i <= maxCachedShort; ++ i) {

            if (i > Short.MAX_VALUE) {

                throw new IllegalStateException();
            }

            cachedShorts[i] = Short.valueOf((short)i);
        }

        final int maxCachedInteger = MAX_CACHED_INTEGER;

        this.cachedIntegers = new Integer[maxCachedInteger + 1];

        for (int i = 0; i <= maxCachedInteger; ++ i) {

            cachedIntegers[i] = Integer.valueOf(i);
        }

        this.cachedLengthOneStrings = new String[CACHE_ONE_CHAR_MAX + 1];
        this.cachedLengthTwoStrings = new String[(CACHE_TWO_CHARS_MAX + 1) * (CACHE_TWO_CHARS_MAX + 1)];

        final char[] twoChars = new char[2];

        for (char c1 = 0; c1 <= CACHE_TWO_CHARS_MAX; ++ c1) {

            cachedLengthOneStrings[c1] = String.valueOf(c1);

            for (char c2 = 0; c2 <= CACHE_TWO_CHARS_MAX; ++ c2) {

                twoChars[0] = c1;
                twoChars[1] = c2;

                cachedLengthTwoStrings[twoCharactersIndex(c1, c2)] = String.valueOf(twoChars);
            }
        }

        final int numCachedThreeCharacters = 1 << 6;

        this.cachedThreeCharacters = new char[numCachedThreeCharacters];

        int dstIndex = 0;

        for (char c = '0'; c <= '9'; ++ c) {

            cachedThreeCharacters[dstIndex ++] = c;
        }

        for (char c = 'A'; c <= 'Z'; ++ c) {

            cachedThreeCharacters[dstIndex ++] = c;
        }

        for (char c = 'a'; c <= 'z'; ++ c) {

            cachedThreeCharacters[dstIndex ++] = c;
        }

        cachedThreeCharacters[dstIndex ++] = ' ';
        cachedThreeCharacters[dstIndex ++] = '-';

        if (dstIndex != numCachedThreeCharacters) {

            throw new IllegalStateException();
        }

        this.threeCharacterIndices = new int[CACHE_THREE_CHARS_MAX];

        int threeCharaterIndex = 0;

        for (char c : cachedThreeCharacters) {

            threeCharacterIndices[c] = threeCharaterIndex ++;
        }

        this.cachedLengthThreeStrings = new String[numCachedThreeCharacters * numCachedThreeCharacters * numCachedThreeCharacters];

        final char[] threeChars = new char[3];

        for (int i = 0; i < numCachedThreeCharacters; ++ i) {

            for (int j = 0; j < numCachedThreeCharacters; ++ j) {

                for (int k = 0; k < numCachedThreeCharacters; ++ k) {

                    final char c1 = cachedThreeCharacters[i];
                    final char c2 = cachedThreeCharacters[j];
                    final char c3 = cachedThreeCharacters[k];

                    threeChars[0] = c1;
                    threeChars[1] = c2;
                    threeChars[2] = c3;

                    final int index = threeCharactersIndex(c1, c2, c3);

                    cachedLengthThreeStrings[index] = String.valueOf(threeChars);
                }
            }
        }
    }

    public Byte getByte(byte value) {

        return Byte.valueOf(value);
    }

    public Short getShort(short value) {

        return value >= 0 ? cachedShorts[value] : Short.valueOf(value);
    }

    public Integer getInt(int value) {

        return value >= 0 && value <= MAX_CACHED_INTEGER ? cachedIntegers[value] : Integer.valueOf(value);
    }

    public Long getLong(long value) {

        return Long.valueOf(value);
    }

    public Float getFloat(float value) {

        return Float.valueOf(value);
    }

    public Double getDouble(double value) {

        return Double.valueOf(value);
    }

    public BigDecimal getBigDecimal(long unscaledValue, int scale) {

        return BigDecimal.valueOf(unscaledValue, scale);
    }

    public String getString(char[] value) {

        return getString(value, value.length);
    }

    public String getString(char[] value, int length) {

        final String result;

        switch (length) {

        case 0:

            result = EMPTY_STRING;
            break;

        case 1:

            final char c = value[0];

            result = c <= CACHE_ONE_CHAR_MAX ? cachedLengthOneStrings[c] : String.valueOf(c);
            break;

        case 2: {

            final char c1 = value[0];
            final char c2 = value[1];

            result = c1 <= CACHE_TWO_CHARS_MAX && c2 <= CACHE_TWO_CHARS_MAX ? cachedLengthTwoStrings[twoCharactersIndex(c1, c2)] : String.valueOf(value);
            break;
        }

        case 3:

            final char c1 = value[0];
            final char c2 = value[1];
            final char c3 = value[2];

            result = isCachedThreeCharacter(c1) && isCachedThreeCharacter(c2) && isCachedThreeCharacter(c3)
                    ? cachedLengthThreeStrings[threeCharactersIndex(c1, c2, c3)]
                    : String.valueOf(value);
            break;

        default:
            result = String.valueOf(value, 0, length);
            break;
        }

        return result;
    }

    boolean isCachedThreeCharacter(char c) {

        boolean found = false;

        for (char cahedThreeCharacter : cachedThreeCharacters) {

            if (cahedThreeCharacter == c) {

                found = true;
                break;
            }
        }

        return found;
    }

    private static int twoCharactersIndex(char c1, char c2) {

        if (c1 > CACHE_TWO_CHARS_MAX) {

            throw new IllegalArgumentException();
        }

        if (c2 > CACHE_TWO_CHARS_MAX) {

            throw new IllegalArgumentException();
        }

        return (c1 << TWO_CHARACTERS_SHIFT) | c2;
    }

    private int threeCharactersIndex(char c1, char c2, char c3) {

        if (c1 > CACHE_THREE_CHARS_MAX) {

            throw new IllegalArgumentException();
        }

        if (c2 > CACHE_THREE_CHARS_MAX) {

            throw new IllegalArgumentException();
        }

        if (c3 > CACHE_THREE_CHARS_MAX) {

            throw new IllegalArgumentException();
        }

        return (threeCharacterIndices[c1] << THREE_CHARACTERS_SHIFT_TWICE) | (threeCharacterIndices[c2] << THREE_CHARACTERS_SHIFT) | threeCharacterIndices[c3];
    }
}
