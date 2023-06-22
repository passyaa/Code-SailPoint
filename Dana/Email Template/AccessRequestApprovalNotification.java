<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE EmailTemplate PUBLIC "sailpoint.dtd" "sailpoint.dtd">
<EmailTemplate created="1665329547088" id="0a166722830c11648183bd604750562e" modified="1676563761646" name="Access Request Approval Notification">
  <Body>
    &lt;html>
      &lt;head>
        &lt;style>
          .alignMe b {
            display: inline-block;
            width: 20%;
            position: relative;
            padding-right: 10px; /* Ensures colon does not overlay the text */
          }

          .alignMe b::after {
            content: " ";
            position: absolute;
            right: 10px;
          }
        &lt;/style>
      &lt;/head>
      &lt;body>
        &lt;p>Dear Approver,&lt;/p>
        &lt;p>Terdapat permintaan perubahan yang membutuhkan perhatian anda dengan detail sebagai berikut:&lt;/p>
        &lt;ul class="alignMe">
          &lt;li>&lt;b>NIP&lt;/b>: $identityName&lt;/li>
          &lt;li>&lt;b>Nama&lt;/b>: $identityDisplayName&lt;/li>
          #foreach ($approvalItem in $approvalSet.items)
            #if ( $approvalSet.items.size() > 1 )
              &lt;br>
            #end
            &lt;li>&lt;b>Aplikasi&lt;/b>: $approvalItem.applicationName&lt;/li>
            #if ( $approvalItem.operation == "Add" )
              &lt;li>&lt;b>Operasi&lt;/b>: Penambahan Akses&lt;/li>
            #elseif ( $approvalItem.operation == "Remove" )
              &lt;li>&lt;b>Operasi&lt;/b>: Penghapusan Access&lt;/li>
            #elseif ( $approvalItem.operation == "Enable" )
              &lt;li>&lt;b>Operasi&lt;/b>: Pengaktifan Akun&lt;/li>
            #elseif ( $approvalItem.operation == "Disable" )
              &lt;li>&lt;b>Operasi&lt;/b>: Penonaktifan Akun&lt;/li>
            #elseif ( $approvalItem.operation == "Create" )
              &lt;li>&lt;b>Operasi&lt;/b>: Pembuatan Akun&lt;/li>
            #elseif ( $approvalItem.operation == "Delete" )
              &lt;li>&lt;b>Operasi&lt;/b>: Penghapusan Akun&lt;/li>
            #end
            #if ( $approvalItem.displayValue )          
              &lt;li>&lt;b>Perubahan&lt;/b>: $approvalItem.displayValue&lt;/li>
            #elseif ( $approvalItem.csv) 
              &lt;li>&lt;b>Perubahan&lt;/b>: $approvalItem.csv&lt;/li>
            #end
            #if ( $approvalItem.requesterComments )
              &lt;li>&lt;b>Deskripsi&lt;/b>: $approvalItem.requesterComments&lt;/li>
            #end
          #end
        &lt;/ul>
        &lt;p>Mohon meninjau perubahan dan menyetujui/menolak permintaan akses melalui Identity portal (https://10.253.249.2:8080/identityiq/).&lt;/p>
        &lt;p>Best Regards,&lt;br>Identity Portal Admin&lt;/p>
      &lt;/body>
    &lt;/html>
  </Body>
  <Description>
    Email Template for notifying approvers that they need to approve access requests
  </Description>
  <Signature>
    <Inputs>
      <Argument name="appName" type="Workflow">
        <Description>The appName object being executed.</Description>
      </Argument>
      <Argument name="workflow" type="Workflow">
        <Description>The Workflow object being executed.</Description>
      </Argument>
      <Argument name="item" type="WorkItem">
        <Description>The WorkItem representing the review.</Description>
      </Argument>
      <Argument name="approvalSet" type="ApprovalSet">
        <Description>The ApprovalSet object contained in the work item.</Description>
      </Argument>
      <Argument name="nip" type="string">
        <Description>The NIP of the user.</Description>
      </Argument>
      <Argument name="launcher" type="string">
        <Description>The name of the Identity that launched the workflow.</Description>
      </Argument>
      <Argument name="identityName" type="string">
        <Description>The name of the Identity that is being changed.</Description>
      </Argument>
      <Argument name="identityDisplayName" type="string">
        <Description>The display name of the Identity that is being changed.</Description>
      </Argument>
    </Inputs>
  </Signature>
  <Subject>[Access Request] Permohonan Persetujuan Akun '$identityDisplayName'</Subject>
</EmailTemplate>
