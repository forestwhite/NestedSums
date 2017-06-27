package nestedsums;

import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static nestedsums.Q_ab.TWO;
import static nestedsums.Q_ab.sqrt;

/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 * 
 * Complex implements a complex number and defines complex arithmetic and
 * mathematical functions. It is a modification on Bennett's Complex.java:
 * 
 * ## Complex implements a complex number and defines complex 
 * ## arithmetic and mathematical functions
 * ## Last Updated February 27, 1601 
 * ## Copyright 1997-1601
 * ##
 * ## @version 1.0
 * ## @author Andrew G. Bennett
 */
public class Complex extends Number {

    private double x, y;

    /**
     * Constructs the complex number z = u + i*v
     *
     * @param u Real part
     * @param v Imaginary part
     */
    public Complex(double u, double v) {
        x = u;
        y = v;
    }

    /**
     * Real part of this Complex number (the x-coordinate in rectangular
     * coordinates).
     *
     * @return Re[z] where z is this Complex number.
     */
    public double real() {
        return x;
    }

    /**
     * Imaginary part of this Complex number (the y-coordinate in rectangular
     * coordinates).
     *
     * @return Im[z] where z is this Complex number.
     */
    public double imag() {
        return y;
    }

    /**
     * Modulus of this Complex number (the distance from the origin in polar
     * coordinates).
     * 
     * To retain precision for larger sums, the calculations use BigDecimals
     *
     * @return |z| where z is this Complex number.
     */
    public double mod() {
        if (x != 0 || y != 0) {
            if(x*x+y*y < Double.MAX_VALUE)
                return Math.hypot(x, y);
            else {
                BigDecimal a = new BigDecimal(x);
                BigDecimal b = new BigDecimal(y);
                BigDecimal product = a.multiply(a).add(b.multiply(b));
                BigDecimal root = Complex.sqrt(product,400);
                return root.doubleValue(); //might be Double.MAX_VALUE
            }
        } else {
            return 0d;
        }
    }

    /**
     * Argument of this Complex number (the angle in radians with the x-axis in
     * polar coordinates).
     *
     * @return arg(z) where z is this Complex number.
     */
    public double arg() {
        return Math.atan2(y, x);
    }

    /**
     * Complex conjugate of this Complex number (the conjugate of x+i*y is
     * x-i*y).
     *
     * @return z-bar where z is this Complex number.
     */
    public Complex conj() {
        return new Complex(x, -y);
    }

    /**
     * Addition of Complex numbers (doesn't change this Complex number).
     * <br>(x+i*y) + (s+i*t) = (x+s)+i*(y+t).
     *
     * @param w is the number to add.
     * @return z+w where z is this Complex number.
     */
    public Complex add(Complex w) {
        return new Complex(x + w.real(), y + w.imag());
    }

    /**
     * Subtraction of Complex numbers (doesn't change this Complex number).
     * <br>(x+i*y) - (s+i*t) = (x-s)+i*(y-t).
     *
     * @param w is the number to subtract.
     * @return z-w where z is this Complex number.
     */
    public Complex dif(Complex w) {
        return new Complex(x - w.real(), y - w.imag());
    }

    /**
     * Complex multiplication (doesn't change this Complex number).
     *
     * @param w is the number to multiply by.
     * @return z*w where z is this Complex number.
     */
    public Complex prod(Complex w) {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    /**
     * Division of Complex numbers (doesn't change this Complex number).
     * <br>(x+i*y)/(s+i*t) = ((x*s+y*t) + i*(y*s-y*t)) / (s^2+t^2)
     *
     * @param w is the number to divide by
     * @return new Complex number z/w where z is this Complex number
     */
    public Complex div(Complex w) {
        double den = Math.pow(w.mod(), 2);
        return new Complex((x * w.real() + y * w.imag()) / den, (y * w.real() - x * w.imag()) / den);
    }

    /**
     * Complex exponential (doesn't change this Complex number).
     *
     * @return exp(z) where z is this Complex number.
     */
    public Complex exp() {
        return new Complex(Math.exp(x) * Math.cos(y), Math.exp(x) * Math.sin(y));
    }

    /**
     * Principal branch of the Complex logarithm of this Complex number.
     * (doesn't change this Complex number). The principal branch is the branch
     * with -pi < arg <= pi.
     *
     * @return log(z) where z is this Complex number.
     */
    public Complex log() {
        return new Complex(Math.log(this.mod()), this.arg());
    }

    /**
     * Complex square root (doesn't change this complex number). Computes the
     * principal branch of the square root, which is the value with 0 <= arg <
     * pi.
     * 
     * This method offers double precision
     *
     * @return sqrt(z) where z is this Complex number.
     */
    public Complex sqrt() {
        double r = Math.sqrt(this.mod());
        double theta = this.arg() / 2;
        return new Complex(r * Math.cos(theta), r * Math.sin(theta));
    }

    /**
     * Square Root calculator for BigDecimal using the Babylonian method.
     * @param   A       The base from which to calculate a square root.  
     * @param   SCALE   The minimum number binary digits of precision, as the 
     *                  Babylonian method converges quadratically
     * @return  
     */
    public static BigDecimal sqrt(BigDecimal A, final int SCALE) {
        BigDecimal x0 = new BigDecimal("0");
        BigDecimal x1 = new BigDecimal(Math.sqrt(A.doubleValue()));
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, SCALE, ROUND_HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(TWO, SCALE, ROUND_HALF_UP);
        }
        return x1;
    }
    
