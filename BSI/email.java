import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.Filter;

String firstname = identity.getFirstname();
String lastname = identity.getLastname();
String employeeid = identity.getAttribute("empId");
String lastDigit = employeeid.substring(employeeid.length() - 4);

if((null != firstname) && (null != lastname)) {

	String localpart= firstname + "."+ lastname;
	String domain = "@syariahmandiri.co.id";
	String genEmail= localpart;

	if (genEmail.length() <= 16 ){
		genEmail = localpart + lastDigit + domain;
	} else if (genEmail.length() >= 16 ){
		genEmail = localpart.substring(0, 16) + lastDigit + domain;
	}

	return genEmail;

}



// --------------------------------- dynamic email

import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.Filter;

String emailCheck = identity.getAttribute("email");
System.out.println("*** \n EMAIL Check = \n***\n" + email + "\n****************************************");
String email = "";

if (emailCheck != null) {
	System.out.println("*** \n Masuk 1 = \n***\n" + "\n****************************************");
	email = identity.getAttribute("email");
	System.out.println("*** \n EMAIL Existing = \n***\n" + email + "\n****************************************");

} else {
	System.out.println("*** \n Masuk 2 = \n***\n" + "\n****************************************");
	String firstname = identity.getFirstname();
	String lastname = identity.getLastname();
	String localpart= firstname + "."+ lastname;
	String domain = "@bankbsi.co.id";
	email = localpart + domain;

	QueryOptions qo = new QueryOptions();
	Filter emailFilter = Filter.ignoreCase(Filter.like("email", , Filter.MatchMode.START));
	qo.addFilter(emailFilter);
	int total = context.countObjects(Identity.class, qo);

	if(total != 0) {
		email = localpart + total.toString() + domain;
		System.out.println("*** \n NEW EMAIL = \n***\n" + email + "\n****************************************");
	}
}

return email;


// ----------------

import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.Filter;

String emailCheck = identity.getAttribute("email");
System.out.println("*** \n EMAIL Check = \n***\n" + email + "\n****************************************");

String firstname = identity.getFirstname();
String lastname = identity.getLastname();
String localpart= firstname + "."+ lastname;
String domain = "@bankbsi.co.id";           

if (emailCheck.equals(null)) {
	System.out.println("*** \n Masuk 1 = \n***\n" + "\n****************************************");
	String email = localpart + domain;

	QueryOptions qo = new QueryOptions();
	Filter emailFilter = Filter.ignoreCase(Filter.like("email", localpart, Filter.MatchMode.START));
	qo.addFilter(emailFilter);
	int total = context.countObjects(Identity.class, qo);

	if(total != 0) {
		email = localpart + total.toString() + domain;
		System.out.println("*** \n NEW EMAIL = \n***\n" + email + "\n****************************************");
	}

}

return email;

// email for Identity mapping

import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.QueryOptions;
import sailpoint.object.Filter;

String firstname = link.getAttribute("First Name").toLowerCase();
String lastname = link.getAttribute("Last Name").toLowerCase();

if((null != firstname) && (null != lastname)) {

	String localpart = firstname + "."+ lastname;
	String userMail = localpart.substring(0, 14);
	String domain = "@bankbsi.co.id";           
	String genEmail = userMail + domain;

	QueryOptions qo = new QueryOptions();
	Filter emailFilter = Filter.ignoreCase(Filter.like("email", userMail, Filter.MatchMode.START));
	qo.addFilter(emailFilter);
	int total = context.countObjects(Identity.class, qo);
	if(total != 0) {
		genEmail = userMail + total.toString() + domain;
	}

	return genEmail;


}