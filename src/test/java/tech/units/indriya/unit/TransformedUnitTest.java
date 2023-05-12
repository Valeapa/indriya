package tech.units.indriya.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.units.indriya.function.RationalNumber;
import tech.units.indriya.function.gravity.GravityConverter;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Force;
import javax.measure.quantity.Speed;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransformedUnitTest {


	private static final Quantity<Speed> ONE_MS = Quantities.getQuantity(1, Units.METRE_PER_SECOND);
	private static final Quantity<Speed> ONE_KNOT = Quantities.getQuantity(1, Units.KNOT);

	private static final Quantity<Force> ONE_NEWTON = Quantities.getQuantity(1, Units.NEWTON);
	private static final Quantity<Force> ONE_KG_FORCE = Quantities.getQuantity(1, Units.KG_FORCE);

	private static final BigDecimal STANDARD_GRAVITY = BigDecimal.valueOf(980665L, 5);
	private static final RationalNumber STANDARD_GRAVITY_RATIONAL = RationalNumber.of(BigDecimal.valueOf(980665L, 5));


	@Test
	void convertFromMsToKnots() {
		Quantity<Speed> speedKnots = ONE_MS.to(Units.KNOT);

		Assertions.assertEquals((double) 3600 / 1852, speedKnots.getValue().doubleValue(), 1E-12);
		Assertions.assertEquals(Units.KNOT, speedKnots.getUnit());

	}


	@Test
	void convertFromKnotToMs() {
		Quantity<Speed> speedMS = ONE_KNOT.to(Units.METRE_PER_SECOND);

		Assertions.assertEquals((double) 1852 / 3600, speedMS.getValue().doubleValue(), 1E-12);
		Assertions.assertEquals(Units.METRE_PER_SECOND, speedMS.getUnit());
	}

	@Test
	void compareDoubleToBigDecimalStandardGravityTest() {
		double standardGravityDouble = 9.80665;

		double result = 1.0 / standardGravityDouble;

		BigDecimal bigDecimalFromDouble = BigDecimal.valueOf(result);
		BigDecimal bigDecimalDivision = BigDecimal.ONE.divide(STANDARD_GRAVITY, 19, RoundingMode.HALF_UP);

		Assertions.assertEquals(bigDecimalDivision.doubleValue(), bigDecimalFromDouble.doubleValue(), 1E-12);
	}


	@Test
	void changeGravityValueTest() {

		// La Quantity del KG_FORCE se crea usando la Unit.KG_FORCE standard (la que tiene la gravedad standard)
		Quantity<Force> newtonQuantityStandardGravity = ONE_KG_FORCE.to(Units.NEWTON);

		Assertions.assertEquals(Quantities.getQuantity(STANDARD_GRAVITY_RATIONAL, Units.NEWTON), newtonQuantityStandardGravity);
		Assertions.assertEquals(ONE_KG_FORCE, newtonQuantityStandardGravity.to(Units.KG_FORCE));

		// Se modifica el valor de la gravedad
		GravityConverter.getInstance().setValue(RationalNumber.of(10, 1));


		Quantity<Force> newtonQuantityGravity_10 = ONE_KG_FORCE.to(Units.NEWTON);
		Assertions.assertEquals(Quantities.getQuantity(10, Units.NEWTON), newtonQuantityGravity_10);

		// Expected 0.98 kg-f - vemos que una quantity creada antes de modificar el valor de la gravedad se transforma correctamente en base al nuevo valor
		Assertions.assertEquals(Quantities.getQuantity(STANDARD_GRAVITY_RATIONAL.divide(RationalNumber.of(10,1)), Units.KG_FORCE),
								newtonQuantityStandardGravity.to(Units.KG_FORCE));



	}

	@Test
	void convertFromNewtonsToKgForce() {
		Quantity<Force> kgForceQuantity = ONE_NEWTON.to(Units.KG_FORCE);

		Assertions.assertEquals(1.0 / STANDARD_GRAVITY.doubleValue(), kgForceQuantity.getValue().doubleValue(), 1E-12);
		Assertions.assertEquals(Units.KG_FORCE, kgForceQuantity.getUnit());
	}

	@Test
	void convertFromKgForceToNewtons() {
		Quantity<Force> newtonQuantity = ONE_KG_FORCE.to(Units.NEWTON);

		Assertions.assertEquals(STANDARD_GRAVITY.doubleValue(), newtonQuantity.getValue().doubleValue(), 1E-12);
		Assertions.assertEquals(Units.NEWTON, newtonQuantity.getUnit());
	}

}