    // Real cosh function (used to compute complex trig functions)
    private double cosh(double theta) {
        return (Math.exp(theta) + Math.exp(-theta)) / 2;
    }

    // Real sinh function (used to compute complex trig functions)
    private double sinh(double theta) {
        return (Math.exp(theta) - Math.exp(-theta)) / 2;
    }

    /**
     * Sine of this Complex number (doesn't change this Complex number).
     * <br>sin(z) = (exp(i*z)-exp(-i*z))/(2*i).
     *
     * @return sin(z) where z is this Complex number.
     */
    public Complex sin() {
        return new Complex(cosh(y) * Math.sin(x), sinh(y) * Math.cos(x));
    }

    /**
     * Cosine of this Complex number (doesn't change this Complex number).
     * <br>cos(z) = (exp(i*z)+exp(-i*z))/ 2.
     *
     * @return cos(z) where z is this Complex number.
     */
    public Complex cos() {
        return new Complex(cosh(y) * Math.cos(x), -sinh(y) * Math.sin(x));
    }

    /**
     * Hyperbolic sine of this Complex number (doesn't change this Complex
     * number).
     * <br>sinh(z) = (exp(z)-exp(-z))/2.
     *
     * @return sinh(z) where z is this Complex number.
     */
    public Complex sinh() {
        return new Complex(sinh(x) * Math.cos(y), cosh(x) * Math.sin(y));
    }

    /**
     * Hyperbolic cosine of this Complex number (doesn't change this Complex
     * number).
     * <br>cosh(z) = (exp(z) + exp(-z)) / 2.
     *
     * @return cosh(z) where z is this Complex number.
     */
    public Complex cosh() {
        return new Complex(cosh(x) * Math.cos(y), sinh(x) * Math.sin(y));
    }

    /**
     * Tangent of this Complex number (doesn't change this Complex number).
     * <br>tan(z) = sin(z)/cos(z).
     *
     * @return tan(z) where z is this Complex number.
     */
    public Complex tan() {
        return (this.sin()).div(this.cos());
    }

    /**
     * Negative of this complex number (chs stands for change sign). This
     * produces a new Complex number and doesn't change this Complex number.
     * <br>-(x+i*y) = -x-i*y.
     *
     * @return -z where z is this Complex number.
     */
    public Complex chs() {
        return new Complex(-x, -y);
    }

    /**
     * String representation of this Complex number.
     *
     * @return String   x+i*y, x-i*y, x, or i*y as appropriate.
     */
    @Override
    public String toString() {
        if (x != 0 && y > 0) {
            return x + " + " + y + "i";
        }
        if (x != 0 && y < 0) {
            return x + " - " + (-y) + "i";
        }
        if (y == 0) {
            return String.valueOf(x);
        }
        if (x == 0) {
            return y + "i";
        }
        // shouldn't get here (unless Inf or NaN)
        return x + " + i*" + y;
    }
    
    /**
     * Modulus of this Complex number (the distance from the origin in polar
     * coordinates) as an int
     *
     * @return int  |z| where z is this Complex number.
     */
    @Override
    public int intValue() {
        return new Double(mod()).intValue();
    }

    @Override
    public long longValue() {
        return Math.round(mod());
    }

    @Override
    public float floatValue() {
        return new Double(mod()).floatValue();
    }

    @Override
    public double doubleValue() {
        
        return mod();
    }
}
