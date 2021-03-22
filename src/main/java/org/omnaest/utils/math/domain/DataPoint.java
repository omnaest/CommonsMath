package org.omnaest.utils.math.domain;

import java.util.function.DoubleSupplier;

public class DataPoint implements DoubleSupplier
{
    private double value;
    private Object reference;

    protected DataPoint(double value, Object reference)
    {
        super();
        this.value = value;
        this.reference = reference;
    }

    public static DataPoint of(double value)
    {
        return new DataPoint(value, null);
    }

    public static <R> DataPoint of(double value, R reference)
    {
        return new DataPoint(value, reference);
    }

    @SuppressWarnings("unchecked")
    public <R> R getReferenceAs(Class<R> type)
    {
        return (R) this.reference;
    }

    @Override
    public double getAsDouble()
    {
        return this.value;
    }
}