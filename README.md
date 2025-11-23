# 📚 Library Catalog

A web application for managing a library’s catalog — including books, authors, genres, and user borrowing history.  
Built as part of my portfolio to demonstrate full-stack and DevOps skills.  
🎓 Graduating May 2026 · Focused on DevOps, Cloud, and Automation.

---

## 🚀 Features

- CRUD operations for **Books**, **Authors**, **Genres**, and **Users**
- Secure **user authentication** with role-based access (admin vs. user)
- **Search** and filter capabilities for books by title, author, or genre
- Borrowing system: users can **check out** and **return** books
- **Dockerized** application for containerized deployment
- **CI/CD pipeline** using GitHub Actions (build → test → deploy)
- Ready for **cloud deployment** (tested on Render / Heroku)
- Responsive, minimal frontend interface

---

## 🧮 Tech Stack

| Layer | Technologies |
|--------|---------------|
| **Backend** | Java · Spring Boot |
| **Frontend** | HTML · CSS |
| **Database** | H2 / SQLite |
| **Containerization** | Docker |
| **CI/CD** | GitHub Actions |
| **Hosting** | Render |
| **Version Control** | Git · GitHub |

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- Maven
- Docker (optional)

### 🖥️ Run Locally

```bash
# Clone the repository
git clone https://github.com/hritikmunde/library_catalog.git
cd library_catalog

# Build and run
./mvnw clean install
./mvnw spring-boot:run
```

Visit **http://localhost:8080** in your browser.

---

### 🛣️ Run with Docker

```bash
# Build Docker image
docker build -t library-catalog .

# Run the container
docker run -p 8080:8080 library-catalog
```

---

## 🧩 Folder Structure

```
library_catalog/
├── src/main/java/com/example/library   # Core backend code
│   ├── controller
│   ├── service
│   └── repository
├── src/main/resources/                 # Configuration files
│   └── application.properties
├── Dockerfile                          # Container build instructions
├── .github/
│   └── workflows/
│       └── ci-cd.yml                   # CI/CD pipeline definition
└── pom.xml                             # Maven dependencies
```

---

## ✅ What I Learned

- Structuring a **Spring Boot monolithic app** with a clean architecture (Controller → Service → Repository)
- **Containerizing** Java applications with Docker for portability
- Writing **CI/CD workflows** in GitHub Actions to automate builds and deployments
- Managing **environment-specific configurations**
- Using GitHub for **version control and collaboration**

---

## 🔧 Future Improvements

- Split into **microservices** (Book, User, Borrowing)
- Add **Terraform / AWS ECS** for Infrastructure-as-Code deployment
- Integrate **monitoring and logging** (Prometheus · Grafana · ELK)
- Add **frontend framework** (React / Vue) for a richer UI
- Expand test coverage (unit, integration, e2e)

---

## 📸 Screenshots

*(Add screenshots or architecture diagrams here — e.g., app UI, CI/CD pipeline screenshots, Docker setup, etc.)*

---

## 📥 Connect with Me

**👨‍💻 Hritik Munde**  
🔗 [LinkedIn](https://www.linkedin.com/in/hritik-munde-922b43183/)  
💻 [GitHub](https://github.com/hritikmunde)  
🎓 *Graduating May 2026 — seeking DevOps / Cloud / Site Reliability Engineer roles.*

---

## ⭐ Acknowledgements

Special thanks to open-source DevOps and Java communities for documentation and examples that inspired this project.

---

**Thank you for checking out this project!**  
If you have feedback or collaboration ideas, feel free to open an issue or connect with me on LinkedIn.
