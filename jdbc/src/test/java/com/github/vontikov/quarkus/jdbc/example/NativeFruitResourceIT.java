package com.github.vontikov.quarkus.jdbc.example;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeFruitResourceIT extends FruitResourceTest {
    // Execute the same tests but in native mode.
}