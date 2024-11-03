package jdbcdrivers.generic;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import jdbcdrivers.BaseTest;

public final class CachedValuesTest extends BaseTest {

    private final CachedValues cachedValues = CachedValues.getInstance();

    private static final int MIN_INTEGER = Short.MIN_VALUE;
    private static final int MAX_INTEGER = Short.MAX_VALUE;

    private static final int MAX_CHAR = 255;

    @Test
    @Category(UnitTests.class)
    public void testByte() {

        for (byte b = Byte.MIN_VALUE; b <= Byte.MAX_VALUE; ++ b) {

            final Byte boxedByte = cachedValues.getByte(b);

            assertThat(boxedByte.byteValue()).isEqualTo(b);
            assertThat(boxedByte).isSameAs(cachedValues.getByte(b));

            if (b == Byte.MAX_VALUE) {

                break;
            }
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testShort() {

        for (short s = Short.MIN_VALUE; s <= Short.MAX_VALUE; ++ s) {

            final Short boxedShort = cachedValues.getShort(s);

            assertThat(boxedShort.shortValue()).isEqualTo(s);

            if (s >= 0) {

                assertThat(boxedShort).isSameAs(cachedValues.getShort(s));
            }
            else if (s < -128) {

                assertThat(boxedShort).isNotSameAs(cachedValues.getShort(s));
            }

            if (s == Short.MAX_VALUE) {

                break;
            }
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testInt() {

        for (int i = MIN_INTEGER; i <= MAX_INTEGER; ++ i) {

            final Integer boxedInteger = cachedValues.getInt(i);

            assertThat(boxedInteger.intValue()).isEqualTo(i);

            if (i >= 0 && i <= CachedValues.MAX_CACHED_INTEGER) {

                assertThat(boxedInteger).isSameAs(cachedValues.getInt(i));
            }
            else if (i < -128) {

                assertThat(boxedInteger).isNotSameAs(cachedValues.getInt(i));
            }
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testLong() {

        for (long l = MIN_INTEGER; l <= MAX_INTEGER; ++ l) {

            final Long boxedLong = cachedValues.getLong(l);

            assertThat(boxedLong.longValue()).isEqualTo(l);

            if (l >= -128 && l <= 127) {

                assertThat(boxedLong).isSameAs(cachedValues.getLong(l));
            }
            else {

                assertThat(boxedLong).isNotSameAs(cachedValues.getLong(l));
            }
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testFloat() {

        for (float f = MIN_INTEGER; f <= MAX_INTEGER; ++ f) {

            final Float boxedFloat = cachedValues.getFloat(f);

            assertThat(boxedFloat.floatValue()).isEqualTo(f);
            assertThat(boxedFloat).isNotSameAs(cachedValues.getFloat(f));
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testDouble() {

        for (double d = MIN_INTEGER; d <= MAX_INTEGER; ++ d) {

            final Double boxedDouble = cachedValues.getDouble(d);

            assertThat(boxedDouble.doubleValue()).isEqualTo(d);
            assertThat(boxedDouble).isNotSameAs(cachedValues.getDouble(d));
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testDecimal() {

        for (long l = MIN_INTEGER; l <= MAX_INTEGER; ++ l) {

            final BigDecimal bigDecimal = cachedValues.getBigDecimal(l, 0);

            assertThat(bigDecimal.longValueExact()).isEqualTo(l);

            if (l >= 0 && l <= 10) {

                assertThat(bigDecimal).isSameAs(cachedValues.getBigDecimal(l, 0));
            }
            else {
                assertThat(bigDecimal).isNotSameAs(cachedValues.getBigDecimal(l, 0));
            }
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testString() {

        final char[] oneChar = new char[1];

        for (char c = 0; c <= Short.MAX_VALUE; ++ c) {

            oneChar[0] = c;

            final String string = cachedValues.getString(oneChar);

            assertThat(string).isEqualTo(String.valueOf(oneChar));

            if (c <= CachedValues.CACHE_ONE_CHAR_MAX) {

                assertThat(string).isSameAs(cachedValues.getString(oneChar));
            }
            else {
                assertThat(string).isNotSameAs(cachedValues.getString(oneChar));
            }
        }

        final char[] twoChars = new char[2];

        for (char c1 = 0; c1 <= MAX_CHAR; ++ c1) {

            for (char c2 = 0; c2 <= MAX_CHAR; ++ c2) {

                twoChars[0] = c1;
                twoChars[1] = c2;

                final String string = cachedValues.getString(twoChars);

                assertThat(string).isEqualTo(String.valueOf(twoChars));

                if (c1 <= CachedValues.CACHE_TWO_CHARS_MAX && c2 <= CachedValues.CACHE_TWO_CHARS_MAX) {

                    assertThat(string).isSameAs(cachedValues.getString(twoChars));
                }
                else {
                    assertThat(string).isNotSameAs(cachedValues.getString(twoChars));
                }
            }
        }

        final char[] threeChars = new char[3];

        for (char c1 = 0; c1 <= MAX_CHAR; ++ c1) {

            for (char c2 = 0; c2 <= MAX_CHAR; ++ c2) {

                for (char c3 = 0; c3 <= MAX_CHAR; ++ c3) {

                    threeChars[0] = c1;
                    threeChars[1] = c2;
                    threeChars[2] = c3;

                    final String string = cachedValues.getString(threeChars);

                    assertThat(string).isEqualTo(String.valueOf(threeChars));

                    if (cachedValues.isCachedThreeCharacter(c1) && cachedValues.isCachedThreeCharacter(c2) && cachedValues.isCachedThreeCharacter(c3)) {

                        assertThat(string).isSameAs(cachedValues.getString(threeChars));
                    }
                    else {
                        assertThat(string).isNotSameAs(cachedValues.getString(threeChars));
                    }
                }
            }
        }
    }
}
