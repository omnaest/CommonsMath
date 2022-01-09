package org.omnaest.utils.math;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.utils.math.MathUtils.OneFactorDeviationNormalizer;
import org.omnaest.utils.math.domain.DataPoints.Buckets;
import org.omnaest.utils.math.domain.SplittedDataPoints;

public class MathUtilsTest
{

    @Test
    public void testSplitIntoPercentiles() throws Exception
    {
        SplittedDataPoints splittedDataPoints = MathUtils.analyze()
                                                         .data(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0)
                                                         .splitByLowerAndUpperPercentile(80);

        assertArrayEquals(new double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9 }, splittedDataPoints.included()
                                                                                                          .toDoubleArray(),
                          0.001);
        assertArrayEquals(new double[] { 0.0, 1.0 }, splittedDataPoints.excluded()
                                                                       .toDoubleArray(),
                          0.001);
    }

    @Test
    public void testSplitIntoLinearBuckets() throws Exception
    {
        Buckets buckets = MathUtils.analyze()
                                   .data(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2)
                                   .splitIntoLinearBuckets(4);

        assertEquals(4, buckets.stream()
                               .count());
        assertArrayEquals(new double[] { 0.1, 0.2, 0.3 }, buckets.toList()
                                                                 .get(0)
                                                                 .toDoubleArray(),
                          0.001);
        assertArrayEquals(new double[] { 0.4, 0.5, 0.6 }, buckets.toList()
                                                                 .get(1)
                                                                 .toDoubleArray(),
                          0.001);
        assertArrayEquals(new double[] { 0.7, 0.8, 0.9 }, buckets.toList()
                                                                 .get(2)
                                                                 .toDoubleArray(),
                          0.001);
        assertArrayEquals(new double[] { 1.0, 1.1, 1.2 }, buckets.toList()
                                                                 .get(3)
                                                                 .toDoubleArray(),
                          0.001);

    }

    @Test
    public void testNormalize() throws Exception
    {
        OneFactorDeviationNormalizer normalizer = MathUtils.normalize()
                                                           .byOneFactorDeviation()
                                                           .acceptAll(1.2, 2.0, 0.5)
                                                           .withDeviationLimit(10.0);
        assertEquals(10.0, normalizer.applyAsDouble(2.0), 0.01);
        assertEquals(1.0, normalizer.applyAsDouble(1.0), 0.01);
        assertEquals(0.1, normalizer.applyAsDouble(0.5), 0.01);
    }

}
