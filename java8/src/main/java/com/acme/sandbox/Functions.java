package com.acme.sandbox;

import java.math.BigInteger;

/** Repository of stray functions. */
public final class Functions {

  public static BigInteger factorial(long n) {
    return factorial(BigInteger.valueOf(n));
  }

  public static BigInteger factorial(BigInteger n) {
    return n.compareTo(BigInteger.ONE) >= 0
        ? n.multiply(factorial(n.subtract(BigInteger.ONE)))
        : BigInteger.ONE;
  }

  public static long fibonacci(long n) {
    if (n < 1) {
      return 0;
    }
    return n < 3 ? 1 : fibonacci(n - 1) + fibonacci(n - 2);
  }

  private Functions() {}
}
