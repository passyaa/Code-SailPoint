// USER_ID
return identity.name;

// CREATED BY
String value = sailpoint;

// VERSI
return identity.name;

// BRACH_CODE
import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.Application;

String branchCode = identity.getAttribute("kodeUnit");
return branchCode;

// ENABLED
String value = 1;

// END_TIME
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

LocalDate now = LocalDate.now(); 
LocalDate lastDayOfYear = now.with(TemporalAdjusters.lastDayOfYear());
String dateTimeEndOfYear = lastDayOfYear + " 00:00:00.0";

return dateTimeEndOfYear;

// NAME
return identity.displayName;

// PASSWORD
$2a$12$4ParYrD9taa1dTLXaRqpROT4BTPyjm.pN/Xsd5qbTPfoLtAo5l5TO

// START_TIME
import java.text.DateFormat;
import java.text.SimpleDateFormat;

DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Date now = new Date();
return dateFormatter.format(now);

// CREATED_DATE
import java.text.DateFormat;
import java.text.SimpleDateFormat;

DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
Date now = new Date();
return dateFormatter.format(now);

// STATUS_AKTIF
1

// STATUS_OTP
1

// EMAIL
return identity.getEmail();