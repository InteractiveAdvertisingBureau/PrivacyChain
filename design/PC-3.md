Name: PC-3
Status: Unsupported
Proposed: 2019-02-27
Debated: 2019-02-27
Approved: Deferred
Title: The database payload format shall be IAB TCF
Synopsis: Use IAB TCF encoded cookie header format for database payloads

# Proposal

Shall the database record format be IAB TCF?

# Concept

* Use IAB TCF, encoded as a string from TCF v1.1 or successor.
* Encoded in the cookie header.

# Rationale

Whereas the Southbound interface merely offers CRUD on "a database," the question at hand is <em>but what does it write?</em>.

Positives: TCF is well defined, has production code to support it; is defended at Layers 8 & 9.
Negatives: TCF is large, redundant (full of zeros) and will get far far heavier in future TCF multidimensional, multiregional versions; expresses consents between consumer principals and corporate entities controlled by IAB.
Resolution: defer back to product to define "what is a consent?"

# References

* [GDPR Transparency and Consent Framework](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework)
* [Consent String and Vendor List Format: Transparency & Consent Framework](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md)
