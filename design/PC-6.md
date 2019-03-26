| Name | PC-6 |
| --- | --- |
| Status | Not yet presented |
| Proposed | 2019-03-27 |
| Debated |  |
| Adopted |  |
| Title | Tree-track development |
| Synopsis | Separate concerns of Chaincode from Northside and Southside |

# Proposal

Shall the concerns of the Chaincode be separated from that of the Northside and Southside?

# Background

From [PC-1](https://github.com/InteractiveAdvertisingBureau/PrivacyChain/blob/master/design/PC-1.md)
* Northside concerns are the design of the REST API
* Southside concerns are the operability of the Hyperledger Fabric network, the Gossip protocol.

# Rationale

The Chaincode ("smart contract") is a separable part of the system.  It carries separate concerns.

Currently the smart contract operates at the level of _get_ and _set_ with a transation logging feature in the _history_.  There is no provision for relating these operations to the notions of _consent_, _purpose_ or enabling statute, however that may be defined.

# Scope

[acxiom_cc.go](https://github.com/InteractiveAdvertisingBureau/PrivacyChain/blob/master/docker/chaincode/acxiom_cc/acxiom_cc.go)

# Span

To address this, undertake the following:

## Document
* The existing capabilities of [acxiom_cc.go](https://github.com/InteractiveAdvertisingBureau/PrivacyChain/blob/master/docker/chaincode/acxiom_cc/acxiom_cc.go)
* The existing expectations, dependencies & invariants, being at least
** Pointwise CRUD
** Scanning & indexing
** JSON payload synthesis, _e.g._ in the `history` 

## Propose
* A new, vendor-independent, name
* Interface evolution roadmap
* A schema evolution roadmap

## Produce
* A prototype
