#if ( $approvalSet )
#if ( $approvalSet.hasApproved() ) 
$launcher Pengajuan akses atas nama '$identityDisplayName' telah disetujui.

#foreach ($item in $approvalSet.approved)
#if ( $item.owner &amp;&amp; $item.owner != $launcher ) 
        Approved By : $item.owner
#end
#if ( $item.comments)
#foreach ($comment in $item.comments)
Completion Comments : $comment
#end
#end
       Application : $item.applicationName
#if ( $item.nativeIdentity )       
           Account : $item.nativeIdentity
#end           
#if ( $item.instance ) 
          Instance : $item.instance
#end
          Operation : $item.operation
#if ( $item.displayName )          
          Attribute : $item.displayName
#elseif ( $item.name ) 
          Attribute : $item.name
#end
#if ( $item.displayValue )
           Role(s) : $item.displayValue
#elseif ( $item.csv ) 
           Role(s) : $item.csv
#end
#if ( $item.requesterComments )
 Requester Comments : $item.requesterComments
#end

#end
#end
#if ( $approvalSet.hasRejected() ) 
$launcher Pengajuan akses atas nama '$identityDisplayName' telah ditolak.

#foreach ($item in $approvalSet.rejected)
        Rejected By : $item.rejecters
#if ( $item.comments)
#foreach ($comment in $item.comments)
Completion Comments : $comment
#end
#end
       Application : $item.applicationName
#if ( $item.nativeIdentity )       
           Account : $item.nativeIdentity
#end           
#if ( $item.instance ) 
          Instance : $item.instance
#end
          Operation : $item.operation
#if ( $item.displayName )          
          Attribute : $item.displayName
#elseif ( $item.name ) 
          Attribute : $item.name
#end
#if ( $item.displayValue )
           Role(s) : $item.displayValue
#elseif ( $item.csv ) 
           Role(s) : $item.csv
#end
#if ( $item.requesterComments )
 Requester Comments: $item.requesterComments
#end

#end
#end
## 
## Handle case where the items are not approved or rejected when the
## approvalScheme is none.
##
#if ( $approvalScheme == "none" )
$launcher Pengajuan akses atas nama '$identityDisplayName' telah disetujui oleh sistem.

#foreach ($item in $approvalSet.items)
#if ( $item.comments)
#foreach ($comment in $item.comments)
Completion Comments : $comment
#end
#end
       Application : $item.applicationName
#if ( $item.nativeIdentity )       
           Account : $item.nativeIdentity
#end           
#if ( $item.instance ) 
          Instance : $item.instance
#end
          Operation : $item.operation
#if ( $item.displayName )          
          Attribute : $item.displayName
#elseif ( $item.name ) 
          Attribute : $item.name
#end
#if ( $item.displayValue )
           Role(s) : $item.displayValue
#elseif ( $item.csv ) 
           Role(s) : $item.csv
#end
#if ( $item.requesterComments )
 Requester Comments : $item.requesterComments
#end
#end
#end
#end