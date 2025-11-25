package com.student.controller;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {

    // DAO object for database operations
    private StudentDAO studentDAO;

    @Override
    public void init() {
        // Initialize DAO when servlet starts
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list"; // default action
        }

        switch (action) {
            case "new":
                showNewForm(request, response);
                break;

            case "edit":
                showEditForm(request, response);
                break;

            case "delete":
                deleteStudent(request, response);
                break;

            case "search":     // <-- Search action added here
                searchStudents(request, response);
                break;
                
            case "sort":
                sortStudents(request, response);
                 break;

            case "filter":
                filterStudents(request, response);
                break;
            default:
                listStudents(request, response);
                break;
                
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;

            case "update":
                updateStudent(request, response);
                break;
        }
    }

    // --------------------------------------------------------------
    // LIST ALL STUDENTS
    // --------------------------------------------------------------
  private void listStudents(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    // 1. Get page parameter
            String pageParam = request.getParameter("page");
            int currentPage = 1;

            if (pageParam != null) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }

            // 2. Records per page
            int recordsPerPage = 10;

            // 3. Calculate offset
            int offset = (currentPage - 1) * recordsPerPage;

            // 4. Get paginated results
            List<Student> students = studentDAO.getStudentsPaginated(offset, recordsPerPage);
            int totalRecords = studentDAO.getTotalStudents();

            // 5. Calculate total pages
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

            // 6. Fix edge cases
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages) currentPage = totalPages;

            // 7. Set attributes
            request.setAttribute("students", students);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

            // 8. Forward once (only 1 forward allowed)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
            dispatcher.forward(request, response);
        }

            // Handle sorting students
            private void sortStudents(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            // Get sorting parameters from request
                String sortBy = request.getParameter("sortBy");
                String order = request.getParameter("order");

            // Call DAO to get sorted list
            StudentDAO dao = new StudentDAO();
            List<Student> students = dao.getStudentsSorted(sortBy, order);

            // Store sorting parameters so JSP can show arrows ▲▼
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("order", order);

            // Send result to view
            request.setAttribute("students", students);
            request.getRequestDispatcher("/views/student-list.jsp").forward(request, response);
}
// EXERCISE 7: FILTER STUDENTS BY MAJOR
    private void filterStudents(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    // Get the 'major' parameter from dropdown
    String major = request.getParameter("major");

    List<Student> students;

    // If major is empty → user selected "All Majors"
    // In this case we MUST return the full student list
    if (major == null || major.trim().isEmpty()) {

        // Return all students (NO filter)
        students = studentDAO.getAllStudents();

    } else {

        // Otherwise filter by the selected major
        students = studentDAO.getStudentsByMajor(major);
    }

    // Save result and selected option to the JSP
    request.setAttribute("students", students);
    request.setAttribute("selectedMajor", major);

    // Forward to the correct JSP file
    request.getRequestDispatcher("/views/student-list.jsp").forward(request, response);
}



    // --------------------------------------------------------------
    // SEARCH STUDENTS 
    // --------------------------------------------------------------
    private void searchStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get keyword from request
        String keyword = request.getParameter("keyword");

        List<Student> students;

        // If keyword empty → show all students
        if (keyword == null || keyword.trim().isEmpty()) {
            students = studentDAO.getAllStudents();
            keyword = ""; // keep empty string for JSP
        } else {
            // Perform search using DAO
            students = studentDAO.searchStudents(keyword);
        }

        // Pass data to JSP
        request.setAttribute("students", students);
        request.setAttribute("keyword", keyword);

        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // --------------------------------------------------------------
    // SHOW NEW FORM
    // --------------------------------------------------------------
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    // --------------------------------------------------------------
    // SHOW EDIT FORM
    // --------------------------------------------------------------
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);

        request.setAttribute("student", existingStudent);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    // --------------------------------------------------------------
    // INSERT NEW STUDENT
    // --------------------------------------------------------------
  private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    // 1. Get parameters and create Student object
    String code = request.getParameter("studentCode");
    String name = request.getParameter("fullName");
    String email = request.getParameter("email");
    String major = request.getParameter("major");
    
    Student student = new Student(code, name, email, major);
    
    // 2. Validate
    if (!validateStudent(student, request)) {
        // Set student as attribute (to preserve entered data)
        request.setAttribute("student", student);
        
      request.setAttribute("isInsertError", true);
        // Forward back to form
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
        return; // STOP here
    }
    
    // 3. If valid, proceed with insert
    if (studentDAO.addStudent(student)) {
        response.sendRedirect("student?action=list&message=Added successfully");
    } else {
        response.sendRedirect("student?action=list&error=Failed to add student");
    }
}




    // --------------------------------------------------------------
    // UPDATE STUDENT
    // --------------------------------------------------------------
   private void updateStudent(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    // 1. Get parameters and create Student object
    int id = Integer.parseInt(request.getParameter("id"));
    String code = request.getParameter("studentCode");
    String name = request.getParameter("fullName");
    String email = request.getParameter("email");
    String major = request.getParameter("major");
    
    Student student = new Student(id, code, name, email, major);
    
    // 2. Validate
    if (!validateStudent(student, request)) {
        // Set student as attribute (to preserve entered data)
        request.setAttribute("student", student);
        // Forward back to form
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
        return; // STOP here
    }
    
    // 3. If valid, proceed with update
    if (studentDAO.updateStudent(student)) {
        response.sendRedirect("student?action=list&message=Updated successfully");
    } else {
        response.sendRedirect("student?action=list&error=Failed to update student");
    }
}



    // --------------------------------------------------------------
    // DELETE STUDENT
    // --------------------------------------------------------------
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
    // ===============================
// 6.1 SERVER-SIDE VALIDATION
// ===============================
    private boolean validateStudent(Student student, HttpServletRequest request) {
    boolean isValid = true;
    
    // Define patterns
    String codePattern = "[A-Z]{2}[0-9]{3,}";
    String emailPattern = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$"; 

    // -------- Validate Student Code --------
    String code = student.getStudentCode();
    if (code == null || code.trim().isEmpty()) {
        request.setAttribute("errorCode", "Student code is required");
        isValid = false;
    } else if (!code.matches(codePattern)) {
        request.setAttribute("errorCode", "Invalid code format. Use 2 uppercase letters + 3+ digits (e.g., SV001)");
        isValid = false;
    }
    
    // -------- Validate Full Name --------
    String name = student.getFullName();
    if (name == null || name.trim().isEmpty()) {
        request.setAttribute("errorName", "Full name is required");
        isValid = false;
    } else if (name.trim().length() < 2) {
        request.setAttribute("errorName", "Full name must be at least 2 characters");
        isValid = false;
    }
    
    // -------- Validate Email (optional field) --------
    String email = student.getEmail();
    if (email != null && !email.trim().isEmpty()) {
        if (!email.matches(emailPattern)) {
            request.setAttribute("errorEmail", "Invalid email format");
            isValid = false;
        }
    }
    
    // -------- Validate Major --------
    String major = student.getMajor();
    if (major == null || major.trim().isEmpty()) {
        request.setAttribute("errorMajor", "Major is required");
        isValid = false;
    }
    
    return isValid;
}
    
}
