import sailpoint.object.Link;

String firstname = link.getAttribute("nama_depan");
String lastname = link.getAttribute("nama_belakang");

String fullname = firstname + "." + lastname;

return fullname;