import sailpoint.connector.Connector;
import sailpoint.connector.ConnectorFactory;
import sailpoint.object.Application;
import sailpoint.object.Filter;
import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.Capability;
import sailpoint.object.ResourceObject;

      
serilog=org.apache.commons.logging.LogFactory.getLog("SERI.Rule.IdentityCreation.UserName");


//Set the environment for this specific application / customer
// Replace this with the settings for your environment

String mailDomain = "@sailpointdemo.com";
String appSchemaFirstName = "firstname";
String appSchemaLastName = "lastname";

// set default IIQ password for demo purposes
//identity.setPassword("xyzzy");


String firstname = account.getStringAttribute(appSchemaFirstName);
serilog.debug("Firstname: " + firstname);
String lastname = account.getStringAttribute(appSchemaLastName);
serilog.debug("Lastname: " + lastname);

// Handle lastnames with multiple parts, i.e. Van den Heuvel
lastname = lastname.replace(" ",".");

// Make sure the first letter is converted to lowercase for Fullname constructs and UniqueID contructs in multi-part last names.
// i.e. Van.den.Heuvel to van.den.Heuvel
if (lastname.contains(".")) {
  String firstLetter = lastname.substring(0,1).toLowerCase();
  String restLetters = lastname.substring(1);
  lastname = firstLetter + restLetters;
}  

serilog.debug("Lastname: " + lastname);
  
if(firstname==null) {
  serilog.debug("no first name: skipping rule");
} else {
  serilog.debug("firstname="+firstname);
}

if(lastname==null) {
  serilog.debug("no last name: skipping rule");
} else {
  serilog.debug("lastname="+lastname);
}
  
  
// Naming convention for the Identity is: 
// firstname.lastname + add number if not unique
// See other options below for number of characters from first and last name

boolean unique=false;
boolean allLast=false;
boolean usesuffix=false;
int suffix=1;

// Interstring runs the name past the I18n rule

String firstChars=interString(firstname);
// Firstname (first 4 chars) -> special characters should get converted into ascii
//if(firstChars.length()>4) {
//  firstChars=firstChars.substring(0,4);
//}

String lastChars=interString(lastname);
// Lastname (first 5 chars) -> special characters should get converted into ascii
//if(lastChars.length()>5) {
//  lastChars=lastChars.substring(0,5);
//}

String candidate=null;

while(!unique) {
  candidate=firstChars + "." + lastChars;
  if(!usesuffix) {
    usesuffix=true;
  } else {
    candidate+=(suffix++); // inc here, so will be bigger next iteration if required
  }
  
  serilog.debug("candidate Username: "+candidate);

  QueryOptions options = new QueryOptions();
  options.addFilter(Filter.eq("name",candidate));
  Iterator iter =  context.search(Identity.class, options);

  // Do we have already have a cube with the same Cube name?
  //
  if (iter.hasNext()) {
    serilog.debug("Found a match. Trying again..");  
  } else {
    serilog.debug("Candidate is unique. Returning..");
    unique=true; 
  }
}


String mailCandidate=candidate + mailDomain;
identity.setAttribute("email", mailCandidate);
identity.setName(candidate);