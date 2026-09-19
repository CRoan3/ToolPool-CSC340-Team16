# Requirements – ToolPool

**Project Name:** ToolPool  
**Team:** Team 16 — Christopher Roan, Nobert Akas  
**Course:** CSC 340  
**Version:** 1.0  
**Date:** 2026-09-16

---

## 1. Overview

**Vision.** ToolPool is a peer-to-peer tool rental platform for people who need temporary access to tools for construction, home improvement, and other projects. It provides users with a cheaper way to access tools while allowing users to rent out tools they already own.

**Glossary**

- **User:** A person with a ToolPool account who can both list tools for rent and rent tools from other users.
- **Tool Listing:** A tool that a user has posted on ToolPool for other users to rent.
- **Renter:** The user renting a tool in a particular rental transaction.
- **Owner:** The user who listed and owns the tool in a particular rental transaction.
- **Rental:** A temporary agreement between two users for the use of a listed tool.
- **SysAdmin:** A system administrator who monitors users and rental activity.

**Primary Users / Roles.**

- **User** — Finds and rents tools from other users or lists their own tools for other users to rent.
- **SysAdmin** — Monitors users and rental activity and facilitates the operation of the platform.

**Scope (this semester).**

- User accounts and profiles
- Tool listings
- Tool searching and filtering
- Tool availability calendar
- Tool booking and rental requests
- Pickup and drop-off logistics
- Damage deposits
- Ratings and reviews
- Rental payments

**Out of scope (deferred).**

- Tool pictures
- Tool usage instructions
- Messaging system
- User collaboration
- Open tool requests

---

## 2. Functional Requirements (User Stories)

### 2.1 User Stories

- **US-1 — Create an Account**  
  _Story:_ As a user, I want to create an account so that I can rent and list tools.  
  _Acceptance:_

  ```gherkin
  Scenario: Create an account
    Given the user does not have an account
    When the user provides the required account information
    Then the system creates an account for the user
  ```

- **US-2 — View Available Tools**  
  _Story:_ As a user, I want to view available tools so that I can find a tool to rent.  
  _Acceptance:_

  ```gherkin
  Scenario: View available tools
    Given tools have been listed and are available for rent
    When the user views available tools
    Then the system displays the available tool listings
  ```

- **US-3 — List a Tool**  
  _Story:_ As a user, I want to list my tool so that other users can rent it.  
  _Acceptance:_

  ```gherkin
  Scenario: List a tool for rent
    Given the user has an account
    When the user submits the required information for a tool
    Then the system creates a rental listing for the tool
  ```

- **US-4 — View Reviews**  
  _Story:_ As a user, I want to view reviews so that I can make an informed rental decision.  
  _Acceptance:_
  ```gherkin
  Scenario: View user reviews
    Given a user has received reviews
    When another user views that user's profile
    Then the system displays the reviews associated with that user
  ```
### 3. User Stories 

1. As a system admin, I want to be able to remove profiles that are suspicious, so that I can help maintain a safe and trustworthy platform.

2. As a user, I want to be able to quickly book a nearby tool, so that I can complete my job quickly.

3. As a user, I want to be able to unlist my tool, so that I can easily stop renting out my tool when I want to.

4. As a user, I want to be able to create a user account, so that I can rent out tools.

5. As a user, I want to be able to view a list of available tools, so that I can choose a tool to rent.

6. As a system admin, I want to be able to see a list of reported users, so that I can review them and remove them if necessary.

7. As a user, I want to be able to view another user's profile, so that I can contact them to rent out a tool.


### 4. Non-Functional Requirements
Performance - ToolPool should respond to inputs in less than 5 seconds.
Availability/Reliability - ToolPool should be available to people everywhere who create accounts for it. 
Security/Privacy - User data should be protected at all times.
Usability - New users should be able to create new accounts in 10 minutes or less. 

### 5. Assumptions, Constraints, and Policies
1. Users are expected to create an account before using ToolPool and have at least a phone number or email.
2. The project must be completed by the end of the fall 2026 UNCG semester.

### 6. Change Management
1. Changes and issues should be handled in GitHub.
2. Any new features should be added to this file.
3. At least one other person should review new changes.
