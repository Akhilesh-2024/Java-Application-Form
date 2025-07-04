# Java Swing Admin System

<p align="center">
  A secure desktop application with user authentication and admin management
</p>

<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Swing-0176C6?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white" />
  <img src="https://img.shields.io/badge/JDBC-007396?style=for-the-badge&logo=java&logoColor=white" />
</div>

## Preview

![Application Screenshot](demo.gif) *(replace with your actual screenshot)*

## Default Credentials

| Role       | Username       | Password  |
|------------|----------------|-----------|
| Admin      | `admin`        | `admin123`|

## Key Features

- **Secure Authentication System**
  - Role-based access (Admin/User)
  - Password protection
  - Session management

- **Admin Dashboard**
  - View all registered users
  - Add new user accounts
  - Edit existing user details
  - Delete user accounts
  - Reset user passwords

- **Database Integration**
  - Embedded SQLite database
  - JDBC connectivity
  - Automatic schema creation

- **User Management**
  - Self-registration
  - Profile management
  - Secure login/logout

## Project Structure

<pre>
src/
├── main/
│   ├── java/
│   │   ├── controllers/        # Event handlers
│   │   ├── models/            # Data models
│   │   ├── views/             # UI components
│   │   ├── database/          # DB connection & queries
│   │   └── App.java           # Main entry point
│   └── resources/             # Configuration files
└── test/                      # Unit tests
</pre>

## Getting Started

1. **Prerequisites**
   - Java JDK 11+
   - Maven (for building)

2. **Installation**
   ```bash
   git clone https://github.com/yourusername/java-swing-admin.git
   cd java-swing-admin
   mvn clean install
   ```

3. **Running the Application**
   ```bash
   java -jar target/admin-system.jar
   ```

## Configuration

Edit `src/main/resources/config.properties` to customize:
- Database path
- Default admin credentials
- Application settings

## Dependencies

- SQLite JDBC Driver
- JUnit (for testing)
- Apache Commons (for utilities)

## License

This project is licensed under the MIT License.

---

<div align="center">
  <sub>Built with ❤︎ by Akhilesh Jadhav</sub>
</div>
