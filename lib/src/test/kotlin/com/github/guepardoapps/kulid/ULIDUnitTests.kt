/**
 * MIT License
 *
 * Copyright (c) 2019-2021 GuepardoApps (Jonas Schubert)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.guepardoapps.kulid

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.experimental.and
import kotlin.random.Random

/**
 * Test class for {@link com.github.guepardoapps.kulid}
 * @author                   GuepardoApps (Jonas Schubert)
 * @since                    1.0.0.0 (06.01.2019)
 */
class ULIDUnitTests {
    private val zeroEntropy = byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0)

    private class TestParam(val timestamp: Long, val entropy: ByteArray?, val value: String, val isIllegalArgument: Boolean) {
        val reproducer: String

        init {
            val stringBuilder = StringBuilder()
            stringBuilder.append("ULID.generate(").append(timestamp.toString()).append("L,")
            if (entropy != null) {
                stringBuilder.append("new byte[]{")
                for (index in entropy.indices) {
                    stringBuilder.append("0x").append(Integer.toHexString((entropy[index] and 0xFF.toByte()) + 0x100).substring(1))
                    if (index + 1 < entropy.size) {
                        stringBuilder.append(",")
                    }
                }
                stringBuilder.append("}")
            } else {
                stringBuilder.append("null")
            }
            stringBuilder.append(")")
            this.reproducer = stringBuilder.toString()
        }
    }

    private val testParameters = arrayOf(
            TestParam(ULID.TIMESTAMP_MIN, zeroEntropy, "00000000000000000000000000", false),
            TestParam(ULID.TIMESTAMP_MAX, zeroEntropy, "7ZZZZZZZZZ0000000000000000", false),
            TestParam(0x00000001L, zeroEntropy, "00000000010000000000000000", false),
            TestParam(0x0000000fL, zeroEntropy, "000000000F0000000000000000", false),
            TestParam(0x00000010L, zeroEntropy, "000000000G0000000000000000", false),
            TestParam(0x00000011L, zeroEntropy, "000000000H0000000000000000", false),
            TestParam(0x0000001fL, zeroEntropy, "000000000Z0000000000000000", false),
            TestParam(0x00000020L, zeroEntropy, "00000000100000000000000000", false),
            TestParam(0x00000021L, zeroEntropy, "00000000110000000000000000", false),
            TestParam(0x0000002fL, zeroEntropy, "000000001F0000000000000000", false),
            TestParam(0x00000030L, zeroEntropy, "000000001G0000000000000000", false),
            TestParam(0x00000031L, zeroEntropy, "000000001H0000000000000000", false),
            TestParam(0x0000003fL, zeroEntropy, "000000001Z0000000000000000", false),
            TestParam(0x00000040L, zeroEntropy, "00000000200000000000000000", false),
            TestParam(0x000000f0L, zeroEntropy, "000000007G0000000000000000", false),
            TestParam(0x000000ffL, zeroEntropy, "000000007Z0000000000000000", false),
            TestParam(0x00000100L, zeroEntropy, "00000000800000000000000000", false),
            TestParam(0x00000101L, zeroEntropy, "00000000810000000000000000", false),
            TestParam(0x000001ffL, zeroEntropy, "00000000FZ0000000000000000", false),
            TestParam(0x00000200L, zeroEntropy, "00000000G00000000000000000", false),
            TestParam(0x00000201L, zeroEntropy, "00000000G10000000000000000", false),
            TestParam(0x000002ffL, zeroEntropy, "00000000QZ0000000000000000", false),
            TestParam(0x00000300L, zeroEntropy, "00000000R00000000000000000", false),
            TestParam(0x00000301L, zeroEntropy, "00000000R10000000000000000", false),
            TestParam(0x000003ffL, zeroEntropy, "00000000ZZ0000000000000000", false),
            TestParam(0x00000400L, zeroEntropy, "00000001000000000000000000", false),
            TestParam(0x00000401L, zeroEntropy, "00000001010000000000000000", false),
            TestParam(0x000007ffL, zeroEntropy, "00000001ZZ0000000000000000", false),
            TestParam(0x00000800L, zeroEntropy, "00000002000000000000000000", false),
            TestParam(0x00007fffL, zeroEntropy, "0000000ZZZ0000000000000000", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x01), "00000000000000000000000001", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0f), "0000000000000000000000000F", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10), "0000000000000000000000000G", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1f), "0000000000000000000000000Z", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x20), "00000000000000000000000010", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x21), "00000000000000000000000011", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x2f), "0000000000000000000000001F", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x30), "0000000000000000000000001G", false),
            TestParam(ULID.TIMESTAMP_MIN, byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3f), "0000000000000000000000001Z", false))

    @Test
    fun testRandom() {
        val value = ULID.random()
        assertNotNull(value, "Generated ULID must not be null")
        assertEquals(26, value.length, "Generated ULID length must be 26")
        assertTrue(value.matches("[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}".toRegex()), "Generated ULID characters must only include [0123456789ABCDEFGHJKMNPQRSTVWXYZ]")
    }

    @Test
    fun testGenerateRandom() {
        val value = ULID.generate(System.currentTimeMillis(), Random.nextBytes(10))
        assertNotNull(value, "Generated ULID must not be null")
        assertEquals(26, value.length, "Generated ULID length must be 26, but returned " + value.length + " instead")
        assertTrue(value.matches(("[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}").toRegex()), "Generated ULID characters must only include [0123456789ABCDEFGHJKMNPQRSTVWXYZ], but returned $value instead")
    }

    @Test
    fun testFromString() {
        val value = ULID.fromString("003JZ9J6G80123456789ABCDEF")
        assertNotNull(value, "Generated ULID must not be null")
        assertEquals(26, value.length, "Generated ULID length must be 26")
        assertTrue(value.matches("[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}".toRegex()), "Generated ULID characters must only include [0123456789ABCDEFGHJKMNPQRSTVWXYZ]")
    }

    @Test
    fun testGenerateFixedValues() {
        for (params in testParameters) {
            var hasIllegalArgumentException = false
            try {
                val value = ULID.generate(params.timestamp, params.entropy)
                assertEquals(params.value, value, ("Generated ULID must be equal to \"" + params.value + "\" for " + params.reproducer + " , but returned \"" + value + "\" instead"))
                assertNotNull(value, "Generated ULID must not be null")
                assertEquals(26, value.length, "Generated ULID length must be 26, but returned " + value.length + " instead")
                assertTrue(value.matches(("[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}").toRegex()), ("Generated ULID characters must only include [0123456789ABCDEFGHJKMNPQRSTVWXYZ], but returned $value instead"))
            } catch (e: IllegalArgumentException) {
                hasIllegalArgumentException = true
            }

            if (params.isIllegalArgument) {
                assertTrue(hasIllegalArgumentException, "IllegalArgumentException is expected for " + params.reproducer)
            } else {
                assertFalse(hasIllegalArgumentException, "IllegalArgumentException is not expected for " + params.reproducer)
            }
        }
    }

    @Test
    fun testIsValidNegative() {
        val invalidUlids = arrayOf(
                null,
                "",
                "0",
                "000000000000000000000000000",
                "-0000000000000000000000000",
                "0000000000000000000000000U",
                "0000000000000000000000000/u3042",
                "0000000000000000000000000#")

        for (ulid in invalidUlids) {
            assertFalse(ULID.isValid(ulid), "ULID \"$ulid\" should be invalid")
        }
    }

    @Test
    fun testIsValidFixedValues() {
        for (params in testParameters) {
            if (!params.isIllegalArgument) {
                assertTrue(ULID.isValid(params.value), "ULID string is valid")
            }
        }
    }

    @Test
    fun testGetTimestampFixedValues() {
        for (params in testParameters) {
            if (!params.isIllegalArgument) {
                assertEquals(params.timestamp, ULID.getTimestamp(params.value), "ULID timestamp is different")
            }
        }
    }

    @Test
    fun testGetEntropyFixedValues() {
        for (params in testParameters) {
            if (!params.isIllegalArgument) {
                assertArrayEquals(params.entropy, ULID.getEntropy(params.value), "ULID entropy is different")
            }
        }
    }

    @Test
    fun testUlidWithZEntropy() {
        val testUlid = "01EA9F3000ZZZZZZZZZZZZZZZZ"

        // Extract timestamp and entropy from the ULID
        val timestamp = ULID.getTimestamp(testUlid)
        val entropy = ULID.getEntropy(testUlid)

        // Regenerate ULID using the extracted timestamp and entropy
        val regeneratedUlid = ULID.generate(timestamp, entropy)

        // Verify that the regenerated ULID matches the original
        assertEquals(testUlid, regeneratedUlid, "Regenerated ULID should match the original")
    }

    @Test
    fun testFromStringInvalid() {
        val invalidUlids = arrayOf(
            "",
            "0",
            "000000000000000000000000000",
            "-0000000000000000000000000",
            "0000000000000000000000000U",
            "0000000000000000000000000/u3042",
            "0000000000000000000000000#")

        for (ulid in invalidUlids) {
            assertThrows(IllegalArgumentException::class.java, { ULID.fromString(ulid) },
                "IllegalArgumentException should be thrown for invalid ULID \"$ulid\"")
        }
    }

    @Test
    fun testGenerateInvalidInputs() {
        // Test timestamp less than TIMESTAMP_MIN
        assertThrows(IllegalArgumentException::class.java, { ULID.generate(ULID.TIMESTAMP_MIN - 1, zeroEntropy) },
            "IllegalArgumentException should be thrown for timestamp < TIMESTAMP_MIN")

        // Test timestamp greater than TIMESTAMP_MAX
        assertThrows(IllegalArgumentException::class.java, { ULID.generate(ULID.TIMESTAMP_MAX + 1, zeroEntropy) },
            "IllegalArgumentException should be thrown for timestamp > TIMESTAMP_MAX")

        // Test null entropy
        assertThrows(IllegalArgumentException::class.java, { ULID.generate(ULID.TIMESTAMP_MIN, null) },
            "IllegalArgumentException should be thrown for null entropy")

        // Test entropy with less than 10 bytes
        val shortEntropy = byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0)
        assertThrows(IllegalArgumentException::class.java, { ULID.generate(ULID.TIMESTAMP_MIN, shortEntropy) },
            "IllegalArgumentException should be thrown for entropy with less than 10 bytes")
    }

    @Test
    fun testMonotonicSorting() {
        // Generate ULIDs with increasing timestamps
        val timestamp1 = 1000L
        val timestamp2 = 1001L
        val timestamp3 = 1002L

        val entropy = byteArrayOf(0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0)

        val ulid1 = ULID.generate(timestamp1, entropy)
        val ulid2 = ULID.generate(timestamp2, entropy)
        val ulid3 = ULID.generate(timestamp3, entropy)

        // Verify that ULIDs are lexicographically sortable
        assertTrue(ulid1 < ulid2, "ULID with earlier timestamp should be lexicographically less than ULID with later timestamp")
        assertTrue(ulid2 < ulid3, "ULID with earlier timestamp should be lexicographically less than ULID with later timestamp")

        // Verify that sorting ULIDs is equivalent to sorting by timestamp
        val ulids = listOf(ulid3, ulid1, ulid2)
        val sortedUlids = ulids.sorted()

        assertEquals(listOf(ulid1, ulid2, ulid3), sortedUlids, "ULIDs should sort in timestamp order")
    }
}
