Auto Comment Checkin Policy Eclipse TFS Plugin
===============================================

Simple check in policy that will autogenerate check-in comment from associated work items.
If no work item is associated and no comment is specified, policy will fail.
If comment is specified, then work item association is not mandatory.


Installation
------------
Download pre-built JAR plugin from downloads folder and drop it into eclipse dropins folder, then restart eclipse.
You can also build from source (see Building section).


Building
--------

Import project into RCP/Plugin-in dev Eclipse environment and export to plugin JAR.
Drop JAR to Eclipse dropins folder and restart eclipse.


Requirements
------------

TEE 10.1.0 Eclipse TFS plugin must be installed in Eclipse.

