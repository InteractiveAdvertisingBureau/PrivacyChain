# Reference Specification of IAB's PrivacyChain

## Overview

The draft PrivacyChain specification is documented in the [IAB PrivacyChain Specification](https://github.com/InteractiveAdvertisingBureau/PrivacyChain).  This document is intended to clarify that draft for use in reference implementations of the specification.

This reference implementation also clarifies the unspecified behaviors necessary to make a practical implementation.  This document also provides guidance towards the future evolution of the API in the _Compatibility_ subsections.

## Changes & Decisions

### Subscription API

* The subscription API is not implemented and is considered deprecated.  It will not appear in future versions of the API..

### Notifications Subsystem

* The notification subsystem will not be part of the PrivacyChain database.

### Find by Entity

* The _Find By Entity_ aspects API is implemented for small databases in this reference implementation.
* It is _unspecified_ whether the service, even this reference implementation, produces a document containing all possible such records in a streaming fashion or it truncates the output after some finite amount of time.  

### Create by Array or List

* These interfaces are deprecated and will not appear in future versions of the API.
* These interfaces are defined to have the semantic of the repeated invocation of the corresponding singleton `POST` operation.

## Omissions

### API Versioning

The API is not yet _versioned_.  As such versioning of the API must be done at the domain (DNS) level in the URL.
For example `https://v1.api.privacychain.example.com/` could be a location where a PrivacyChain could be presented.

Provisionally, this specification should be considered as _Version Two_ of the API were the previous specification was given the appelation [audienceChain 1.0.0](https://github.com/InteractiveAdvertisingBureau/PrivacyChain/blob/master/audience_chain_api.yml).  That usage is now obsoleted.

Future versions of the PrivacyChain API will formalize the versioning in [https://www.openapis.org/[(OpenAPI)-type specification documents.

### Identifiers

The origin and semantics of the identifiers used to label data subjects is not specified.

### Consent

The use of IAB's Transparency & Consent Framework [v1.1](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework) is assumed.  However, as of 2019-05, [v2.0](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/tree/master/TCFv2) has been published for comment.  This specifiication does not address the capabilities of v2.0.

## The Data Types

### Identifiers

An identifier is the primary key of a record type.

* `{id}` is a 64-bit integer coded in decimal.

When used in a reference context, it is prefixed with the name of the data type being referenced:

 * `{consentId}` an identifier which references a consent record.
 * `{transferId}` an identifier which references a (data) transfer record.
 * `{subscriptionId}` an identifier which references a (data) transfer record.

### Names

A name is the cultural denotation of a legal entity, a structure such as an organization or corporation to which a granted consent may be received.

So thus an _entity_ is not freeform text,
but rather is a {{vendorId}}
as recorded in the IAB Transparency & Consent Framework's Global Vendor List (GVL)
Clarify.

* `{entity}` is _something_ that references an _entity_.

The general intent is that the referenced _entity_ is the canonical legal name of a governed organizaiton operating withn a covered jurisdication.  An _entity_ is not expected to be the referencable name of a natural person.

Matching, as known to the PrivacyChain service, is exact (`operator==` is numerical or lexicographic equality).  The values expressible in an `{entity}` are those expressible in a JSON string, _i.e._ UTF-8 and of "reasonable" length. 

### Dates

* `{expires}` is a date denominated in seconds since The Epoch (1970-01-01T00:00:00 UTC).

Dates are therefore expressed as decimal integers, assuming a 64-bit underlying integer type.

### Attributes

* `{attributes}` is a representation of the Transparency & Consent Framework (TCF) object.

The preferred encoding of TCF is as a string as if it were to be transmitted an HTTP header, an HTML4 Cookie, or over IAB's OpenRTB.  

Alternatively:

The _attributes_ may or may not be stored by a conforming implementation.
The current implementation does not guarantee to store the values.
Of course, there is an implementation-specified limit on the size of the attributes that can be stored in any implementation.

### Status

* `{status}` is an indication of the status of a record.
 * good status
 * bad status

The universe of values available for `{status}` is unspecified.
The following values are assumed

* `true`, `1` implies that the record is valid, that such consent has been asserted.
* `false`, `0` impiles that the record is invalid, that the consent has been _revoked_.

There is no meaningful semantic difference between an absent consent record and a revoked consent record.

## Response Codes

### Success

* `200`
 * OK (Everything is good)
* `202`
 * Accepted (The document is accepted)

### Redirection

The 300-series codes are not used for signalling.

### Application Failure

* `400`
 * Incomplete query
 * Inappropriate query
* `404`
 * Invalid URL

### Service Failures

* `500`
 * Inappropriate HTTP verb
 * Inappropriate URL
 * Some other problem

## The REST API of PrivacyChain

### `POST /consent`

#### Purpose
Add a new consent record to the database.

#### Preconditions
The `{id}` MUST NOT already exist.

#### Postconditions
Depending upon success or failure.

##### Success
Response: `202` Accepted

The consent is recorded at the identifier `{id}`.
The identifier becomes `{consentId}` in a reference context.

##### Failures
Response: `400` Invalid

The supplied `{id}` already exists.  Instead use `PUT /consent/{consentId}`

#### Submitted Document
The posted document shall have the following components:

* `{id}` required
* `{consentType}` required
* `{entity}` required
* `{expires}` required
* `{attributes}` required
* `{status}` required

#### Response Document
No document is returned back to the client.

### `PUT /consent/{consentId}`

#### Purpose
Update an existing consent.

Each operation completely overwrites the document that was there previously.
As such, there is no way to update only a single field at a time.
That might be useful, for example to only extend the expiration time of a consent while leaving other values the same.

#### Preconditions
A consent record alreayd exists at `{consentId}`.

If supplied, the `{id}` of the document must match the `{consentId}` of the path.

#### Postconditions
Depending upon success or failure.

##### Success
Response: `202` Accepted

The consent record is overwritten with the new information.

##### Failures
Response: `400` Invalid

* The `{id}` is supplied and does not match the `{consentId}`
* The `{consentId}` is not valid for the data type.

Response: `404` Not Found

No record exists at `{consentId}`

#### Submitted Document
The posted document shall have the following components:

* `{id}` optional
* `{consentType}` required
* `{entity}` required
* `{expires}` required
* `{attributes}` required
* `{status}` required

#### Response Document
No document is returned back to the client.

### `POST /consent/createWithArray`

#### Purpose
Create multiple consent records from the supplied document, which is an array.

#### Preconditions
None of the `{id}` may already exist.

#### Postconditions
Depending upon success or failure.

##### Complete Success
The response under complete success is the same as for the singleton `POST /consent`.

##### Partial Success & Partial Failure
See the subsequent section _Complete Failure_.

This API offers no way to return any fidelity into _which_ of the consent records failed to be added to the database.
Only a coarse failure is returned as if _all_ records had failed to be written.  However some unspecified number of records may have been written to the database.  It is unspecified whether the server shall terminate processing at the first failure or shall continue on to attempt as many _sub-POST_ operations as possible.

##### Complete Failure
The response under complete success is the same as for the singleton `POST /consent`.

#### Submitted Document
The document shall consiste of an array of consent objects, each of which contains the following:

* `{id}` required
* `{consentType}` required
* `{entity}` required
* `{expires}` required
* `{attributes}` required
* `{status}` required

#### Response Document
No document is returned to the client.

#### Compatibility
The utility of this API interface is unclear.  It provides exactly the same capability as repeated calls to `POST /consent`.  Clients should expect this call to disappear in future versions of the API.

The preferred approach for large batch uploads is for the client to use a _keepalive_ feature of the HTTP protocol to allow a single connection to be reused for multiple invocations of `POST /consent`.

### `POST /consent/createWithList`

This call is basically the same as `POST /consent/createWithArray`.  It has the same semantic and the same difficulties with returning any meaningful response code.  It will be removed in future versions of the API.

It is substantially the same as _createWithArray_ except that a _list_ is interpreted to mean a document of [JSON Lines](https://jsonlines.org).

#### Submitted Document
The submitted document is a _list_ of consent record objects which are to be created.

#### Response Document
No document is returned.

#### Compatibility

This interface will disappear in future versions of the API.
It will disappear in the same version where `/consent/createWithArray` disappears.

### `GET /consent/{consentId}`

#### Purpose
Recover the consent record from its `{consentId}`.

#### Preconditions
There are no preconditions.

#### Postconditions
Depending upon the success or the failure of the recovery.

##### Success
Response: `200` OK

The consent record is returned.

##### Failure
Response: `404` Not Found

There is no document at the indicated `{consentId}`

#### Submitted  Document
There is no submitted document on a `GET` request.

#### Response Document
The document returned will contain the following elements:

* `{id}`
* `{consentType}`
* `{entity}`
* `{expires}`
* `{attributes}`
* `{status}`

### `GET /consent/findIdsByEntity`

#### Purpose
Find consent records by which are referenced by the entity named in the query parameter `{entity}`

#### Preconditions
None.

#### Postconditions
Depending upon the success or the failure of the recovery.

##### Success
Response: `200` OK

The response document is returned.
This document contains the `{consentId}` of all consent records which mention the `{entity}` of the original query.

##### Failure
Response: `400` Invalid

The supplied `{entity}' is not appropriate for the data type.

#### Response Document

A single document of [JSON Lines](http://jsonlines.org/) of `{consentId}` is returned.

#### Compatibility

Of course the notion of querying for and returning the set of `consentId` which reference a particular entity only works for a universe of small consents, for very small databases.  This is a limitation of both this API and also the reference implementation.  The size of the set of consents which refer to an arbitrary entity can be quite large.

It is unspecified whether the service produces a document containing all possible such consent records in a streaming fashion or it truncates the output after some finite amount of time.  

#### Opportunities

* [ldp-paging](https://www.w3.org/TR/ldp-paging/)
* [realtime-paged-data-exchange](https://www.w3.org/2017/08/realtime-paged-data-exchange/)

### `POST /consent/revoke/{consentId}`

#### Purpose
A consent record is declared to be _revoked_.

#### Preconditions
The consent record at `{consentId}` must already exist.

#### Postconditions
Depending upon the success or failure of the operation.

##### Success
Response: `200` OK

The consent record is moved a state of _revoked_.
##### Failure
Response: `400` Invalid

The submitted document is invalid.

Response: `404` Not Found

There is no consent record at `{consentId}`.

#### Submitted Document
The submitted document must be empty.

#### Response Document
The repsonse document is empty in both success and failure.

#### Compatibility
This interface would most naturally be located at `/consent/{consentId}/revoke`.
Clients are advised that this location will be used in future versions of the API.

### `POST /consent/revokeWithArray`

#### Purpose
Revoke multiple consent records.

#### Semantic
The semantic of this API is defined as the repeated invocation of `POST /consent/revoke/{consentId}}`.

It is unspecified whether the server shall terminate processing at the first failure or shall continue on to attempt as many _sub-POST_ operations as possible.

#### Preconditions
None of the `{consentId}` may already exist.

#### Postconditions
Depending upon success or failure.

##### Complete Success
The response under complete success is the same as for the singleton `POST /consent/revoke/{consentId}`.

##### Partial Success & Partial Failure
See the subsequent section _Complete Failure_.

This API offers no way to return any fidelity into _which_ of the consent references
failed to be marked as _revoked_ in the database.
Only a coarse failure is returned as if _all_ records had failed to be written.
However some unspecified number of records may have been written as _revoked_ in the database.

##### Complete Failure
The response under complete success is the same as for the singleton `POST /consent`.

#### Submitted Document
The document shall consiste of an array of consent objects, each of which contains the following:

#### Response Document
No document is returned.

#### Compatibilities
The criticism developed against `POST /consent/createWithArray` applies here.

This interface will be removed in future versions of the API.

### `POST /consent/revokeWithList`

This call is basically the same as `POST /consent/revokeWithArray`.  It has the same semantic and the same difficulties with returning any meaningful response code.  It will be removed in future versions of the API.

It is substantially the same as _revokeWithArray_ except that a _list_ is interpreted to mean a document of [JSON Lines](https://jsonlines.org).

#### Submitted Document
The submitted document is a _list_ of `{consentId}` which are to be revoked.

#### Response Document
No document is returned.

#### Compatibility

This interface will disappear in future versions of the API.
It will disappear in the same version where `/consent/createWithArray` disappears.

### `POST /datatransfer`

#### Purpose
A new log record of a data transfer action is recorded.
 
#### Preconditions
* The `{id}` MUST NOT already exist.
* The `{consentId}` MUST already exist.

#### Unchecked
* The `{source}` is unchecked, but should reference a valid _entity_.
* The `{destination}` is unchecked, but should reference a valid _entity_.

#### Postconditions
Depending upon the success or failure.

##### Success
Response: `202` Accepted

The data transfer record has been accepted into the database.

##### Failure
Response: `400` Invalid

The submitted document is invalid.

#### Submitted Document
The submitted document shall contain the following elements:

* `{id}` required
* `{consentId}` required
* `{source}` required
* `{destination}` required
* `{attributes}` required
* `{status}` required

#### Response Document
The empty document is returned`

#### Compatibility

This interface would most naturally be located at `/consent/{consentId}/transfer`.
Clients are advised that this location will be used in future versions of the API.

### `PUT /datatransfer/{transferId}`

#### Purpose
Update an existing data transfer

Each operation completely overwrites the document that was there previously.
As such, there is no way to update only a single field at a time.

It is not clear why it would be useful to update only individual fields.
It is not clear that updating a transfer is meaningful at all.
If the transfer has occurred and has been recorded then the expectation is that it is immutable in the database.

#### Preconditions
A consent record already exists at `{consentId}`.

If supplied, the `{id}` of the document must match the `{transferId}` of the path.

#### Postconditions
Depending upon success or failure.

##### Success
Response: `202` Accepted

The consent record is overwritten with the new information.

##### Failures
Response: `400` Invalid

* The `{id}` is supplied and does not match the `{transferId}`
* The `{consentId}` is not valid for the data type.
* The `{transferId}` is not valid for the data type.

Response: `404` Not Found

No record exists at `{transferId}`

#### Submitted Document
The submitted document shall consist of the following elements:

* `{id}` required
* `{consentId}` required
* `{source}` required
* `{destination}` required
* `{attributes}` required
* `{status}` required

#### Response Document
The empty document is returned.

### `POST /datatransfer/createWithArray`

#### Purpose
Create multiple  data transfers records from the supplied document, which is an array.

#### Preconditions
None of the `{id}` may already exist.

#### Postconditions
Depending upon success or failure.

##### Complete Success
The response under complete success is the same as for the singleton `POST /consent`.

##### Partial Success & Partial Failure
See the subsequent section _Complete Failure_.

This API offers no way to return any fidelity into _which_ of the consent records failed to be added to the database.
Only a coarse failure is returned as if _all_ records had failed to be written.  However some unspecified number of records may have been written to the database.  It is unspecified whether the server shall terminate processing at the first failure or shall continue on to attempt as many _sub-POST_ operations as possible.

##### Complete Failure
The response under complete success is the same as for the singleton `POST /consent`.

#### Submitted Document
The document shall consiste of an array of consent objects, each of which contains the following:

* `{id}` required
* `{consentId}` required
* `{source}` required
* `{destination}` required
* `{attributes}` required
* `{status}` required

#### Response Document
No document is returned to the client.

#### Compatibility
The utility of this API interface is unclear.  It provides exactly the same capability as repeated calls to `POST /datatransfer`.  Clients should expect this call to disappear in future versions of the API.

The preferred approach for large batch uploads is for the client to use a _keepalive_ feature of the HTTP protocol to allow a single connection to be reused for multiple invocations of `POST /datatransfer`.

### `POST /datatransfer/createWithList`

This call is basically the same as `POST /datatransfer/createWithArray`.  It has the same semantic and the same difficulties with returning any meaningful response code.  It will be removed in future versions of the API.

It is substantially the same as _createWithArray_ except that a _list_ is interpreted to mean a document of [JSON Lines](https://jsonlines.org).

#### Submitted Document
The submitted document is a _list_ of consent record objects which are to be created.

#### Response Document
No document is returned.

#### Compatibility

This interface will disappear in future versions of the API.
It will disappear in the same version where `/datatransfer/createWithArray` disappears.

### `GET /datatransfer/{transferId}`

#### Purpose
Recover the data transfer record from its `{transferId}`.

#### Preconditions
There are no preconditions.

#### Postconditions
Depending upon the success or the failure of the recovery.

##### Success
Response: `200` OK

The data transfer record is returned.

##### Failure
Response: `404` Not Found

There is no document at the indicated `{transferId}`.

#### Submitted  Document
There is no submitted document on a `GET` request.

#### Response Document
The document returned will contain the following elements:

* `{id}`
* `{consentId}`
* `{source}`
* `{destination}`
* `{attributes}`
* `{status}`

### `GET /datatransfer/findByConsentID`

#### Purpose
Find data transfer records by which are referenced by the entity named in the query parameter `{entity}`

#### Preconditions
None.

#### Postconditions
Depending upon the success or the failure of the recovery.

##### Success
Response: `200` OK

The response document is returned.
This document contains the `{transferId}` of all data transfer records which mention the `{entity}` of the original query.

##### Failure
Response: `400` Invalid

The supplied `{entity}' is not appropriate for the data type.

#### Response Document

A single document of [JSON Lines](http://jsonlines.org/) of `{consentId}` is returned.

#### Compatibility

Of course the notion of querying for and returning the set of `transferId` which reference a particular entity only works for a universe of small data transfers, for very small databases.  This is a limitation of both this API and also the reference implementation.  The size of the set of data transfers which refer to an arbitrary entity can be quite large.

It is unspecified whether the service produces a document containing all possible such data transfer records in a streaming fashion or it truncates the output after some finite amount of time.  

#### Opportunities

* [ldp-paging](https://www.w3.org/TR/ldp-paging/)
* [realtime-paged-data-exchange](https://www.w3.org/2017/08/realtime-paged-data-exchange/)


### `POST /subscription`

#### Purpose
Subscribe to notifications for all events related to a consent record.

#### Status
This API is not part of this the reference implementation.

#### Postcondition
Response: `400` Invalid

This interface is not implemented and will not be part of any future interface.
Notifications to data subjects will occur through a separate system.

### Submitted Document
The posted document is (was) expected to have these fields

* `{id}`
* `{consentId}`
* `{entity}`
* `{subscriptionDate}`
* `{email}`
* `{status}`

### `GET /subscription/findByEntity`

#### Purpose
Return subscriptions for an entity.

#### Status
This API is not part of this the reference implementation.

#### Postcondition
Response: `400` Invalid

This interface is not available.

### `GET /subscription/{subscriptionId}`

#### Purpose
Find subscription by subscription ID

#### Status
This API is not part of this the reference implementation.

#### Postcondition
Response: `400` Invalid

This interface is not available.

### `PUT /subscription/{subscriptionId}`

#### Purpose
Update the subscription named subscription ID

#### Status
This API is not part of this the reference implementation.

#### Postcondition
Response: `400` Invalid

This interface is not available.

### `DELETE /subscription/{subscriptionId}`

#### Purpose
Delete the subscription named by the subscription ID.

#### Status
This API is not part of this the reference implementation.

#### Postcondition
Response: `400` Invalid

This interface is not available.
