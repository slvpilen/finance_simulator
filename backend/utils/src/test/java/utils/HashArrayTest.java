package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HashArrayTest {

        HashArray<Integer, Double> hashArray;

        @BeforeEach
        void setup() { hashArray = new HashArray<>(); }

        @Test
        @DisplayName("Put multiple elelemts")
        void putTest() {
                hashArray.put(20240101, 10.0);
                hashArray.put(20240102, 20.0);
                hashArray.put(20240103, 30.0);

                assertEquals(3, hashArray.size(),
                                "Size should be 3");
                assertEquals(0, hashArray.indexOf(20240101));

                hashArray.put(20240101, 100.0);
                assertEquals(3, hashArray.size(),
                                "Size should be 3");
                assertEquals(0, hashArray.indexOf(20240101));
        }

        @Test
        @DisplayName("Removing all elements")
        void clearTest() {
                hashArray.put(20240101, 10.0);
                hashArray.put(20240102, 20.0);
                hashArray.put(20240103, 30.0);

                hashArray.clear();

                assertEquals(0, hashArray.size(),
                                "Size should be 0");
                assertThrows(IndexOutOfBoundsException.class,
                                () -> hashArray.indexOf(
                                                20240101),
                                "Should throw IndexOutOfBoundsException");

        }

        @Test
        @DisplayName("Closest key")
        void closestKeyTest() {
                hashArray.put(20240101, 10.0);
                hashArray.put(20240102, 20.0);
                hashArray.put(20240103, 30.0);

                assertEquals(20240101, hashArray
                                .getClosestKey(20240101));
                assertEquals(20240103, hashArray
                                .getClosestKey(20240104));
                assertEquals(20240103, hashArray
                                .getClosestKey(20240105));
        }

        @Test
        @DisplayName("Closest key different years")
        void closestKeyTest2() {
                hashArray.put(20231231, 10.0);
                hashArray.put(20240102, 20.0);
                hashArray.put(20240103, 30.0);

                assertEquals(20231231, hashArray
                                .getClosestKey(20240101));

        }

        @Test
        @DisplayName("Closest key different years")
        void closestKeyTest3() {
                hashArray.put(20101231, 10.0);
                hashArray.put(20110102, 20.0);
                hashArray.put(20120103, 30.0);
                hashArray.put(20130103, 30.0);
                hashArray.put(20140103, 30.0);
                hashArray.put(20140104, 30.0);
                hashArray.put(20140105, 30.0);
                hashArray.put(20140106, 30.0);
                hashArray.put(20140107, 30.0);
                hashArray.put(20150103, 30.0);
                hashArray.put(20150103, 30.0);
                hashArray.put(20150103, 30.0);
                hashArray.put(20160103, 30.0);

                assertEquals(20140107, hashArray
                                .getClosestKey(20150102));

                assertEquals(20120103, hashArray
                                .getClosestKey(20130101));

        }

        @Test
        @DisplayName("Closest key excact match")
        void closestKeyTest4() {
                hashArray.put(20101231, 10.0);
                hashArray.put(20110102, 20.0);
                hashArray.put(20120103, 30.0);
                hashArray.put(20130103, 30.0);
                hashArray.put(20140103, 30.0);
                hashArray.put(20140104, 30.0);
                hashArray.put(20150103, 30.0);
                hashArray.put(20160103, 30.0);

                assertEquals(20101231, hashArray
                                .getClosestKey(20101231));

                assertEquals(20160103, hashArray
                                .getClosestKey(20160103));

        }
}
