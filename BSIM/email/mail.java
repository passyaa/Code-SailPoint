// email for Identity mapping

import sailpoint.object.Identity;
import sailpoint.object.Link;
import sailpoint.object.QueryOptions;
import sailpoint.object.Filter;

String fullName = link.getAttribute("nm_peg").toLowerCase();
String nip = link.getAttribute("nip");
String emailName = fullName.replace(" ", ".");
String domain = "@banksinarmas.com";           
String genEmail = emailName + domain;

if((null != emailName)) {

    QueryOptions qo = new QueryOptions();
	Filter emailFilter = Filter.ignoreCase(Filter.like("email", emailName, Filter.MatchMode.START));
	qo.addFilter(emailFilter);
	int total = context.countObjects(Identity.class, qo);
    
    // jika jumlah huruf nama email dibawah 17 huruf
    if (emailName.length() <= 17 ){
        // jika email namenya ditemukan sama
        if(total != 0) {
		    genEmail = emailName + nip.substring(0, 3) + domain;
	    } else {
            genEmail = emailName + domain;
        }
	} else if (genEmail.length() >= 17 ){
		// jika email namenya ditemukan sama
        if(total != 0) {
		    genEmail = emailName.substring(0, 17)  + nip.substring(0, 3) + domain;
	    } else {
            genEmail = emailName.substring(0, 17)  + domain;
        }
	}
}

return genEmail;