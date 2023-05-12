package tech.units.indriya.function.gravity;

import tech.units.indriya.function.AbstractConverter;
import tech.units.indriya.function.Calculus;
import tech.units.indriya.function.MultiplyConverter;
import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.internal.function.Calculator;
import tech.units.indriya.spi.NumberSystem;

import javax.measure.UnitConverter;
import java.math.BigDecimal;
import java.util.Objects;

public class GravityConverter extends AbstractConverter implements MultiplyConverter, ValueSetter<RationalNumber> {

	private static final RationalNumber STANDARD_GRAVITY
		= RationalNumber.of(BigDecimal.valueOf(980665L, 5));	// Standard gravity

	private RationalNumber gravityValue;

	private static final GravityConverter INSTANCE = new GravityConverter();

	public static GravityConverter getInstance() {
		return INSTANCE;
	}


	private GravityConverter() {
		this.gravityValue = STANDARD_GRAVITY;
	}


	@Override
	public boolean equals(Object cvtr) {
		return cvtr == this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(gravityValue);
	}

	@Override
	protected String transformationLiteral() {
		return gravityValue + " * value";
	}

	@Override
	protected AbstractConverter inverseWhenNotIdentity() {
		return InverseGravityConverter.getInstance();
	}

	@Override
	protected boolean canReduceWith(AbstractConverter that) {
		return false;
	}

	@Override
	protected Number convertWhenNotIdentity(Number value) {
		return Calculator.of(gravityValue)
						 .multiply(value)
						 .peek();
	}


	@Override
	public double getAsDouble() {
		return gravityValue.doubleValue();
	}

	@Override
	public int compareTo(UnitConverter o) {
		if (this == o) {
			return 0;
		}
		if (o instanceof MultiplyConverter) {
			NumberSystem ns = Calculus.currentNumberSystem();
			return ns.compare(this.getValue(), ((MultiplyConverter) o).getValue());
		}
		return -1;
	}

	@Override
	public boolean isIdentity() {
		return false;
	}

	@Override
	public Number getValue() {
		return gravityValue;
	}

	@Override
	public void setValue(RationalNumber newValue) {
		this.gravityValue = newValue;
		InverseGravityConverter.getInstance().setValue(newValue.reciprocal());
	}

	private static class InverseGravityConverter extends AbstractConverter implements MultiplyConverter, ValueSetter<RationalNumber> {

		private static InverseGravityConverter INSTANCE;

		private RationalNumber inverseGravityValue;

		private InverseGravityConverter() {
			this.inverseGravityValue = STANDARD_GRAVITY.reciprocal();
		}


		public static InverseGravityConverter getInstance() {
			if (INSTANCE == null) {
				INSTANCE = new InverseGravityConverter();
			}

			return INSTANCE;
		}

		@Override
		public double getAsDouble() {
			return this.inverseGravityValue.doubleValue();
		}

		@Override
		public boolean equals(Object cvtr) {
			return cvtr == this;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(inverseGravityValue);
		}

		@Override
		protected String transformationLiteral() {
			return inverseGravityValue.toString() + " * value";
		}

		@Override
		protected AbstractConverter inverseWhenNotIdentity() {
			return GravityConverter.getInstance();
		}

		@Override
		protected boolean canReduceWith(AbstractConverter that) {
			return false;
		}

		@Override
		protected Number convertWhenNotIdentity(Number value) {
			return Calculator.of(inverseGravityValue)
							 .multiply(value)
							 .peek();
		}

		@Override
		public int compareTo(UnitConverter o) {
			if (this == o) {
				return 0;
			}
			if (o instanceof MultiplyConverter) {
				NumberSystem ns = Calculus.currentNumberSystem();
				return ns.compare(this.getValue(), ((MultiplyConverter) o).getValue());
			}
			return -1;
		}

		@Override
		public boolean isIdentity() {
			return false;
		}

		@Override
		public Number getValue() {
			return inverseGravityValue;
		}

		@Override
		public void setValue(RationalNumber newValue) {
			this.inverseGravityValue = newValue;
		}
	}
}
