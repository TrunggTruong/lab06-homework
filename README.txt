STUDENT INFORMATION:
Name: Trương Lê Hiếu Trung			
Student ID: ITITWE21091
Class: Web Application Development_S1_2025-26_G01_lab02


COMPLETED EXERCISES:
[x] Exercise 1: Database & User Model
[x] Exercise 2: User Model & DAO
[x] Exercise 3: Login/Logout Controllers
[x] Exercise 4: Views & Dashboard
[x] Exercise 5: Authentication Filter
[x] Exercise 6: Admin Authorization Filter
[x] Exercise 7: Role-Based UI
[ ] Exercise 8: Change Password

AUTHENTICATION COMPONENTS:
- Models: User.java
- DAOs: UserDAO.java
- Controllers: LoginController.java, LogoutController.java, DashboardController.java
- Filters: AuthFilter.java, AdminFilter.java
- Views: login.jsp, dashboard.jsp, updated student-list.jsp

TEST CREDENTIALS:
Admin:
- Username: admin
- Password: password123

Regular User:
- Username: john
- Password: password123

FEATURES IMPLEMENTED:
- User authentication with BCrypt
- Session management
- Login/Logout functionality
- Dashboard with statistics
- Authentication filter for protected pages
- Admin authorization filter
- Role-based UI elements
- Password security

SECURITY MEASURES:
- BCrypt password hashing
- Session regeneration after login
- Session timeout (30 minutes)
- SQL injection prevention (PreparedStatement)
- Input validation
- XSS prevention (JSTL escaping)

KNOWN ISSUES:
Some error messages are still minimal or not fully styled.

Direct URL access is blocked by filters, but the message display is not consistent across all pages.
No protection for actions like delete or update.

BONUS FEATURES:
- [List any bonus features implemented]

TIME SPENT: 
Initial project structure and MVC setup: ~2 hours

Login + session management: ~1 hour

Role-based authorization + filter logic: ~1.5 hours

UI updates, JSTL conditions, and navigation bar: ~1 hour

Testing and fixing restrictions: ~1 hour

TESTING NOTES:
Tested login with both admin and user accounts to verify session and role handling.

Confirmed AdminFilter blocks unauthorized access through both buttons and direct URLs.

Verified that admin can add/edit/delete, while regular users cannot see these actions.

Checked error messages appear correctly on restricted actions.

Tested logout behavior and session cleanup across multiple browsers.