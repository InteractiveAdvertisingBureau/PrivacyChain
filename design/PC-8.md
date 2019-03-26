| Name | PC-8 |
| --- | --- |
| Status | Not yet presented |
| Proposed | 2019-03-27 |
| Debated |  |
| Adopted | |
| Title | Certificate Authority and Key Distribution |
| Synopsis | Define how keys and certificates will be managed |

# Proposal

A manager for the certificates, across their lifetimes is necessary.

Assess whether [Athenz](https://github.com/yahoo/athenz) is relevant.

# Background

<quote>Athenz is a set of services and libraries supporting service authentication and role-based authorization (RBAC) for provisioning and configuration (centralized authorization) use cases as well as serving/runtime (decentralized authorization) use cases. Athenz authorization system utilizes x.509 certificates and two types of tokens: Principal Tokens (N-Tokens) and RoleTokens (Z-Tokens). The use of x.509 certificates is strongly recommended over tokens. The name "Athenz" is derived from "AuthNZ" (N for authentication and Z for authorization).</quote>

# Rationale

Whereas key distribution and certificate management is _the_ key skill in operating these systems, a definition of the processes for managing the key and certificate life cycle is necessary.

# Scope

Any certificate or key distribution issues, including revocation.

# Span

## Document
* How it is expected to be done in Hyperledger Fabric

## Propose
* A reasonable approach for the PrivacyChain Consortium I.

# Produce
* An _operations_  statement (a.k.a. a "runbook").
* A _governance_ statement.
* A manager (a software service artifact).
