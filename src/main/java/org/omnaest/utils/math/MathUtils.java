package org.omnaest.utils.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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

    public static OneFactor oneFactor()
    {
        return new OneFactor()
        {
            @Override
            public OneFactorDeviationNormalizer normalizer()
            {
                return new OneFactorDeviationNormalizer()
                {
                    private List<Double> values              = new ArrayList<>();
                    private double       normalizationFactor = 1.0;
                    private double       deviationLimit      = 2.0;

                    @Override
                    public OneFactorDeviationNormalizer withDeviationLimit(double deviationLimit)
                    {
                        this.deviationLimit = deviationLimit;
                        this.calculateNormalizationFactor();
                        return this;
                    }

                    @Override
                    public void accept(double value)
                    {
                        this.values.add(value);
                        this.calculateNormalizationFactor();
                    }

                    @Override
                    public void accept(Double value)
                    {
                        Optional.ofNullable(value)
                                .ifPresent(iValue -> this.accept(iValue.doubleValue()));
                    }

                    private void calculateNormalizationFactor()
                    {
                        double max = this.values.stream()
                                                .mapToDouble(v -> v)
                                                .max()
                                                .orElse(1.0);
                        this.normalizationFactor = Math.log(this.deviationLimit) / Math.log(max);
                    }

                    @Override
                    public double applyAsDouble(double value)
                    {
                        return applyFactor(value, this.normalizationFactor);
                    }

                    @Override
                    public double getNormalizationFactor()
                    {
                        return this.normalizationFactor;
                    }

                    @Override
                    public OneFactorDeviationNormalizer acceptAll(double... values)
                    {
                        if (values != null)
                        {
                            for (double value : values)
                            {
                                this.accept(value);
                            }
                        }
                        return this;
                    }

                    @Override
                    public OneFactorDeviationNormalizer acceptAll(Collection<Double> values)
                    {
                        Optional.ofNullable(values)
                                .orElse(Collections.emptyList())
                                .forEach(this);
                        return this;
                    }

                    @Override
                    public OneFactorDeviationNormalizer acceptAll(DoubleStream values)
                    {
                        Optional.ofNullable(values)
                                .orElse(DoubleStream.empty())
                                .forEach(this);
                        return this;
                    }

                };
            }

            @Override
            public double applyFactor(double value, double factor)
            {
                if (value < 0)
                {
                    throw new IllegalArgumentException("value must be greater or equal to zero");
                }

                return Math.pow(value, factor);
            }
        };
    }

    public static interface OneFactor
    {
        public OneFactorDeviationNormalizer normalizer();

        public double applyFactor(double value, double factor);
    }

    public static NormalizerChoice normalize()
    {
        return new NormalizerChoice()
        {
            @Override
            public OneFactorDeviationNormalizer byOneFactorDeviation()
            {
                return oneFactor().normalizer();
            }
        };
    }

    public static interface NormalizerChoice
    {
        public OneFactorDeviationNormalizer byOneFactorDeviation();
    }

    public static interface OneFactorDeviationNormalizer extends DoubleConsumer, Consumer<Double>, DoubleUnaryOperator
    {

        public OneFactorDeviationNormalizer withDeviationLimit(double deviationLimit);

        public OneFactorDeviationNormalizer acceptAll(double... values);

        public OneFactorDeviationNormalizer acceptAll(Collection<Double> values);

        public OneFactorDeviationNormalizer acceptAll(DoubleStream values);

        public double getNormalizationFactor();

    }
}
