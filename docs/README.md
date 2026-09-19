# ToolPool

## Team Members

- Christopher Roan
- Nobert Akas

## Description

ToolPool is a platform that allows users to rent and lend tools within their community. This application is designed to make tools and equipment more accessible without requiring users to purchase tools they may only need temporarily. Users who own tools can list them for rent, and other users can search for available tools, view tool and tool owner information, rent tools, and make payments through the platform. Users can communicate with each other about picking up and returning tools. 

## App Functions

### 1. User

1. Create and manage profile
2. Browse available tools
3. Search and filter tool listings
4. View tool details
5. List a tool for rent
6. Edit or remove owned tool listings
7. Rent tools from other users
8. Set availability for owned tools
9. Make payments for rentals
10. Receive payments for rented-out tools
11. View rental history
12. View other user profiles
13. Leave reviews after completed rentals
14. Message users
15. Change user area

### 2. System Administrator

1. Manage user accounts
2. Manage tool listings
3. Review reported users or listings
4. Remove inappropriate or fraudulent listings
5. Maintain platform data and system functionality
6. View user messages
7. View user transactions

### 3. User Stories 

1. As a system admin, I want to be able to remove profiles that are suspicious, so that I can help maintain a safe and trustworthy platform.
    Ex: Remove a suspicious profile
    Given I am logged in as a system admin
    When I identify a suspicious user profile
    And I choose to remove the profile
    Then the profile should be removed from the platform

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
