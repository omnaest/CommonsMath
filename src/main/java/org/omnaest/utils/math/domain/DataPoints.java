package org.omnaest.utils.math.domain;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.omnaest.utils.math.internal.DataPointsImpl;

public interface DataPoints extends Iterable<DataPoint>
{
    public Stream<DataPoint> stream();

    public double calculatePercentile(double percentile);

    public double calculateAverage();

    /**
     * Splits the {@link DataPoints} into below,middle and above sets, where the below and above constitutes the excluded and the middle the includes
     * {@link DataPoints}. The lower barrier is the percentile of the percentile sum / 2 and the upper the (100-percentileSum/2) percentile.
     * 
     * @param percentileSum
     * @return
     */
    public SplittedDataPoints splitByLowerAndUpperPercentile(double percentileSum);

    public Buckets splitIntoLinearBuckets(int numberOfBuckets);

    public double[] toDoubleArray();

    public static DataPoints of(Collection<DataPoint> dataPoints)
    {
        return new DataPointsImpl(dataPoints);
    }

    public static interface Buckets extends Iterable<DataPoints>
    {
        public Stream<DataPoints> stream();

        public List<DataPoints> toList();
    }
}