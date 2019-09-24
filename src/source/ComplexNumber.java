package source;

public class ComplexNumber {
	double re;
	double im;

	public ComplexNumber(double real, double imaginary) {
		re = real;
		im = imaginary;
	}

	public ComplexNumber(double a) {
		re = a;
		im = 0;
	}

	@Override
	public String toString() {
		return re + "+" + im + "i";
	}

	public static ComplexNumber Add(ComplexNumber a, ComplexNumber b) {
		return new ComplexNumber(a.re + b.re, a.im + b.im);
	}

	public static ComplexNumber Multiply(ComplexNumber a, ComplexNumber b) {
		return new ComplexNumber(a.re * b.re - a.im * b.im, a.re * b.im + a.im * b.re);
	}

	public static ComplexNumber Multiply(ComplexNumber a, double b) {
		return new ComplexNumber(a.re * b, a.im * b);
	}

	public ComplexNumber Reciprocal() {
		double squdist = im * im + re * re;
		return new ComplexNumber(re / squdist, -im / squdist);
	}

	public double Abs() {
		return Math.sqrt(re * re + im * im);
	}
}
