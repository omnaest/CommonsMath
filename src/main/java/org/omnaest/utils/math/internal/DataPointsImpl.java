package org.omnaest.utils.math.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.StatUtils;
import org.omnaest.utils.math.domain.DataPoint;
import org.omnaest.utils.math.domain.DataPoints;
import org.omnaest.utils.math.domain.SplittedDataPoints;

public class DataPointsImpl implements DataPoints
{
    private List<DataPoint> dataPoints;

    public DataPointsImpl(Collection<DataPoint> dataPoints)
    {
        this.dataPoints = dataPoints.stream()
                                    .collect(Collectors.toList());
    }

    @Override
    public Iterator<DataPoint> iterator()
    {
        return this.stream()
                   .iterator();
    }

    @Override
    public Stream<DataPoint> stream()
    {
        return this.dataPoints.stream();
    }

    @Override
    public double[] toDoubleArray()
    {
        return this.dataPoints.stream()
                              .mapToDouble(DataPoint::getAsDouble)
                              .toArray();
    }

    @Override
    public double calculateAverage()
    {
        return StatUtils.mean(this.dataPoints.stream()
                                             .mapToDouble(DataPoint::getAsDouble)
                                             .toArray());
    }

    @Override
    public double calculatePercentile(double percentile)
    {
        return StatUtils.percentile(this.dataPoints.stream()
                                                   .mapToDouble(DataPoint::getAsDouble)
                                                   .toArray(),
                                    percentile);
    }

    @Override
    public SplittedDataPoints splitByLowerAndUpperPercentile(double percentileSum)
    {
        double excludedPercentileSum = 100.0 - percentileSum;
        double lowerPercentileValue = this.calculatePercentile(excludedPercentileSum / 2.0);
        double upperPercentileValue = this.calculatePercentile(100.0 - excludedPercentileSum / 2.0);
        Map<Boolean, List<DataPoint>> includedToDataPoints = this.dataPoints.stream()
                                                                            .collect(Collectors.partitioningBy(dataPoint -> lowerPercentileValue <= dataPoint.getAsDouble()
                                                                                    && dataPoint.getAsDouble() <= upperPercentileValue));
        return new SplittedDataPoints()
        {
            @Override
            public DataPoints included()
            {
                return DataPoints.of(includedToDataPoints.get(true));
            }

            @Override
            public DataPoints excluded()
            {
                return DataPoints.of(includedToDataPoints.get(false));
            }
        };
    }

    @Override
    public Buckets splitIntoLinearBuckets(int numberOfBuckets)
    {
        double max = this.dataPoints.stream()
                                    .mapToDouble(dp -> dp.getAsDouble())
                                    .max()
                                    .orElse(0.0);
        double min = this.dataPoints.stream()
                                    .mapToDouble(dp -> dp.getAsDouble())
                                    .min()
                                    .orElse(0.0);
        double range = max - min + Double.MIN_VALUE;
        Function<DataPoint, Integer> classifier = dataPoint -> (int) Math.floor(Math.min(Math.max(0, (dataPoint.getAsDouble() - min) / range),
                                                                                         0.9999999999999999)
                * numberOfBuckets);

        Map<Integer, List<DataPoint>> bucketIndexToDataPoints = this.dataPoints.stream()
                                                                               .collect(Collectors.groupingBy(classifier));
        return new Buckets()
        {
            @Override
            public Iterator<DataPoints> iterator()
            {
                return this.stream()
                           .iterator();
            }

            @Override
            public List<DataPoints> toList()
            {
                return this.stream()
                           .collect(Collectors.toList());
            }

            @Override
            public Stream<DataPoints> stream()
            {
                return bucketIndexToDataPoints.keySet()
                                              .stream()
                                              .sorted()
                                              .map(index -> DataPoints.of(bucketIndexToDataPoints.getOrDefault(index, Collections.emptyList())));
            }
        };
    }

}