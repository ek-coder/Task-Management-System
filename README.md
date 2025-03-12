# 📝 Task Management System

A simple and efficient **Task Management System** that allows employers to assign tasks to employees, track progress, and manage work efficiently. Think of it as a **digital to-do list**, but for teams—where tasks are assigned, updated, and marked as done in a streamlined way.

---

## 🚀 Features

- **Employer Dashboard**: Employers can log in, assign tasks, and track employee progress.
- **Employee Panel**: Employees can log in, view assigned tasks, add comments, and mark them as completed.
- **User Authentication**: Secure login system for both employers and employees.
- **Task Status Updates**: Employees can mark tasks as "Done" once completed.
- **Database Integration**: Stores user credentials and task data in **MySQL**.
- **MySQL Connector**: Uses **MySQL Connector JAR** for database connectivity.
- **User-Friendly Interface**: Simple UI for easy navigation.

---

## 🛠️ Tech Stack

- **Backend**: Java (Servlets, JSP)  
- **Frontend**: HTML, CSS, JavaScript  
- **Database**: MySQL  
- **Database Connector**: MySQL Connector JAR (Version: `mysql-connector-java-9.0.1.jar`)  
- **Server**: Apache Tomcat  

---

## 🎯 How to Run

### ✅ **1. Clone the Repository**
    ```sh
    git clone https://github.com/your-username/Task-Management-System.git
    cd Task-Management-System
###✅ **2. Set Up the Database**
  - Create a MySQL database:
    ```sh
    CREATE DATABASE task_management;
  
  - Configure Database Connection
For Java Servlet & JSP Project, update DBConnection.java:
    ```sh
    public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/task_management";
    private static final String USER = "root";
    private static final String PASSWORD = "yourpassword";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    }


###✅ **4. Add MySQL Connector JAR**
If using manual JAR addition, place mysql-connector-java-9.0.1.jar inside the lib/ folder.
If using Maven, it will automatically download the required dependencies.

###✅ **5. Run the Project**
For a Java Servlet project:
1. Deploy it in Apache Tomcat.
2. Start Tomcat Server.
3. Access the app at http://localhost:8080/TaskManagementSystem/.

###💡 Future Improvements
✅ Add task deadlines.
✅ Implement email notifications for task updates.
✅ Introduce task priority levels (High, Medium, Low).





