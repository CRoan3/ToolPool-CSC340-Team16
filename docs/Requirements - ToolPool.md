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
