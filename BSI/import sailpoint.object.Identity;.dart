import sailpoint.object.Identity;
import sailpoint.object.QueryOptions;
import sailpoint.object.Filter;
import java.util.Calendar;

Calendar calendar = Calendar.getInstance();
int hour = calendar.get(Calendar.HOUR_OF_DAY);
String firstname = identity.getFirstname();
String lastname = identity.getLastname();

if((null != firstname) && (null != lastname)) {

    String localpart= firstname + "."+ lastname;
    String genEmail= localpart;

    QueryOptions qo = new QueryOptions();
    Filter emailFilter = Filter.ignoreCase(Filter.like("email", localpart, Filter.MatchMode.START));
    qo.addFilter(emailFilter);
    int total = context.countObjects(Identity.class, qo);
    if(total != 0) {
        genEmail = localpart + total.toString() + hour.toString();
    } else {
        genEmail = localpart + hour.toString();
    }

return genEmail;

}