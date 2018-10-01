![iab tech lab](https://user-images.githubusercontent.com/19175352/38649177-0d37d17c-3daa-11e8-8934-f0fb47919716.png)

# PrivacyChain

## Introduction

The PrivacyChain is a distributed, blockchain platform , based on a shared, immutable, distributed ledger. This ledger ensures that participants in a PrivacyChain have a single, consistent, up-to-date view to a consumers opt-ins or opt-outs, something that is more difficult to accomplish with traditional technologies. As a result, PrivacyChain helps publishers and advertisers build more trusting relationships with their customers.  It also provides companies with a standardized consent management solution which speeds and simplifies deployment for all their partners in the data supply chain.  And because of this consistency and ease of deployment, PrivacyChain simplifies companies' ability to prove that they are complying with numerous consumer privacy regulations worldwide, including the California Consumer Privacy Act, General Data Protection Regulation, and the European Privacy Directive, as well as a company's own privacy policies.

A demo is available at http://tools.iabtechlab.com.  Companies interested in testing the specification can build applications that make calls into the testbed and see how they are handled and propagated across the blockchain in a standard implementation.

### Entities
Following entities are defined in privacychain:
1. Data Collector – For example, Brand, Publisher, Data Source of consumer data
2. Data Buyer – For example, Brand, Ad Agency, Data Aggregator
3. Advertiser – For example, Brand, Ad Agency
4. Individual – Consumer, user

### Use Case 1 – Consent Collection

| Title | Data Collector captures consent when an individual's personal data and opt-in preference is being collected |
| --- | --- |
| Description | <ol><li>Individual signs up as a member at the data collector1 ("entity") website</li><li> Website displays privacy messaging and prompts user to opt-in to accept privacy terms and condition</li><li>The website requests user to provide consent on the use of the individuals data for these purposes:</li><ul><li>For the entity to provide basic services</li><li>For first party site personalization</li><li>For first party marketing purpose</li><li>For sharing with third party for market purpose</li></ul></ol> |
| Post condition | Individuals consent along with its meta data2 is captured in PrivacyChain |

### Use Case 2 – Data Movement Tracking

| Title | Advertiser tracks audience data movement when personal data is transferred to a third party   |
| --- | --- |
| Description |<ol><li>Advertiser creates audience segment for advertising campaign</li><li>Advertiser ensure that audience has provided consent for their data to be used for marketing purpose</li><li>Advertiser transfers audience segment to third party for campaign execution</li><li>Third party received audience segment</li></ol> |
| Post condition | <ol><li>Transfer of individual's consent along with its meta data to the third party is captured in PrivacyChain</li><li>Third party receipt of individual's consent along with its meta data is captured in PrivacyChain </li></ol> |

### Use Case 3 – Data Movement Tracking

| Title | Third party tracks audience data movement when data is transferred to another third party. All third parties delete audience data post campaign |
| --- | --- |
| Description |<ol><li>Third party transfers audience segment to another third party5 for campaign execution</li><li>Third party received audience segment</li><li>All third parties delete audience data post campaign</li></ol> | 
| Post condition | <ol><li>Transfer of individual's consent along with its meta data to the third party is captured in PrivacyChain</li><li>Third party receipt of data and individual's consent along with its meta data is captured in PrivacyChain</li><li>Third parties deletion of individual's data post campaign is captured in PrivacyChain</li></ol> |

### Use Case 4 – Consent Collection

| Title | Data Seller captures individual's consent when individual's personal data is being collected   |
| --- | --- |
| Description |<ol><li>Individual signs up to use services at a data collector7 ('entity') site</li><li>Website displays privacy messaging and prompts individual to opt-in to accept privacy terms and condition</li><li>Privacy terms and condition and opt-in include individual personal data collection:</li><ul><li>For the entity to provide basic services</li><li>For first party site personalization</li><li>For first party to share data with third party part of data sales</li></ul></ol> |
| Post condition | Individual's consent along with its meta data8 is captured in PrivacyChain |

### Use Case 5 – Data Movement Tracking

| Title | Data Seller tracks data movement when individual's data is sold and transferred to third party   |
| --- | --- |
| Description |<ol><li>Data Seller sold opt-in individual data to a third party </li><li>Third party received data from Data Seller </li></ol>| 
| Post condition |<ol><li> Transfer of the individual's consent along with its meta data to the third party is captured in PrivacyChain. </li><li> Third party receipt of data and individual's consent along with its meta data is captured in PrivacyChain </li></ol>|

### Use Case 6 – Individual Inquiry

| Title | Individual inquires consent status and data movement |
| --- | --- |
| Description |<ol><li>User login to brand website he/she signed up previously</li><li>User inquires attribute(s) he/she has provided data to the brand</li><li>User inquires movement of his/her data outside of the brand </li></ol>| 
| Post condition | The brand's website displays a list of user's attribute(s) along with meta data:<ul><li>Consent status</li><li>Expiry date</li><li>Usage/purpose</li><li>he brand's website displays a list of third party destinations in which user's data has been shared with/transferred to </li></ul> |

### Use Case 7 – Data Propagation

| Title | Individual manages his/her consent and updated consent propagates to downstream entities |
| --- | --- |
| Description |<ol><li> User login to brand website he/she signed up previously</li><li>User inquires attribute(s) he/she has provided data to the brand</li><li>User revoke consent to share data with third party </li></ol>|
| Post condition |<ol><li>The action of revoking consent to share data with third party is captured in PrivacyChain</li><li>PrivacyChain triggers consent revocation notification to all third-party entities that had received the individual's consent previously </li></ol>|

### Use Case 8 – Auditing

| Title | Regulator auditing Data Collector and Data Processor's privacy practice |
| --- | --- |
| Description |<ol><li> Regulator access PrivacyChain</li><li> Regulator retrieve audit trail of data collection, data movement with consent along with the metadata for a particular Data Collector/Processor</li></ol>|
| Post condition |<ol><li>PrivacyChain supports audit trail data extraction</li><li> Regulator determines compliance </li></ol>|

### Use Case 9 – External Governance and Monitoring

| Title | Regulatory authority and consumer advocacy group monitors the integrity of the consortium |
| --- | --- |
| Description |<ol><li> Regulatory authority and consumer advocacy group request setting up and running PrivacyChain nodes</li><li>PrivacyChain consortium approves request via PrivacyChain governance process</li><li>Regulatory authority and consumer advocacy group follow provisioning instructions</li><li>PrivacyChain provision regulatory authority and consumer advocacy group within the network </li></ol>|
| Post condition |<ol><li> Regulatory authority and consumer advocacy group each runs a node within PrivacyChain</li><li>Regulatory authority and consumer advocacy group access data within the ledger</li></ol> |

### API Calls

API specification can be found in Swaggerhub repository - https://iabtechlab.com/privacychain/api-docs

| **Item #** | **Priority** | **Endpoint** | **Parameters** | **Method** | **Notes** |
| --- | --- | --- | --- | --- | --- |
| CT-01 | MVP | /consent | {id}{consentType}{entity}{expires}{attributes}{status} | POSTPUT | Add a new consent record to the chainUpdate an existing consent |
| CT-02 | V1.0 | /consent/createWithArray | {id}{consentType}{entity}{expires}{attributes}{status} | POST | Create multiple consents with given input array |
| CT-03 | V1.0 | /consent/createWithList | {id}{consentType}{entity}{expires}{attributes}{status} | POST | Create multiple consents with input list |
| CT-04 | MVP | /consent/findIdsByEntity |   | GET | Find consent IDs by entity |
| CT-05 | MVP | /consent/{consentId} | {id}{consentType}{entity}{expires}{attributes}{status} | GET | Find consent by consent ID |
| CT-06 | V1.0 | /consent/revokeWithArray |   | POST | Revoke multiple consent records |
| CT-07 | MVP | /consent/revoke/{consentId} |   | POST | Revoke single consent record |
| DT-01 | MVP | /datatransfer | {id}{consentId}{source}{destination}{attributes}{status} | POSTPUT | Log data transfer actionUpdate an existing data transfer |
| DT-02 | V1.0 | /datatransfer/createWithArray | {id}{consentId}{source}{destination}{attributes}{status} | POST | Create data transfers with given input array |
| DT-03 | V1.0 | /datatransfer/createWithList | {id}{consentId}{source}{destination}{attributes}{status} | POST | Create data transfer with given input list |
| DT-04 | MVP | /datatransfer/findByConsentID | {id}{consentId}{source}{destination}{attributes}{status} | GET | Find data transfers by consent ID |
| DT-05 | MVP | /datatransfer/{datatransferId} | {id}{consentId}{source}{destination}{attributes}{status} | GET | Find data transfer by data transfer ID |
| SB-01 | V1.0 | /subscription | {id}{consentId}{entity}{subscriptionDate}{email}{status} | POST | Subscribe to notifications for all events related to a consent record |
| SB-02 | V1.0 | /subscription/findByEntity |   | GET | Return subscriptions for an entity |
| SB-03 | V1.0 | /subscription/{subscriptionId} |   | GETDELETE | Find subscription by subscription IDDelete subscription by subscription ID |

## About IAB Tech Lab  

The IAB Technology Laboratory (Tech Lab) is a non-profit research and development consortium that produces and provides standards, software, and services to drive growth of an effective and sustainable global digital media ecosystem. Comprised of digital publishers and ad technology firms, as well as marketers, agencies, and other companies with interests in the interactive marketing arena, IAB Tech Lab aims to enable brand and media growth via a transparent, safe, effective supply chain, simpler and more consistent measurement, and better advertising experiences for consumers, with a focus on mobile and TV/digital video channel enablement. The IAB Tech Lab portfolio includes the DigiTrust real-time standardized identity service designed to improve the digital experience for consumers, publishers, advertisers, and third-party platforms. Board members include AppNexus, ExtremeReach, Google, GroupM, Hearst Digital Media, Integral Ad Science, Index Exchange, LinkedIn, MediaMath, Microsoft, Moat, Pandora, PubMatic, Quantcast, Telaria, The Trade Desk, and Yahoo! Japan. Established in 2014, the IAB Tech Lab is headquartered in New York City with an office in San Francisco and representation in Seattle and London.

Learn more about IAB Tech Lab here: [https://www.iabtechlab.com/](https://www.iabtechlab.com/)

## Disclaimer

THE STANDARDS, THE SPECIFICATIONS, THE MEASUREMENT GUIDELINES, AND ANY OTHER MATERIALS OR SERVICES PROVIDED TO OR USED BY YOU HEREUNDER (THE "PRODUCTS AND SERVICES") ARE PROVIDED "AS IS" AND "AS AVAILABLE," AND IAB TECHNOLOGY LABORATORY, INC. ("TECH LAB") MAKES NO WARRANTY WITH RESPECT TO THE SAME AND HEREBY DISCLAIMS ANY AND ALL EXPRESS, IMPLIED, OR STATUTORY WARRANTIES, INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AVAILABILITY, ERROR-FREE OR UNINTERRUPTED OPERATION, AND ANY WARRANTIES ARISING FROM A COURSE OF DEALING, COURSE OF PERFORMANCE, OR USAGE OF TRADE. TO THE EXTENT THAT TECH LAB MAY NOT AS A MATTER OF APPLICABLE LAW DISCLAIM ANY IMPLIED WARRANTY, THE SCOPE AND DURATION OF SUCH WARRANTY WILL BE THE MINIMUM PERMITTED UNDER SUCH LAW. THE PRODUCTS AND SERVICES DO NOT CONSTITUTE BUSINESS OR LEGAL ADVICE. TECH LAB DOES NOT WARRANT THAT THE PRODUCTS AND SERVICES PROVIDED TO OR USED BY YOU HEREUNDER SHALL CAUSE YOU AND/OR YOUR PRODUCTS OR SERVICES TO BE IN COMPLIANCE WITH ANY APPLICABLE LAWS, REGULATIONS, OR SELF-REGULATORY FRAMEWORKS, AND YOU ARE SOLELY RESPONSIBLE FOR COMPLIANCE WITH THE SAME, INCLUDING, BUT NOT LIMITED TO, DATA PROTECTION LAWS, SUCH AS THE PERSONAL INFORMATION PROTECTION AND ELECTRONIC DOCUMENTS ACT (CANADA), THE DATA PROTECTION DIRECTIVE (EU), THE E-PRIVACY DIRECTIVE (EU), THE GENERAL DATA PROTECTION REGULATION (EU), AND THE E-PRIVACY REGULATION (EU) AS AND WHEN THEY BECOME EFFECTIVE.
