Name: PC-4
Status: Not yet presented
Proposed: 2019-02-27
Debated: (no date)
Approved: (no date)
Title: Use Rabin fingerprints to hash identifiers
Synopsis: Use rabinpoly(identifier) to reduce any identifier to 64-bit number

# Proposal

Use `rabinpoly(identifier)` as primary key for the Southbound database.

# Rationale

Whereas the definition of an <em>identifier</em> is unspecified in the PrivacyChain product specification, and whereas identifiers vary widely across the IAB service footprint, there is a need for a method to reduce any naming system into a fixed-width primary key which is suitable for use in the Hyperledger Fabric database.

An <em>identifier</em> could be any of an IDFA, GPSAID, a publisher cookie, a publisher login token or other unique representative.

```
let name = ...any name at all...
let hash = rabinpoly(name)
```

The operations in chaincode are thus
```
let payload, err = get(hash)
let _, err = set(hash, new_payload)
```

# References

* [Rabin Fingerprint](https://en.wikipedia.org/wiki/Rabin_fingerprint)
* [librabinpoly](https://github.com/stevegt/librabinpoly), includes a detailed [history](https://github.com/stevegt/librabinpoly#History).
* [rabinpoly.h](https://github.com/OkCupid/sfslite/blob/master/crypt/rabinpoly.h) in [OkCubpid/sfslite](https://github.com/OkCupid/sfslite)
* [org.cdlib.rabinpoly.RabinPoly](https://gitlab.com/egh/rabinpoly-java/blob/master/src/main/java/org/cdlib/rabinpoly/RabinPoly.java) in [rabinpoly-java](https://gitlab.com/egh/rabinpoly-java)
