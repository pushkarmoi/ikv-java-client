package io.inlined.benchmarks;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class KVGenerator {
  private final ArrayList<byte[]> _keys;

  public KVGenerator(int samples) {
    _keys = new ArrayList<>(samples);
    for (int i = 0; i < samples; i++) {
      // {} help specify partitioning key for redis.
      byte[] key = String.format("KEY-{%d}", i).getBytes(StandardCharsets.UTF_8);
      _keys.add(key);
    }
  }

  public byte[] key(int sample) {
    return _keys.get(sample);
  }

  // Should be used to create values
  // Ex. seed = sample * field-id
  public static byte[] createPseudoRandomBytes(int length, int seed) {
    byte[] result = new byte[length];
    Arrays.fill(result, (byte) seed);
    return result;
  }
}
