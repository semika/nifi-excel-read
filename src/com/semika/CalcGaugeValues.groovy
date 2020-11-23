import java.math.BigDecimal
import java.security.MessageDigest
import java.text.DecimalFormat


FlowFile flowFile = session.get()

if (!flowFile) {
	return
}

try {
	
	String meterReading    = flowFile.getAttribute("Opening_Meter");
	

		
	if (meterReading != null && meterReading.trim().length() > 0) {
		
		// copy past start
		
		DecimalFormat df = new DecimalFormat(".00");

		//Find Gauge-1 values
		Double doubleMeterReading  = Double.parseDouble(meterReading);
		Double readingInTwoDecimal = Double.parseDouble(df.format(doubleMeterReading));

		//Find Feet(gauge-1),
		Double feetModValue = readingInTwoDecimal%12;
		Double feet = (readingInTwoDecimal - feetModValue)/12;


		//Find Inch(gauge-1),
		BigDecimal modValue = new BigDecimal(df.format(feetModValue));
		int inches = modValue.intValue();


		//Find 'Frac Inch(gauge-1)'
		BigDecimal bigDecimal = new BigDecimal(df.format(doubleMeterReading));
		int intValue = bigDecimal.intValue();
		double fracInch = bigDecimal.subtract(new BigDecimal(intValue)).doubleValue();

		int quarteInchValue = 0;

		if (0.0 <= fracInch &&  fracInch <= 0.25) {
			quarteInchValue = 0;
		} else if (0.25 < fracInch &&  fracInch <= 0.5) {
			quarteInchValue = 1;
		} else if (0.5 < fracInch &&  fracInch <= 0.75) {
			quarteInchValue = 2;
		} else if (0.75 < fracInch &&  fracInch <= 1) {
			quarteInchValue = 3;
		}
		
		//copy past end
		
		session.transfer(flowFile, REL_SUCCESS)
		
		flowFile = session.putAttribute(flowFile, "Feet(gauge-1)", feet + "")
		flowFile = session.putAttribute(flowFile, "Inch(gauge-1)", inches + "")
		flowFile = session.putAttribute(flowFile, "Frac Inch(gauge-1)", quarteInchValue + "")
	} else {
		//send the flow file to failure relationship. 
		session.transfer(flowFile, REL_FAILURE)
	}
	
} catch (Exception e) {
	log.error("Error when finding gauge values", e)

	session.transfer(flowFile, REL_FAILURE)
}