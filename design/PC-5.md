| Name | PC-5 |
| --- | --- |
| Status | Not yet presented |
| Proposed | 2019-03-27 |
| Debated |  |
| Adopted | |
| Title | Schema manager & record versioning |
| Synopsis | Records will be in different formats over the lifetime of the database |

# Proposal

A schema dictionary will manage the records in the database.
The schema of the record in the database will be assessed according to the following rule:

1. Examine the byte at index 0 (the first byte)
2. Dispatch according to the published dictionary.

| Value | Schema |
| --- | --- |
| 0x00 | The payload is uninterpreted octets, a.k.a. `std::byte` |
| 0x01 | The payload is unspecified octets encoded in base64, per [RFC4648](https://tools.ietf.org/html/rfc4648) |
| 0x02 | The payload is JSON of unspecified schema, [JSON](https://www.json.org/)
| 0x03 | The payload is TCF v1.1 as practiced 2019-03 [TCF](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework)
| ... | ... |
| 0xff | The second bytes (index 1) shall be consulted, and so on |

This table will be expanded as necessary.  Candidates for such expansion are CBOR [CBOR](https://cbor.io/), [RFC7049](https://tools.ietf.org/html/rfc7049) and _Structured Headers for HTTP_ [draft-ietf-httpbis-header-structure-latest](https://httpwg.org/http-extensions/draft-ietf-httpbis-header-structure.html).

# Rationale

Whereas the PrivacyChain database will be long-lived, optimistically, as long as the lifetime of a natural person, it is necessary for the Northside system to be able to interpret "old records."  As well as new forms of consent are developed, these will be recorded in record formats which are as-yet unknown.  Saying "it's just JSON, handle it in the application" is insufficient.

# Scope

* The definition of the table.
* The definition of admission control to the table.

# Span
To address this, undertake the following:

## Document
* Existing practice.

## Propose
* The definition of the table.
* The definition of admission control to the table.
* The definition of deprection on the table.
## Produce
* Prototoype codec 

# References

* [RFC4648](https://tools.ietf.org/html/rfc4648) - _The Base16, Base32, and Base64 Data Encodings_
* [JSON](https://www.json.org/) - _JavaScript Object Notation_
* [CBOR](https://cbor.io/) - _Concise Binary Object Representation_
* [RFC7049](https://tools.ietf.org/html/rfc7049) - _Concise Binary Object Representation_
* [draft-ietf-httpbis-header-structure-latest](https://httpwg.org/http-extensions/draft-ietf-httpbis-header-structure.html) - _Structured Headers for HTTP_, 2019-03-11
* [GDPR Transparency and Consent Framework](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework)
* [Consent String and Vendor List Format: Transparency & Consent Framework](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md)
