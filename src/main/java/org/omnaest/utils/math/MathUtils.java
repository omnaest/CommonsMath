package org.omnaest.utils.math;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.omnaest.utils.math.domain.DataPoint;
import org.omnaest.utils.math.domain.DataPoints;

public class MathUtils
{
    public static DataAnalyzer analyze()
    {
        return new DataAnalyzer()
        {
            @Override
            public DataPoints data(double... dataPoints)
            {
                return this.data(Arrays.stream(dataPoints)
                                       .boxed()
                                       .map(DataPoint::of)
                                       .collect(Collectors.toList()));
            }

            @Override
            public DataPoints data(Collection<DataPoint> dataPoints)
            {
                return DataPoints.of(dataPoints);
            }

        };
    }

    public static interface DataAnalyzer
    {
        public DataPoints data(Collection<DataPoint> dataPoints);

        public DataPoints data(double... dataPoints);

    }
}
